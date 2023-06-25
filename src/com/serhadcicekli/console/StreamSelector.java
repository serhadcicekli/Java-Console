package com.serhadcicekli.console;
import java.util.ArrayList;
public class StreamSelector extends ConsoleProgram {
    ArrayList<String> streams = new ArrayList<>();
    ProgramCallback virtualCallback;
    ConsoleProgram program;
    private void parseStreamCode(String code){
        String temp = "";
        for(int i = 0;i < code.length(); i++){
            if(code.charAt(i) == '&'){
                if(temp.length() > 0){
                    streams.add(temp.trim());
                    temp = "";
                }
                continue;
            }
            temp += code.charAt(i);
        }
        if(temp.length() > 0){
            streams.add(temp.trim());
        }
    }
    @Override
    public void start(DataProvider provider) {
        String streamCode = provider.str("arg" + Integer.toString(provider.itg("argc") - 1));
        parseStreamCode(streamCode);
        provider.set("argc", provider.itg("argc") - 2);
        DataProvider callp = new DataProvider().set("kernel", false).set("method", "test-executors").set("provider", provider);
        programCallback.call(callp);
        Class<? extends ConsoleProgram> outProgram = (Class<? extends ConsoleProgram>)callp.get("out");
        if(outProgram == null){
            programCallback.call(new DataProvider().set("kernel", true).set("method", "out").set("stream", "console").set("content", "Unknown command: " + provider.str("input")));
        }else{
            try {
                program = outProgram.newInstance();
                virtualCallback = callbackProvider -> {
                    streams.forEach(strm -> programCallback.call(callbackProvider.set("stream", strm)));
                };
                program.programCallback = virtualCallback;
                program.start(provider);
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
        programCallback.call(new DataProvider().set("kernel", false).set("method", "end"));
    }

    @Override
    public void input(DataProvider provider) {

    }
}
