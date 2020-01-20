package com.atypon.workflow;

import java.util.Map;

public class Context {

    private Map<String, Object> params;

    public Context(Map<String, Object> params) {
        this.params = params;
    }


    @SuppressWarnings("unchecked")
    public <T> T getParam(String paramName) {
        return (T) params.get(paramName);
    }

    public void put(String paramName, Object paramValue) {
        params.put(paramName, paramValue);
    }
}
