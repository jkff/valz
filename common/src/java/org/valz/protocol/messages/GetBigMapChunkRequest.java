package org.valz.protocol.messages;

import com.sdicons.json.model.*;
import org.valz.util.ParserException;

import java.math.BigInteger;
import java.util.Map;

import static org.valz.util.CollectionUtils.ar;
import static org.valz.util.JsonUtils.makeJson;

public class GetBigMapChunkRequest {
    public static GetBigMapChunkRequest fromJson(JSONValue json) throws
            ParserException {
        JSONObject jsonObject = (JSONObject)json;
        Map<String, JSONValue> jsonMap = jsonObject.getValue();

        String name = ((JSONString)jsonMap.get("name")).getValue();
        int count = ((JSONInteger)jsonMap.get("count")).getValue().intValue();
        JSONValue fromKeyJson = jsonMap.get("fromKey");

        String fromKey = (fromKeyJson instanceof JSONNull)
                ? null
                : ((JSONString)fromKeyJson).getValue();
        
        return new GetBigMapChunkRequest(name, fromKey, count);
    }

    public final String name;
    public final String fromKey;
    public final int count;

    /**
     *  Pass fromKey=null to get just the metadata (key type) for this name.
     */
    public GetBigMapChunkRequest(String name, String fromKey, int count) {
        this.name = name;
        this.fromKey = fromKey;
        this.count = count;
    }

    public JSONValue toJson() {
        JSONValue fromKeyJson = (fromKey == null)
                ? new JSONNull()
                : new JSONString(fromKey);
        return makeJson(ar("name", "fromKey", "count"),
                ar(new JSONString(name), fromKeyJson,
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
