package org.valz.util;

import com.sdicons.json.mapper.JSONMapper;
import com.sdicons.json.mapper.MapperException;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    private Utils() {}

    public static JSONValue makeJson(Object... collection) {
        if (collection.length % 2 != 0) throw new IllegalArgumentException("collection must have even size.");
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i=0; i<collection.length; i+=2) {
            map.put((String)collection[i], collection[i+1]);
        }
        try {
            return JSONMapper.toJSON(map);
        } catch (MapperException e) {
            throw new RuntimeException(e);
        }
    }
}
