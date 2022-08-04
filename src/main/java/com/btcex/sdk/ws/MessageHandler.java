package com.btcex.sdk.ws;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);





    private Channel channel;

    public  void onMsg(String text){

        //TODO
        log.info("message:{}",text);

    }


    public  void send(String message){



    }



    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
