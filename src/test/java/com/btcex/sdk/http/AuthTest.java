package com.btcex.sdk.http;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.btcex.sdk.common.dto.JsonRpcRequestParam;
import com.btcex.sdk.common.util.HttpHelper;
import com.btcex.sdk.common.util.SHAUtils;
import com.btcex.sdk.common.util.StringToHex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class AuthTest {

    private final static Logger log = LoggerFactory.getLogger(AuthTest.class);


    private static String url = "https://www.bitcharm.com/api/v1";

    private static String method = "/public/auth";

    private static String clientId = "28e91afa";


    private static String clientSecret = "8bf440448c1dac3978282eb8";

    @Test
    public void authTestByClientSignature() {

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

        jsonRpcRequestParam.setId("1");
        jsonRpcRequestParam.setMethod(method);


        Map<String, String> data = new HashMap<>();
        data.put("grant_type", grantType);
        data.put("client_id", clientId);
        data.put("nonce", nonce);
        data.put("timestamp", time + "");
        data.put("signature", signHex);


        jsonRpcRequestParam.setParams(data);

        String jsonString = JSONObject.toJSON(jsonRpcRequestParam).toString();

        String response = HttpHelper.doPostJson(url + method, jsonString);




        log.info("response:\n{}",  JSON.toJSONString(   JSONObject.parseObject(response), SerializerFeature.PrettyFormat));

    }

    @Test
    public void authTestByRefreshToken() {

        String grantType = "refresh_token";


        JsonRpcRequestParam jsonRpcRequestParam = new JsonRpcRequestParam();

        jsonRpcRequestParam.setId("1");
        jsonRpcRequestParam.setMethod(method);

        Map<String, String> data = new HashMap<>();
        data.put("grant_type", grantType);

        //
        data.put("refresh_token", "*******");



        jsonRpcRequestParam.setParams(data);

        String jsonString = JSONObject.toJSON(jsonRpcRequestParam).toString();

        String response = HttpHelper.doPostJson(url + method, jsonString);


        log.info("response:\n{}", response);





    }


}
