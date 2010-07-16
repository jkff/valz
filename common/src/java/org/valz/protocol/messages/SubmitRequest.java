package org.valz.protocol.messages;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.aggregates.*;

import java.util.Map;

import static org.valz.util.CollectionUtils.ar;
import static org.valz.util.JsonUtils.makeJson;

public class SubmitRequest<T> {

    public static SubmitRequest fromJson(AggregateRegistry aggregateRegistry, JSONValue jsonValue) throws
            ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        Map<String, JSONValue> map = jsonObject.getValue();
        String name = ((JSONString)map.get("name")).getValue();
        Aggregate aggregate = AggregateFormat.fromJson(aggregateRegistry, map.get("aggregate"));
        Object value = aggregate.dataFromJson(map.get("value"));

        return new SubmitRequest(name, aggregate, value);
    }


    public String getName() {
        return name;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }

    public T getValue() {
        return value;
    }

    private final String name;
    private final Aggregate<T> aggregate;
    private final T value;


    public SubmitRequest(String name, Aggregate<T> aggregate, T value) {
        this.name = name;
        this.aggregate = aggregate;
        this.value = value;
    }

    public JSONValue toJson(AggregateRegistry aggregateRegistry) {
        return makeJson(ar("name", "aggregate", "value"),
                ar(name, AggregateFormat.toJson(aggregateRegistry, aggregate), aggregate.dataToJson(value)));
    }
}
