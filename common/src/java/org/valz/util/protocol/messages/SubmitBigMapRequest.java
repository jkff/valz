package org.valz.util.protocol.messages;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateFormatter;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.ParserException;

import java.util.Map;
import java.util.TreeMap;

import static org.valz.util.JsonUtils.makeJson;

public class SubmitBigMapRequest<T> {

    public static <T> SubmitBigMapRequest<T> fromJson(AggregateRegistry registry, JSONValue jsonValue) throws
            ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        Map<String, JSONValue> jsonMap = jsonObject.getValue();
        String name = ((JSONString)jsonMap.get("name")).getValue();
        Aggregate aggregate = AggregateFormatter.fromJson(registry, jsonMap.get("aggregate"));


        JSONObject jsonMapObject = (JSONObject)jsonMap.get("value");
        TreeMap<String, T> map = new TreeMap<String, T>();
        for (Map.Entry<String, JSONValue> entry : jsonMapObject.getValue().entrySet()) {
            map.put(entry.getKey(), (T)aggregate.dataFromJson(entry.getValue()));
        }

        return new SubmitBigMapRequest(name, aggregate, map);
    }


    public String getName() {
        return name;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }

    public Map<String,T> getValue() {
        return value;
    }

    private final String name;
    private final Aggregate<T> aggregate;
    private final Map<String,T> value;


    public SubmitBigMapRequest(String name, Aggregate<T> aggregate, Map<String,T> value) {
        this.name = name;
        this.aggregate = aggregate;
        this.value = value;
    }

    public JSONValue toJson(AggregateRegistry registry) {

        JSONObject mapObj = new JSONObject();
        Map<String, JSONValue> map = mapObj.getValue();
        for (Map.Entry<String, T> entry : value.entrySet()) {
            map.put(entry.getKey(), aggregate.dataToJson(entry.getValue()));
        }

        return makeJson("name", name, "aggregate", AggregateFormatter.toJson(registry, aggregate), "value",
                mapObj);
    }
}