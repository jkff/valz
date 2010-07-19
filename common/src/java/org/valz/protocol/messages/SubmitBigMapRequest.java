package org.valz.protocol.messages;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.keytypes.KeyTypeFormat;
import org.valz.util.JsonUtils;
import org.valz.model.*;
import org.valz.keytypes.KeyType;
import org.valz.keytypes.KeyTypeRegistry;
import org.valz.util.ParserException;

import java.util.Map;
import java.util.TreeMap;

import static org.valz.util.JsonUtils.makeJson;
import static org.valz.util.CollectionUtils.*;

public class SubmitBigMapRequest<K, T> {

    public static <K, T> SubmitBigMapRequest<K, T> fromJson(KeyTypeRegistry keyTypeRegistry,
                                                            AggregateRegistry aggregateRegistry,
                                                            JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        Map<String, JSONValue> jsonMap = jsonObject.getValue();
        String name = ((JSONString)jsonMap.get("name")).getValue();
        KeyType<K> keyType = KeyTypeFormat.fromJson(keyTypeRegistry, jsonMap.get("keyType"));
        Aggregate<T> aggregate = AggregateFormat.fromJson(aggregateRegistry, jsonMap.get("aggregate"));


        JSONObject jsonMapObject = (JSONObject)jsonMap.get("value");
        TreeMap<K, T> map = new TreeMap<K, T>(keyType);
        for (Map.Entry<String, JSONValue> entry : jsonMapObject.getValue().entrySet()) {
            map.put(keyType.dataFromJson(JsonUtils.jsonFromString(entry.getKey())), aggregate.dataFromJson(entry.getValue()));
        }

        return new SubmitBigMapRequest(name, keyType, aggregate, map);
    }


    public String getName() {
        return name;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }

    public Map<K, T> getValue() {
        return value;
    }

    public KeyType<K> getKeyType() {
        return keyType;
    }
    
    private final String name;
    private final KeyType<K> keyType;
    private final Aggregate<T> aggregate;
    private final Map<K, T> value;


    public SubmitBigMapRequest(String name, KeyType<K> keyType, Aggregate<T> aggregate,
                               Map<K, T> value) {
        this.name = name;
        this.keyType = keyType;
        this.aggregate = aggregate;
        this.value = value;
    }

    public JSONValue toJson(KeyTypeRegistry keyTypeRegistry, AggregateRegistry aggregateRegistry) {

        JSONObject mapObj = new JSONObject();
        Map<String, JSONValue> map = mapObj.getValue();
        for (Map.Entry<K, T> entry : value.entrySet()) {
            map.put(keyType.dataToJson(entry.getKey()).render(false), aggregate.dataToJson(entry.getValue()));
        } 
        return makeJson(ar("name", "keyType", "aggregate", "value"),
                ar(name, KeyTypeFormat.toJson(keyTypeRegistry, keyType), AggregateFormat.toJson(aggregateRegistry, aggregate), mapObj));
    }
}