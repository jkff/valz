package org.valz.util;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;
import org.jetbrains.annotations.NotNull;
import org.valz.util.aggregates.ParserException;

import java.io.StringReader;
import java.util.Map;
import java.util.TreeMap;

public class JsonUtils {
    private JsonUtils() {
    }

    public static <K, V> JSONValue makeJson(@NotNull String[] keys, @NotNull V[] values) {
        if (keys.length != values.length) {
            throw new IllegalArgumentException("keys length must be equals values length.");
        }

        JSONObject obj = new JSONObject();
        Map<String, JSONValue> map = obj.getValue();
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            Object value = values[i];

            map.put(key, (value instanceof JSONValue) ? (JSONValue)value : JSONValue.decorate(value));
        }
        return obj;
    }

    public static <T> JSONValue makeJson(Map<String, T> map, JsonFormatter<T> formatter) {
        JSONObject jsonObject = new JSONObject();
        Map<String, JSONValue> jsonMap = jsonObject.getValue();
        for (Map.Entry<String, T> entry : map.entrySet()) {
            jsonMap.put(entry.getKey(), formatter.toJson(entry.getValue()));
        }
        return jsonObject;
    }

    public static <T> Map<String, T> fromJson(JSONValue json, JsonParser<T> parser) throws ParserException {
        JSONObject obj = (JSONObject)json;
        Map<String, JSONValue> jsonMap = obj.getValue();
        Map<String, T> map = new TreeMap<String, T>();
        for (Map.Entry<String, JSONValue> entry : jsonMap.entrySet()) {
            map.put(entry.getKey(), parser.fromJson(entry.getValue()));
        }
        return map;
    }

    public static String jsonToString(JSONValue json) {
        return json.render(false);
    }

    public static JSONValue jsonFromString(String str) throws ParserException {
        try {
            return new JSONParser(new StringReader(str)).nextValue();
        } catch (TokenStreamException e) {
            throw new ParserException(e);
        } catch (RecognitionException e) {
            throw new ParserException(e);
        }
    }
}
