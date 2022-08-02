package com.btcex.sdk.ws;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WebSocketClient implements Closeable {


    private static final Logger log = LoggerFactory.getLogger(WebSocketClient.class);

    private final int connectionTimeout;

    private WsContext wsContext;

    private final URI uri;


    private final int port;


    private Bootstrap bootstrap;

   private ClientHandler handler;

    private Channel channel;


    public WebSocketClient(int connectionTimeout, String url, int port) throws URISyntaxException {
        this.connectionTimeout = connectionTimeout;
        this.uri = new URI(url);
        this.port = port;
        this.wsContext = new WsContext(new CountDownLatch(1));
    }


    /**
     * Create a connection
     *
     * @throws ClientException
     */
    public  void CreateConnect() throws ClientException{




    }


    private static final NioEventLoopGroup group = new NioEventLoopGroup();


    /**
     * init connection
     */
    public   void init(){

        // haker
        WebSocketClientHandshaker webSocketClientHandshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
        // handler
        handler = new ClientHandler(webSocketClientHandshaker, this.wsContext);
        //start
        bootstrap = new Bootstrap();
        //
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new WsChannelInitializer(handler));

    }


    public void connect() throws ClientException {
        try {
            init();
            CreateConnect();
        } catch (Exception e) {
            throw new ClientException ("Failed Connection :{}" + e.getMessage());
        }
    }





    /**
     * send message
     * @param message
     * @throws ClientException
     */
    public void send(String message) throws ClientException {
        Channel channel = getChannel();
        if (channel != null) {
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return;
        }
        throw new ClientException ("Connection is closed");
    }



    public String receiveResult() throws ClientException {
        this.receive(this.wsContext.getCountDownLatch());
        if (StringUtils.isEmpty(this.wsContext.getResult())) {
            throw new ClientException("The message is empty");
        }
        return this.wsContext.getResult();
    }



    /**
     * Receive message
     * @param countDownLatch
     * @throws ClientException
     */
    private void receive(CountDownLatch countDownLatch) throws ClientException {
        boolean waitFlag = false;
        try {
            waitFlag = countDownLatch.await(connectionTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.info("No response message was received for this connection");
            Thread.currentThread().interrupt();
        }
        if (!waitFlag) {
            log.error("Timeout({}}s) when receiving response message", connectionTimeout);
            throw new ClientException("timeout");
        }


    }



    protected  Channel getChannel(){

        return null;
    }


    @Override
    public void close() throws IOException {

    }
}
