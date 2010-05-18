package org.valz.util.keytypes;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.ParserException;

import java.util.Map;
import java.util.TreeMap;

import static org.valz.util.CollectionUtils.ar;
import static org.valz.util.JsonUtils.makeJson;

public class KeyTypeFormatter {
    public static JSONValue toJson(KeyTypeRegistry keyTypeRegistry, KeyType<?> keyType) {
        KeyTypeConfigFormatter formatter = keyTypeRegistry.get(keyType.getName());
        return makeJson(ar("name", "config"), ar(keyType.getName(), formatter.toJson(keyType)));
    }

    public static String toJsonString(KeyTypeRegistry keyTypeRegistry, KeyType<?> keyType) {
        return toJson(keyTypeRegistry, keyType).render(false);
    }

    public static KeyType fromJson(KeyTypeRegistry keyTypeRegistry, JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        Map<String, JSONValue> jsonMap = jsonObject.getValue();
        Map<KeyType, JSONValue> map = new TreeMap<KeyType, JSONValue>();
        String name = ((JSONString)jsonMap.get("name")).getValue();
        KeyTypeConfigFormatter configFormatter = keyTypeRegistry.get(name);
        return configFormatter.fromJson(jsonMap.get("config"));
    }

    private KeyTypeFormatter() {
    }
}