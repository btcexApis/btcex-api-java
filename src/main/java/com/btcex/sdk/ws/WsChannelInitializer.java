package com.btcex.sdk.ws;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;

public class WsChannelInitializer extends ChannelInitializer<SocketChannel> {


    private final ClientHandler handler;

    public WsChannelInitializer(ClientHandler handler) {

        this.handler = handler;
    }


    @Override
    protected void initChannel(SocketChannel channel)  {

        ChannelPipeline p = channel.pipeline();
        p.addLast(new HttpClientCodec());
        p.addLast(new HttpObjectAggregator(8192));
        p.addLast(WebSocketClientCompressionHandler.INSTANCE);
        p.addLast(handler);





    }
}
