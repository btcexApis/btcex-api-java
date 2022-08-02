package com.btcex.sdk.ws;

public class ClientException extends RuntimeException {


    public  ClientException (String message){
       new RuntimeException(message);
    }


}
