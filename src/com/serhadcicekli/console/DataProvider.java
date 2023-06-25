package com.serhadcicekli.console;

import java.util.HashMap;

public class DataProvider {
    protected HashMap<String, Object> parameters = new HashMap<>();
    public DataProvider set(String name, Object obj){
        parameters.put(name, obj);
        return this;
    }
    public DataProvider reflect(DataProvider provider){
        provider.parameters.entrySet().forEach(stringObjectEntry -> set(stringObjectEntry.getKey(),stringObjectEntry.getValue()));
        return this;
    }
    public Object get(String name){
        Object obj = parameters.get(name);
        return obj==null?null:obj;
    }
    public boolean exists(String name){
        return parameters.containsKey(name);
    }
    public String str(String name){
        return (String)get(name);
    }
    public int itg(String name){
        return (Integer)get(name);
    }
    public float flt(String name){
        return (Float)get(name);
    }
    public double dbl(String name){
        return (Double)get(name);
    }
    public boolean bol(String name){
        return (Boolean)get(name);
    }
}
