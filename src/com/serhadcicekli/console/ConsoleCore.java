package com.serhadcicekli.console;

import java.util.ArrayList;
import java.util.HashMap;

public class ConsoleCore{
    private boolean ready = false;
    int minp, maxp, ssp;
    private void initDefaults(){
        addExecutor(new ConsoleProgram.Executor().setLinkedProgram(Echo.class).addTester(provider -> provider.str("arg0").toLowerCase().equals("echo")));
        addExecutor(new ConsoleProgram.Executor().setPriority(ssp).setLinkedProgram(StreamSelector.class).addTester(provider -> {
            if(provider.itg("argc") >= 3){
                return provider.str("arg" + Integer.toString(provider.itg("argc") -2)).equals(">>");
            }else{
                return false;
            }
        }));
        addExecutor(new ConsoleProgram.Executor().setLinkedProgram(Exit.class).addTester(provider -> provider.str("arg0").toLowerCase().equals("exit")));
    }
    public void init(ConsoleCallbackInterface callbackInterface, int minPriority, int maxPriority, int streamSelectorPriorityLimit){
        if(ready) return;
        minp = minPriority;
        maxp = maxPriority;
        ssp = streamSelectorPriorityLimit;
        directAddMap.put('\"', '\"');
        directAddMap.put('0', '\0');
        directAddMap.put('1', '\1');
        directAddMap.put('2', '\2');
        directAddMap.put('3', '\3');
        directAddMap.put('4', '\4');
        directAddMap.put('5', '\5');
        directAddMap.put('6', '\6');
        directAddMap.put('7', '\7');
        directAddMap.put('b', '\b');
        directAddMap.put('f', '\f');
        directAddMap.put('n', '\n');
        directAddMap.put('r', '\r');
        directAddMap.put('t', '\t');
        this.callbackInterface = callbackInterface;
        initDefaults();
        ready = true;
    }
    public void addExecutor(ConsoleProgram.Executor executor){
        executors.add(executor);
    }
    public interface ConsoleCallbackInterface{
        void call(DataProvider provider);
    }
    private ConsoleProgram currentProgram = null;
    private ArrayList<ConsoleProgram.Executor> executors = new ArrayList<>();
    private ArrayList<ArrayList<ConsoleProgram.Executor>> sortedExecutors = new ArrayList<>();
    private ConsoleCallbackInterface callbackInterface;
    private Object getObj(String obj){
        if(obj.equals("executors")){
            return executors;
        }
        return null;
    }
    protected void callFnc(DataProvider provider){
        if(!ready) return;
        if(provider.bol("kernel")){
            callbackInterface.call(provider);
        }else{
            String method = provider.str("method");
            if(method.equals("end")){
                currentProgram = null;
                return;
            }
            if(method.equals("get")){
                provider.set(provider.str("object"), getObj(provider.str("object")));
                return;
            }
            if(method.equals("test-executors")){
                provider.set("out", testExecutors((DataProvider)provider.get("provider")));
            }
        }
    }
    private void sortWithPriority(ArrayList<ConsoleProgram.Executor> executorList){
        if(!ready) return;
        int minPriority = 0, maxPriority = 100;
        for(int i = 0;i < executorList.size();i++){
            if(executorList.get(i).priority > maxPriority){
                maxPriority = executorList.get(i).priority;
            }
            if(executorList.get(i).priority < minPriority){
                minPriority = executorList.get(i).priority;
            }
        }
        ArrayList<ArrayList<ConsoleProgram.Executor>> rawArray = new ArrayList<>();
        for(int i = minPriority;i <= maxPriority;i++){
            rawArray.add(new ArrayList<>());
        }
        for(int i = 0;i < executorList.size();i++){
            rawArray.get(executorList.get(i).priority - minPriority).add(executorList.get(i));
        }
        executorList.clear();
        for(int i = 0;i < rawArray.size(); i++){
            for(int j = 0;j < rawArray.get(i).size();j++){
                executorList.add(rawArray.get(i).get(j));
            }
        }
    }
    private Class<? extends ConsoleProgram> testExecutors(DataProvider provider){
        if(!ready) return null;
        sortWithPriority(executors);
        for(int i = executors.size() - 1;i >= 0;i--){
            if(executors.get(i).test(provider))
                return executors.get(i).programToExecute;
        }
        return null;
    }
    private HashMap<Character, Character> directAddMap = new HashMap<>();
    private char getDirectAddCharacter(char a) throws ParsingException{
        Character b = directAddMap.get(a);
        if(b == null) throw new ParsingException();
        return b;
    }
    public ConsoleCore(){

    }
    public static class ParsingException extends Exception{};
    private DataProvider parseString(String text) throws ParsingException{
        if(!ready) return null;
        ArrayList<String> args = new ArrayList<>();
        String temp = "";
        boolean noSplit = false;
        boolean directAdd = false;
        for(int i = 0; i < text.length(); i++){
            char current = text.charAt(i);
            if(directAdd){
                try {
                    temp += getDirectAddCharacter(current);
                }catch (ParsingException e){
                    args.clear();
                    throw e;
                }
                directAdd = false;
                continue;
            }else{
                if(current == '\\'){
                    directAdd = true;
                    continue;
                }
            }
            if(current == '"'){
                noSplit = !noSplit;
                continue;
            }
            if(noSplit){
                temp += current;
            }else{
                if(current == ' '){
                    if(temp.length() > 0){
                        args.add(temp);
                        temp = "";
                    }
                    continue;
                }
                temp += current;
            }
        }
        if(temp.length() > 0){
            args.add(temp);
        }
        if(noSplit){
            args.clear();
            throw new ParsingException();
        }
        if(directAdd){
            args.clear();
            throw new ParsingException();
        }
        DataProvider provider = new DataProvider().set("input", text).set("argc", args.size());
        for(int i = 0; i < args.size(); i++){
            provider.set("arg" + Integer.toString(i), args.get(i));
        }
        return provider;
    }
    public void input(String command){
        if(!ready) return;
        DataProvider provider;
        try {
            provider = parseString(command);
        } catch (ParsingException e) {
            return;
        }
        if(currentProgram == null){
            Class<? extends ConsoleProgram> programToExecute = testExecutors(provider);
            if(programToExecute == null){
                callFnc(new DataProvider().set("kernel", true).set("method", "out").set("stream", "console").set("content", "Unknown command: " + provider.str("input")));
            }else{
                try {
                    currentProgram = programToExecute.newInstance();
                } catch (InstantiationException e) {
                    return;
                } catch (IllegalAccessException e) {
                    return;
                }
                currentProgram.programCallback = provider1 -> callFnc(provider1);
                currentProgram.start(provider);
            }
        }else{
            currentProgram.input(provider);
        }
    }
}
