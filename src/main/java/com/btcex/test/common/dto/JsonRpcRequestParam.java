package com.btcex.test.common.dto;


import java.io.Serializable;


public class JsonRpcRequestParam<T> implements Serializable {


    private String jsonrpc = "2.0";

    private String id;

    private String method;

    private T params;


    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }
}
