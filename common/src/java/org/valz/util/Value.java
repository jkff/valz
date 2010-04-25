package org.valz.util;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.ParserException;

import static org.valz.util.Utils.makeJson;

public class Value<T> {

    public static Value parse(AggregateRegistry registry, JSONValue json) throws ParserException {
        JSONObject jsonObject = (JSONObject)json;
        Aggregate aggregate = AggregateParser.parse(registry, jsonObject.get("aggregate"));
        Object value = aggregate.parseData(jsonObject.get("value"));
        return new Value(aggregate, value);
    }

    private final Aggregate<T> aggregate;
    private final T value;



    public Value(Aggregate<T> aggregate, T value) {
        this.aggregate = aggregate;
        this.value = value;
    }



    public Aggregate<T> getAggregate() {
        return aggregate;
    }

    public T getValue() {
        return value;
    }

    public JSONValue toJson() {
        return makeJson(
                "aggregate", AggregateParser.toJson(aggregate),
                "value", aggregate.dataToJson(value));
    }
}
