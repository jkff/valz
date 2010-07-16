package org.valz.protocol.messages;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;
import org.valz.util.JsonUtils;
import org.valz.aggregates.*;
import org.valz.keytypes.KeyType;
import org.valz.keytypes.KeyTypeFormatter;
import org.valz.keytypes.KeyTypeRegistry;

import java.util.Map;
import java.util.TreeMap;

import static org.valz.util.CollectionUtils.ar;
import static org.valz.util.JsonUtils.makeJson;

public class BigMapChunkValue<K, T> {

    public static <K, T> BigMapChunkValue<K, T> fromJson(KeyTypeRegistry keyTypeRegistry,
                                                         AggregateRegistry aggregateRegistry,
                                                         JSONValue json) throws ParserException {
        JSONObject jsonObject = (JSONObject)json;
        Map<String, JSONValue> jsonMap = jsonObject.getValue();
        final Aggregate<T> aggregate =
                AggregateFormat.fromJson(aggregateRegistry, jsonMap.get("aggregate"));
        final KeyType<K> keyType = KeyTypeFormatter.fromJson(keyTypeRegistry, jsonMap.get("keyType"));

        Map<String, JSONValue> jsonValue = ((JSONObject)jsonMap.get("value")).getValue();
        Map<K, T> value = new TreeMap<K, T>(keyType);


        for (Map.Entry<String, JSONValue> entry : jsonValue.entrySet()) {
            value.put(keyType.dataFromJson(JsonUtils.jsonFromString(entry.getKey())),
                    aggregate.dataFromJson(entry.getValue()));
        }

        return new BigMapChunkValue<K, T>(keyType, aggregate, value);
    }

    private final KeyType<K> keyType;
    private final Aggregate<T> aggregate;
    private final Map<K, T> value;

    public BigMapChunkValue(KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> value) {
        this.keyType = keyType;
        this.aggregate = aggregate;
        this.value = value;
    }


    public KeyType<K> getKeyType() {
        return keyType;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }

    public Map<K, T> getValue() {
        return value;
    }

    public JSONValue toJson(KeyTypeRegistry keyTypeRegistry, AggregateRegistry aggregateRegistry) {

        JSONObject jsonObject = new JSONObject();
        Map<String, JSONValue> jsonMap = jsonObject.getValue();
        for (Map.Entry<K, T> entry : value.entrySet()) {
            jsonMap.put(keyType.dataToJson(entry.getKey()).render(false),
                    aggregate.dataToJson(entry.getValue()));
        }

        return makeJson(ar("keyType", "aggregate", "value"),
                ar(KeyTypeFormatter.toJson(keyTypeRegistry, keyType),
                        AggregateFormat.toJson(aggregateRegistry, aggregate), jsonObject));
    }
}