package org.valz.aggregates;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;

import java.util.Map;

import static org.valz.util.CollectionUtils.ar;
import static org.valz.util.JsonUtils.makeJson;

public class Sample<T> {
    public static Sample<?> fromJson(AggregateRegistry aggregateRegistry, JSONValue json) throws ParserException {
        JSONObject jsonObject = (JSONObject)json;
        Map<String, JSONValue> map = jsonObject.getValue();
        Aggregate aggregate = AggregateFormat.fromJson(aggregateRegistry, map.get("aggregate"));
        Object value = aggregate.dataFromJson(map.get("value"));
        return new Sample(aggregate, value);
    }

    private final Aggregate<T> aggregate;
    private final T value;

    public Sample(Aggregate<T> aggregate, T value) {
        this.aggregate = aggregate;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }

    public JSONValue toJson(AggregateRegistry aggregateRegistry) {
        return makeJson(ar("aggregate", "value"),
                ar(AggregateFormat.toJson(aggregateRegistry, aggregate), aggregate.dataToJson(value)));
    }
}
