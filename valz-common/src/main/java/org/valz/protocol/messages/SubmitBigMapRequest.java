package org.valz.protocol.messages;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.model.*;
import org.valz.util.ParserException;

import java.util.Map;
import java.util.TreeMap;

import static org.valz.util.JsonUtils.makeJson;
import static org.valz.util.CollectionUtils.*;

public class SubmitBigMapRequest<T> {
    public static <T> SubmitBigMapRequest<T> fromJson(AggregateRegistry aggregateRegistry,
                                                            JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        Map<String, JSONValue> jsonMap = jsonObject.getValue();
        String name = ((JSONString)jsonMap.get("name")).getValue();
        Aggregate<T> aggregate = AggregateFormat.fromJson(aggregateRegistry, jsonMap.get("aggregate"));


        JSONObject jsonMapObject = (JSONObject)jsonMap.get("value");
        TreeMap<String, T> map = new TreeMap<String, T>();
        for (Map.Entry<String, JSONValue> entry : jsonMapObject.getValue().entrySet()) {
            map.put(entry.getKey(), aggregate.dataFromJson(entry.getValue()));
        }

        return new SubmitBigMapRequest(name, aggregate, map);
    }


    public String getName() {
        return name;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }

    public Map<String, T> getValue() {
        return value;
    }

    private final String name;
    private final Aggregate<T> aggregate;
    private final Map<String, T> value;

    public SubmitBigMapRequest(String name, Aggregate<T> aggregate, Map<String, T> value) {
        this.name = name;
        this.aggregate = aggregate;
        this.value = value;
    }

    public JSONValue toJson(AggregateRegistry aggregateRegistry) {
        JSONObject mapObj = new JSONObject();
        Map<String, JSONValue> map = mapObj.getValue();
        for (Map.Entry<String, T> entry : value.entrySet()) {
            map.put(entry.getKey(), aggregate.dataToJson(entry.getValue()));
        } 
        return makeJson(ar("name", "aggregate", "value"),
                ar(name, AggregateFormat.toJson(aggregateRegistry, aggregate), mapObj));
    }
}