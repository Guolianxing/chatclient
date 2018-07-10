package com.chat.chatclient.util;

import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Create by Guolianxing on 2018/7/6.
 */
public class HttpUtil {

    public static JSONObject sendPost(String url, Map<String, String> params) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        if (params != null) {
            List<NameValuePair> list = new ArrayList<>();
            for (String key : params.keySet()) {
                String value = params.get(key);
                list.add(new BasicNameValuePair(key, value));
            }
            StringEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            httpPost.setEntity(entity);
        }
        CloseableHttpResponse response = httpClient.execute(httpPost);
        String string = EntityUtils.toString(response.getEntity());
        return JSONObject.fromObject(string);
    }

}
