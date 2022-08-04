package com.btcex.test.ws;

import com.alibaba.fastjson.JSONObject;
import com.btcex.test.common.dto.JsonRpcRequestParam;
import com.btcex.test.common.util.SHAUtils;
import com.btcex.test.common.util.StringToHex;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class WsTest {


    public static final Logger log = LoggerFactory.getLogger(WsTest.class);


    public static final AtomicInteger atomicInteger = new AtomicInteger();

    @Test
    public void simplenessTest() throws SSLException, InterruptedException {


        WebSocketClient webSocketClient = new WebSocketClient("wss://api.btcex.com/ws/api/v1");

        webSocketClient.create();

        Channel channel = webSocketClient.getChannel();

        MessageHandler messageHandler = webSocketClient.getMessageHandler();

        messageHandler.setChannel(channel);

        boolean open = false;

        do {
            open = channel.isOpen();
            Thread.sleep(1000);
            //Just call it once
            log.info("auth");
            if (open) this.clientSignatureAuth(channel);

        } while (!open);


        while (true) {
            Thread.sleep(1000);
            channel.writeAndFlush(new TextWebSocketFrame("PING"));
        }

    }


    private static String clientId = "28e91afa";
    private static String clientSecret = "***************";

    private void clientSignatureAuth(Channel channel) {

        String method = "/public/auth";

        String grantType = "client_signature";

        long time = new Date().getTime();

        String nonce = UUID.randomUUID().toString();

        String source = new StringBuffer()
                .append(clientId).append("\n")
                .append(time).append("\n")
                .append(nonce).append("\n")
                .toString();

        String sign = SHAUtils.sha256_HMAC(source, clientSecret);

        String signHex = StringToHex.stringToHexString(sign);


        JsonRpcRequestParam jsonRpcRequestParam = new JsonRpcRequestParam();

        //This ID is the same as response's
        jsonRpcRequestParam.setId(atomicInteger.incrementAndGet() + "");
        jsonRpcRequestParam.setMethod(method);


        Map<String, String> data = new HashMap<>();
        data.put("grant_type", grantType);
        data.put("client_id", clientId);
        data.put("nonce", nonce);
        data.put("timestamp", time + "");
        data.put("signature", signHex);


        jsonRpcRequestParam.setParams(data);

        String jsonString = JSONObject.toJSON(jsonRpcRequestParam).toString();


        channel.writeAndFlush(new TextWebSocketFrame(jsonString));


    }


    public void refreshTokenAuth(Channel channel) {


        String grantType = "refresh_token";

        String method = "/public/auth";

        JsonRpcRequestParam jsonRpcRequestParam = new JsonRpcRequestParam();

        jsonRpcRequestParam.setId("1");
        jsonRpcRequestParam.setMethod(method);

        Map<String, String> data = new HashMap<>();
        data.put("grant_type", grantType);

        //
        data.put("refresh_token", "*******");


        jsonRpcRequestParam.setParams(data);

        String jsonString = JSONObject.toJSON(jsonRpcRequestParam).toString();

        channel.writeAndFlush(new TextWebSocketFrame(jsonString));
    }


}
