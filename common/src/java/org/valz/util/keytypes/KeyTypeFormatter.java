package org.valz.util.keytypes;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.ParserException;

import java.util.Map;

import static org.valz.util.JsonUtils.makeJson;

public class KeyTypeFormatter {
    public static JSONValue toJson(KeyTypeRegistry registry, KeyType<?> aggregate) {
        KeyTypeConfigFormatter formatter = registry.get(aggregate.getName());
        return makeJson("name", aggregate.getName(), "config", formatter.toJson(aggregate));
    }

    public static KeyType fromJson(KeyTypeRegistry registry, JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        Map<String, JSONValue> map = jsonObject.getValue();
        String name = ((JSONString)map.get("name")).getValue();
        KeyTypeConfigFormatter configFormatter = registry.get(name);
        return configFormatter.fromJson(map.get("config"));
    }

    private KeyTypeFormatter() {
    }
}