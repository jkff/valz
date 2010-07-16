package org.valz.protocol.messages;

import com.sdicons.json.model.*;
import org.valz.aggregates.ParserException;
import org.valz.keytypes.KeyType;
import org.valz.keytypes.KeyTypeFormatter;
import org.valz.keytypes.KeyTypeRegistry;

import java.math.BigInteger;
import java.util.Map;

import static org.valz.util.CollectionUtils.ar;
import static org.valz.util.JsonUtils.makeJson;

public class GetBigMapChunkRequest<K> {
    public static <K> GetBigMapChunkRequest fromJson(KeyTypeRegistry keyTypeRegistry, JSONValue json) throws
            ParserException {
        JSONObject jsonObject = (JSONObject)json;
        Map<String, JSONValue> jsonMap = jsonObject.getValue();

        String name = ((JSONString)jsonMap.get("name")).getValue();
        int count = ((JSONInteger)jsonMap.get("count")).getValue().intValue();
        JSONValue jsonValueKeyType = jsonMap.get("keyType");

        if (jsonValueKeyType == null || jsonValueKeyType.isNull()) {
            return new GetBigMapChunkRequest<K>(name, null, count, null);
        } else {
            KeyType<K> keyType = KeyTypeFormatter.fromJson(keyTypeRegistry, jsonMap.get("keyType"));
            K fromKey = keyType.dataFromJson(jsonMap.get("fromKey"));
            return new GetBigMapChunkRequest<K>(name, fromKey, count, keyType);
        }
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
        if (keyType == null) {
            // this is first request of this iterator
            return makeJson(ar("name", "keyType", "fromKey", "count"),
                    ar(new JSONString(name), new JSONNull(), new JSONNull(),
                            new JSONInteger(new BigInteger(count + ""))));
        } else {
            return makeJson(ar("name", "keyType", "fromKey", "count"),
                    ar(new JSONString(name), KeyTypeFormatter.toJson(keyTypeRegistry, keyType),
                            keyType.dataToJson(fromKey), new JSONInteger(new BigInteger(count + ""))));
        }
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
