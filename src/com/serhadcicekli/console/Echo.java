package com.serhadcicekli.console;
public class Echo extends ConsoleProgram {
    @Override
    public void start(DataProvider provider) {
        programCallback.call(new DataProvider().set("kernel", true).set("method", "out").set("stream", "console").set("content", provider.str("arg1")));
        programCallback.call(new DataProvider().set("kernel", false).set("method", "end"));
    }

    @Override
    public void input(DataProvider provider) {

    }
}
