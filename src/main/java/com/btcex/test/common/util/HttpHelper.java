package com.btcex.test.common.util;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HttpHelper {

    private static final Logger log = LoggerFactory.getLogger(HttpHelper.class);


    public static final String default_charset = "UTF-8";
    public static final Integer default_timeout = 3000;
    public static final Integer default_socket = 3000;


    public static String deGet(String url) {
        return doGet(url, null, null);
    }

    public static String doGet(String url, Map<String, String> params, Map<String, String> heades) {
        return doGet(url, params, heades, default_charset);

    }

    /**
     * Http GET 请求
     *
     * @param url
     * @param params
     * @param charset
     * @return
     */
    public static String doGet(String url, Map<String, String> params, Map<String, String> headers, String charset) {

        try {


            log.debug("HttpHelper doGet URL [{}]", url);
            String requestUrl = "";
            if (null == params) {
                requestUrl = url;
            } else {
                Set<Map.Entry<String, String>> entries = params.entrySet();
                StringBuffer buffer = new StringBuffer();
                for (Map.Entry<String, String> map : entries) {
                    buffer.append(map.getKey()).append("=").append(map.getValue()).append("&");
                }
                requestUrl = url + "?" + buffer.toString();
                log.debug("HttpGet params: [{}]", buffer.toString());
            }

            HttpGet httpGet = new HttpGet(requestUrl);

            if (headers != null) {
                headers.forEach((key, value) -> {
                    httpGet.setHeader(key, value);
                });
            }
            httpGet.setConfig(getConfig());
            return doexcete(httpGet, charset);
        } catch (IOException e) {
            log.error("HttpHelper doGet Exception", e);
        } finally {

        }
        return null;
    }

    public static String doPostForm(String url) {
        return doPostForm(url, null);
    }

    public static String doPostForm(String url, Map<String, String> data) {
        return doPostForm(url, data, default_charset);
    }

    /**
     * doPost form 表单提交
     *
     * @param url
     * @param data
     * @param charset
     * @return
     */
    public static String doPostForm(String url, Map<String, String> data, String charset) {

        try {
            log.debug("HttpHelper doPostForm URL [{}]", url);

            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(getConfig());

            if (null == data) {

            } else {
                log.debug("HttpHelper doPostForm params [{}]", data.toString());
                List<NameValuePair> list = new ArrayList<>();
                Set<Map.Entry<String, String>> entries = data.entrySet();
                for (Map.Entry<String, String> map : entries) {
                    list.add(new BasicNameValuePair(map.getKey(), map.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(list, charset));
            }
            return doexcete(httpPost, charset);
        } catch (Exception e) {
            log.error("HttpHelper doPostForm Exception", e);
        }
        return null;
    }

    public static String doPostJson(String url, String jsonString) {
        return doPostJson(url, jsonString, default_charset);
    }

    public static String doPostJson(String url, String jsonString, Map<String, String> headers) {
        return doPostJson(url, jsonString, headers, default_charset);
    }

    /**
     * @param url
     * @param jsonString
     * @param charset
     * @return
     */
    public static String doPostJson(String url, String jsonString, String charset) {

        try {

            log.debug("HttpHelper doPostJson URL [{}]", url);
            log.debug("HttpHelper doPostJson params [{}]", jsonString);
            HttpPost httpPost = new HttpPost(url);

            httpPost.setConfig(getConfig());

            StringEntity entity = new StringEntity(jsonString.toString(), charset);
            entity.setContentEncoding(charset);
            entity.setContentType("application/json");

            httpPost.setEntity(entity);
            return doexcete(httpPost, charset);

        } catch (Exception e) {
            log.error("HttpHelper doPostJson Exception", e);
        }

        return null;
    }

    public static String doPostJson(String url, String jsonString, Map<String, String> headers, String charset) {

        try {

            log.debug("HttpHelper doPostJson URL [{}]", url);
            log.debug("HttpHelper doPostJson params [{}]", jsonString);
            HttpPost httpPost = new HttpPost(url);

            httpPost.setConfig(getConfig());
            if (headers != null) {
                headers.forEach((key, value) -> {
                    httpPost.setHeader(key, value);
                });
            }

            StringEntity entity = new StringEntity(jsonString.toString(), charset);
            entity.setContentEncoding(charset);
            entity.setContentType("application/json");

            httpPost.setEntity(entity);
            return doexcete(httpPost, charset);

        } catch (Exception e) {
            log.error("HttpHelper doPostJson Exception", e);
        }

        return null;
    }

    /**
     * @param url
     * @param params
     * @return
     */
    public static String doPostXml(String url, String params) {
        return doPostXml(url, params, default_charset);
    }

    /**
     * @param url
     * @param jsonString
     * @param charset
     * @return
     */
    public static String doPostXml(String url, String jsonString, String charset) {

        try {

            log.debug("HttpHelper doPostJson URL [{}]", url);
            log.debug("HttpHelper doPostJson params [{}]", jsonString);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(getConfig());

            StringEntity entity = new StringEntity(jsonString.toString(), charset);
            entity.setContentEncoding(charset);
            entity.setContentType("application/xml");

            httpPost.setEntity(entity);
            return doexcete(httpPost, charset);

        } catch (Exception e) {
            log.error("HttpHelper doPostJson Exception", e);
        }

        return null;
    }

    private static RequestConfig getConfig() {
        return RequestConfig.custom().setConnectTimeout(default_timeout).setSocketTimeout(default_socket).build();
    }

    private static String doexcete(HttpUriRequest httpPost, String charset) throws ParseException, IOException {
        CloseableHttpClient aDefault = HttpClients.createDefault();
        CloseableHttpResponse execute = aDefault.execute(httpPost);

        log.info("StatusLine : [{}]", execute.getStatusLine());


        String string = EntityUtils.toString(execute.getEntity(), charset);
        aDefault.close();
        return string;
    }


    private static HttpResponse doexcete2(HttpUriRequest httpPost, String charset) throws ParseException, IOException {
        CloseableHttpClient aDefault = HttpClients.createDefault();
        CloseableHttpResponse execute = aDefault.execute(httpPost);
        log.info("StatusLine : [{}]", execute.getStatusLine());
        String string = EntityUtils.toString(execute.getEntity(), charset);
        aDefault.close();
        return execute;

    }


    public static HttpResponse doPostJsonBody(String url, String jsonString, Map<String, String> headers) {

        try {

            log.debug("HttpHelper doPostJson URL [{}]", url);
            log.debug("HttpHelper doPostJson params [{}]", jsonString);
            HttpPost httpPost = new HttpPost(url);

            httpPost.setConfig(getConfig());
            if (headers != null) {
                headers.forEach((key, value) -> {
                    httpPost.setHeader(key, value);
                });
            }

            StringEntity entity = new StringEntity(jsonString.toString(), default_charset);
            entity.setContentEncoding(default_charset);
            entity.setContentType("application/json");

            httpPost.setEntity(entity);
            return doexcete2(httpPost, default_charset);

        } catch (Exception e) {
            log.error("HttpHelper doPostJson Exception", e);
        }

        return null;
    }

}