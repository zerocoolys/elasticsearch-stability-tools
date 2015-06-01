package com.ss.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dolphineor on 2015-5-28.
 */
public class MessageObject {

    // Elasticsearch source
    private Map<String, Object> attribute = new HashMap<>();

    {
        attribute.put("tt", UUID.randomUUID());
        attribute.put("fl", "17.0");// flash版本号
        attribute.put("v", "1.0");
        attribute.put("ja", "1");// 是否支持java 0.不支持, 1.支持
        attribute.put("ck", "1");// 是否支持cookie 0.不支持, 1.支持
        attribute.put("tit", "ddd");// 页面title文本信息
        attribute.put("Accept-Language", "zh-CN,zh;q=0.8");
        attribute.put("lg", "zh-CN");
        attribute.put("sc", "24-bit");
        attribute.put("sr", "1920x1080");
        attribute.put("method", "GET");
        attribute.put("Accept", "*/*");
        attribute.put("Connection", "keep-alive");
        attribute.put("version", "HTTP/1.1");
        attribute.put("sr", "1920x1080");
        attribute.put("sr", "1920x1080");
        attribute.put("accept-encoding", "gzip,deflate,sdch");
        attribute.put("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
    }

    public Map<String, Object> getAttribute() {
        return attribute;
    }

    public void add(String key, Object value) {
        attribute.put(key, value);
    }

    public Object get(String key) {
        return attribute.get(key);
    }
}
