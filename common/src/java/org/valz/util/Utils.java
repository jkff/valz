package org.valz.util;

import com.sdicons.json.mapper.JSONMapper;
import com.sdicons.json.mapper.MapperException;
import com.sdicons.json.mapper.helper.impl.MapMapper;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    private Utils() {}

    public static Object makeJson(Object... collection) {
        if (collection.length % 2 != 0) {
            throw new IllegalArgumentException("collection must have even size.");
        }

        Map<String, Object> map = new HashMap<String, Object>();
        for (int i=0; i<collection.length; i+=2) {
            if (!(collection[i] instanceof String)) {
                throw new IllegalArgumentException("key must be String.");
            }
            if (map.containsKey((String)collection[i])) {
                throw new IllegalArgumentException("key must be unique.");
            }
            map.put((String)collection[i], collection[i+1]);
        }
        return map;
    }
}
