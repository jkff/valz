package org.valz.util;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.ParserException;

import java.util.Map;
import java.util.TreeMap;

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

    public static <T> JSONValue makeJson(Map<String, T> map, JsonFormatter<T> formatter) {
        JSONObject obj = new JSONObject();
        Map<String, JSONValue> jsonMap = obj.getValue();
        for (Map.Entry<String, T> entry : map.entrySet()) {
            jsonMap.put(entry.getKey(), formatter.toJson(entry.getValue()));
        }
        return obj;
    }

    public static <T> Map<String, T> fromJson(JSONValue jsonValue, JsonParser<T> parser)  throws
            ParserException {
        JSONObject obj = (JSONObject)jsonValue;
        Map<String, JSONValue> jsonMap = obj.getValue();
        Map<String, T> map = new TreeMap<String, T>();
        for (Map.Entry<String, JSONValue> entry : jsonMap.entrySet()) {
            map.put(entry.getKey(), parser.fromJson(entry.getValue()));
        }
        return map;
    }
}
