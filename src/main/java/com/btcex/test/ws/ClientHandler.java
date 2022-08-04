package com.btcex.test.ws;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ChannelHandler.Sharable
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);


    private WebSocketClient client;
    private WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    public ClientHandler(WebSocketClient client, WebSocketClientHandshaker handshaker) {
        this.client = client;
        this.handshaker = handshaker;
    }





    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel in active");
        handshaker.handshake(ctx.channel());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel not in active, reBuild channel.");
        super.channelInactive(ctx);
    }







    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            Channel channel = ctx.channel();
            if (!handshaker.isHandshakeComplete()) {
                handshaker.finishHandshake(channel, (FullHttpResponse) msg);
                handshakeFuture.setSuccess();
                return;
            }
            if (msg instanceof TextWebSocketFrame) {
                TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) msg;

                client.getMessageHandler().onMsg(textWebSocketFrame.text());

            }
        } finally {
            ReferenceCountUtil.release(msg);
        }



    }





    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
}
