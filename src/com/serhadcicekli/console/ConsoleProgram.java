package com.serhadcicekli.console;
import java.util.ArrayList;
public abstract class ConsoleProgram{
    protected ProgramCallback programCallback;
    protected ArrayList<ExecutorTester> testers = new ArrayList<>();
    protected void print(String message){
        programCallback.call(new DataProvider().set("kernel", true).set("method", "out").set("stream", "console").set("content", message));
    }
    protected void end(){
        programCallback.call(new DataProvider().set("kernel", false).set("method", "end"));
    }
    public interface ExecutorTester{

        boolean test(DataProvider provider);
    }
    public interface ProgramCallback{
        void call(DataProvider provider);
    }
    public static final Executor firstArgumentEquals(String a, Class<? extends ConsoleProgram> toexec){
        return new Executor().setLinkedProgram(toexec).addTester(provider -> provider.str("arg0").toLowerCase().equals(a.toLowerCase()));
    }
    public static class Executor{
        protected Class<? extends ConsoleProgram> programToExecute;
        protected int priority = 0;
        private ArrayList<ExecutorTester> testers = new ArrayList<>();

        public Executor addTester(ExecutorTester tester){
            testers.add(tester);
            return this;
        }
        public Executor setLinkedProgram(Class<? extends ConsoleProgram> programToExecute){
            this.programToExecute = programToExecute;
            return this;
        }
        public Executor setPriority(int priority){
            this.priority = priority;
            return this;
        }
        protected boolean test(DataProvider provider){
            for(int i = 0;i < testers.size();i++){if(testers.get(i).test(provider)) return true;} return false;
        }
    }
    public abstract void start(DataProvider provider);
    public abstract void input(DataProvider provider);
    protected static ConsoleProgram createNew(Class<? extends ConsoleProgram> program) throws InstantiationException, IllegalAccessException {
        try {
            return program.newInstance();
        }catch (Exception e){
            return null;
        }
    }
}
