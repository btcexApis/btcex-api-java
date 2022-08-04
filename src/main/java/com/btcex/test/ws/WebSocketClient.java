package com.btcex.test.ws;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.UnsupportedAddressTypeException;

public class WebSocketClient implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(WebSocketClient.class);


    private final String url;
    private  String host;
    private int port;

    private Bootstrap bootstrap;

    private NioEventLoopGroup worker;

    private Channel channel;


    private  MessageHandler messageHandler =  new MessageHandler();


    public Channel getChannel(){

        return channel;
    }


    public WebSocketClient(String url) {
        this.url = url;
    }




    public  void create() throws SSLException, InterruptedException {
        URI uri = URI.create(this.url);
        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        this.setPort(uri);
        this.setHost(uri);
        final SslContext sslCtx =  sslContext(scheme);

        bootstrap = new Bootstrap();
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.
                newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders());
        this.worker = new NioEventLoopGroup(1);
        bootstrap.group(worker).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        if (null != sslCtx) {
                            channelPipeline.addLast(sslCtx.newHandler(socketChannel.alloc(), host, port));
                        }
                        channelPipeline
                                .addLast(new HttpClientCodec(), new HttpObjectAggregator(8192))
                                .addLast(new ClientHandler(WebSocketClient.this,handshaker))
                        ;

                    }
                });


        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();



        channel = channelFuture.channel();

    }


    private SslContext sslContext(String scheme) throws SSLException {
        final SslContext sslCtx;
        if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
            throw new UnsupportedAddressTypeException();
        }
        final boolean ssl = "wss".equalsIgnoreCase(scheme);

        if (ssl) {
            sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }
        return sslCtx;
    }

    private void setHost(URI uri ) {

        this.host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
    }


    private void setPort(URI uri ) {

        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        this.host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        if (uri.getPort() == -1) {
            if ("http".equalsIgnoreCase(scheme) || "ws".equalsIgnoreCase(scheme)) {
                this.port = 80;
            } else if ("wss".equalsIgnoreCase(scheme)) {
                this.port = 443;
            } else {
                this.port = -1;
            }
        } else {
            this.port = uri.getPort();
        }
    }


    @Override
    public void close() throws IOException {

    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
}
