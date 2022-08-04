package com.btcex.test.http;

import com.alibaba.fastjson.JSONObject;
import com.btcex.test.common.dto.JsonRpcRequestParam;
import com.btcex.test.common.util.HttpHelper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RequestTest {

    private final static Logger log = LoggerFactory.getLogger(AuthTest.class);

    private static String url = "https://www.btcex.com/api/v1";

    private static String method = "/private/*";


    private static String token = "****";




    @Test
    public void privateRequestTest(){

        String jsonData = data();


        Map<String, String> headers = new HashMap<>();

        headers.put("Authorization","Bearer "+token);

        String response = HttpHelper.doPostJson(url + method, jsonData,headers);

        log.info("response:{}",response);



    }




    @Test
    public void publicRequestTest() {
        String jsonData = data();

        String response = HttpHelper.doPostJson(url + method, jsonData);

        log.info("response:{}",response);

    }


    private String data() {
        JsonRpcRequestParam jsonRpcRequestParam = new JsonRpcRequestParam();

        jsonRpcRequestParam.setId("1");
        jsonRpcRequestParam.setMethod(method);


        Map<String, String> data = new HashMap<>();
        data.put("key", "value");


        jsonRpcRequestParam.setParams(data);

        String jsonString = JSONObject.toJSON(jsonRpcRequestParam).toString();
        return jsonString;
    }

}
