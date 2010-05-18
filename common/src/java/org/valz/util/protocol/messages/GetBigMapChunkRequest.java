package org.valz.util.protocol.messages;

import com.sdicons.json.model.JSONInteger;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.ParserException;
import org.valz.util.keytypes.KeyType;
import org.valz.util.keytypes.KeyTypeFormatter;
import org.valz.util.keytypes.KeyTypeRegistry;

import java.math.BigInteger;
import java.util.Map;

import static org.valz.util.CollectionUtils.ar;
import static org.valz.util.JsonUtils.makeJson;

public class GetBigMapChunkRequest<K> {
    public static GetBigMapChunkRequest fromJson(KeyTypeRegistry keyTypeRegistry, JSONValue json) throws
            ParserException {
        JSONObject jsonObject = (JSONObject)json;
        Map<String, JSONValue> jsonMap = jsonObject.getValue();

        String name = ((JSONString)jsonMap.get("name")).getValue();
        KeyType keyType = keyTypeRegistry.get(name).fromJson(jsonMap.get("keyType"));
        Object fromKey = keyType.dataFromJson(jsonMap.get("fromKey"));
        int count = ((JSONInteger)jsonMap.get("count")).getValue().intValue();

        return new GetBigMapChunkRequest(name, fromKey, count, keyType);
    }

    public final String name;
    public final K fromKey;
    public final int count;
    public final KeyType<K> keyType;

    public GetBigMapChunkRequest(String name, K fromKey, int count, KeyType<K> keyType) {
        this.name = name;
        this.fromKey = fromKey;
        this.count = count;
        this.keyType = keyType;
    }

    public JSONValue toJson(KeyTypeRegistry keyTypeRegistry) {
        return makeJson(ar("name", "keyType", "fromKey", "count"),
                ar(new JSONString(name), KeyTypeFormatter.toJson(keyTypeRegistry, keyType),
                        keyType.dataToJson(fromKey), new JSONInteger(new BigInteger(count + ""))));
    }

    @Override
    public int hashCode() {
        return (name == null ? 0 : name.hashCode()) ^ (fromKey == null ? 0 : fromKey.hashCode()) ^ count;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GetBigMapChunkRequest)) {
            return false;
        }
        GetBigMapChunkRequest that = (GetBigMapChunkRequest)o;
        if (this.name == null ? that.name != null : !this.name.equals(that.name)) {
            return false;
        }
        if (this.fromKey == null ? that.fromKey != null : !this.fromKey.equals(that.fromKey)) {
            return false;
        }
        return this.count == that.count;
    }

    @Override
    public String toString() {
        return String.format("[%s, %s, %d]", name, fromKey, count);
    }
}
