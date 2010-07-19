package org.valz.keytypes;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.ParserException;

import java.util.Map;

import static org.valz.util.CollectionUtils.ar;
import static org.valz.util.JsonUtils.makeJson;

public abstract class KeyTypeFormat<T extends KeyType<?>> {
    public abstract T dataFromJson(JSONValue jsonValue) throws ParserException;

    public abstract JSONValue dataToJson(T key);

    public static JSONValue toJson(KeyTypeRegistry keyTypeRegistry, KeyType<?> keyType) {
        KeyTypeFormat formatter = keyTypeRegistry.get(keyType.getName());
        return makeJson(ar("name", "config"), ar(keyType.getName(), formatter.dataToJson(keyType)));
    }

    public static KeyType fromJson(KeyTypeRegistry keyTypeRegistry, JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        Map<String, JSONValue> jsonMap = jsonObject.getValue();
        String name = ((JSONString)jsonMap.get("name")).getValue();
        KeyTypeFormat format = keyTypeRegistry.get(name);
        return format.dataFromJson(jsonMap.get("config"));
    }
}