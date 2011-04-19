package org.valz.protocol.messages;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;
import org.valz.model.*;
import org.valz.util.ParserException;

import java.util.Map;
import java.util.TreeMap;

import static org.valz.util.CollectionUtils.ar;
import static org.valz.util.JsonUtils.makeJson;

public class BigMapChunkValue<T> {
    private final Aggregate<T> aggregate;

    private final TreeMap<String, T> value;

    public BigMapChunkValue(Aggregate<T> aggregate, TreeMap<String, T> value) {
        this.aggregate = aggregate;
        this.value = value;
    }

    public TreeMap<String, T> getValue() {
        return value;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }

    public static <T> BigMapChunkValue<T> fromJson(
            AggregateRegistry aggregateRegistry, JSONValue json) throws ParserException
    {
        JSONObject jsonObject = (JSONObject)json;
        Map<String, JSONValue> jsonMap = jsonObject.getValue();
        final Aggregate<T> aggregate =
                AggregateFormat.fromJson(aggregateRegistry, jsonMap.get("aggregate"));

        Map<String, JSONValue> jsonValue = ((JSONObject)jsonMap.get("value")).getValue();
        TreeMap<String, T> value = new TreeMap<String, T>();

        for (Map.Entry<String, JSONValue> entry : jsonValue.entrySet()) {
            value.put(entry.getKey(), aggregate.dataFromJson(entry.getValue()));
        }

        return new BigMapChunkValue<T>(aggregate, value);
    }

    public JSONValue toJson(AggregateRegistry aggregateRegistry) {
        JSONObject jsonObject = new JSONObject();
        Map<String, JSONValue> jsonMap = jsonObject.getValue();
        for (Map.Entry<String, T> entry : value.entrySet()) {
            jsonMap.put(entry.getKey(), aggregate.dataToJson(entry.getValue()));
        }

        return makeJson(ar("aggregate", "value"),
                ar(AggregateFormat.toJson(aggregateRegistry, aggregate), jsonObject));
    }
}