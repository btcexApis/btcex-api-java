package com.btcex.sdk.ws;

import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);


    private final WebSocketClientHandshaker webSocketClientHandshaker;

    private WsContext wsContext;

    private Channel channel;

    public ClientHandler(WebSocketClientHandshaker webSocketClientHandshaker, WsContext wsContext) {

        this.webSocketClientHandshaker = webSocketClientHandshaker;

        this.wsContext = wsContext;


    }

    private ChannelPromise handshakeFuture;

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }



    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }




}
