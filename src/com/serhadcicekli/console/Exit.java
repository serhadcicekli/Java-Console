package com.serhadcicekli.console;

public class Exit extends ConsoleProgram{
    @Override
    public void start(DataProvider provider) {
        if(provider.itg("argc") == 1){
            programCallback.call(new DataProvider().set("kernel", true).set("method", "out").set("stream", "console").set("content", "Are you sure you want to exit?"));
        }else{
            if(provider.str("arg1").equals("-i")){
                programCallback.call(new DataProvider().set("kernel", true).set("method", "shutdown"));
                programCallback.call(new DataProvider().set("kernel", false).set("method", "end"));
            }else{
                programCallback.call(new DataProvider().set("kernel", true).set("method", "out").set("stream", "console").set("content", "Are you sure you want to exit?"));
            }
        }
    }

    @Override
    public void input(DataProvider provider) {
        if(provider.str("input").toLowerCase().equals("yes")){
            programCallback.call(new DataProvider().set("kernel", true).set("method", "shutdown"));
        }
        programCallback.call(new DataProvider().set("kernel", false).set("method", "end"));
    }
}
