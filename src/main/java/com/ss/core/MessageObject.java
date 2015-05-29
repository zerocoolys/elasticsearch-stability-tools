package com.ss.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dolphineor on 2015-5-28.
 */
public class MessageObject {

    // Elasticsearch source
    private Map<String, Object> attribute = new HashMap<>();

    public Map<String, Object> getAttribute() {
        return attribute;
    }

    public void add(String key, Object value) {
        attribute.put(key, value);
    }
}
