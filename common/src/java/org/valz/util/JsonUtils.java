package org.valz.util;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;

import java.util.Map;

public class JsonUtils {
    private JsonUtils() {
    }

    public static JSONValue makeJson(Object... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("keyValuePairs must have even size.");
        }

        JSONObject obj = new JSONObject();
        Map<String, JSONValue> map = obj.getValue();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            String key = (String)keyValuePairs[i];
            Object value = keyValuePairs[i + 1];

            map.put(key, (value instanceof JSONValue) ? (JSONValue)value : JSONValue.decorate(value));
        }
        return obj;
    }
}
