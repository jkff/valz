package org.valz.protocol.messages;

import com.sdicons.json.model.*;
import org.valz.keytypes.KeyType;
import org.valz.keytypes.KeyTypeFormat;
import org.valz.keytypes.KeyTypeRegistry;
import org.valz.util.ParserException;

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
        JSONValue keyTypeJson = jsonMap.get("keyType");
        JSONValue fromKeyJson = jsonMap.get("fromKey");

        KeyType<K> keyType = keyTypeJson.isNull()
                ? null : KeyTypeFormat.fromJson(keyTypeRegistry, keyTypeJson);
        K fromKey = fromKeyJson.isNull()
                ? null : keyType.dataFromJson(fromKeyJson);
        
        return new GetBigMapChunkRequest<K>(name, keyType, fromKey, count);
    }

    public final String name;
    public final KeyType<K> keyType;
    public final K fromKey;
    public final int count;

    /**
     *  Pass keyType=null, fromKey=null to get just the metadata (key type) for this name.
     */
    public GetBigMapChunkRequest(String name, KeyType<K> keyType, K fromKey, int count) {
        if(keyType == null && fromKey != null)
            throw new IllegalArgumentException(
                    "keyType and fromKey must either both be null (for a metadata-only request), " +
                    "or both be non-null (for a chunk request)");
        this.name = name;
        this.keyType = keyType;
        this.fromKey = fromKey;
        this.count = count;
    }

    public JSONValue toJson(KeyTypeRegistry keyTypeRegistry) {
        JSONValue keyTypeJson = (keyType == null)
                ? new JSONNull()
                : KeyTypeFormat.toJson(keyTypeRegistry, keyType);
        JSONValue fromKeyJson = (fromKey == null)
                ? new JSONNull()
                : keyType.dataToJson(fromKey);
        return makeJson(ar("name", "keyType", "fromKey", "count"),
                ar(new JSONString(name), keyTypeJson, fromKeyJson,
                        new JSONInteger(new BigInteger(count + ""))));
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
