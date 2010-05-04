package org.valz.util.aggregates;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;

import java.util.Map;

import static org.valz.util.JsonUtils.makeJson;

public class Value<T> {
    public static Value fromJson(AggregateRegistry registry, JSONValue json) throws ParserException {
        JSONObject jsonObject = (JSONObject)json;
        Map<String, JSONValue> map = jsonObject.getValue();
        Aggregate aggregate = AggregateFormatter.fromJson(registry, map.get("aggregate"));
        Object value = aggregate.dataFromJson(map.get("value"));
        return new Value(aggregate, value);
    }

    private final Aggregate<T> aggregate;
    private final T value;

    public Value(Aggregate<T> aggregate, T value) {
        this.aggregate = aggregate;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }

    public JSONValue toJson(AggregateRegistry registry) {
        return makeJson("aggregate", AggregateFormatter.toJson(registry, aggregate), "value",
                aggregate.dataToJson(value));
    }
}