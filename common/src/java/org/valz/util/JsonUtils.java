package org.valz.util;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;
import org.jetbrains.annotations.NotNull;

import java.io.StringReader;
import java.util.Map;

public class JsonUtils {
    private JsonUtils() {
    }

    public static <V> JSONValue makeJson(@NotNull String[] keys, @NotNull V[] values) {
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
