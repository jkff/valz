package org.valz.util;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.ParserException;

import java.util.Map;

import static org.valz.util.Utils.makeJson;

public class Value<T> {

    public static Value parse(AggregateRegistry registry, Object json) throws ParserException {
        JSONObject jsonObject = (JSONObject)json;
        Map<String, JSONValue> map = jsonObject.getValue();
        Aggregate aggregate = AggregateParser.parse(registry, map.get("aggregate"));
        Object value = aggregate.parseData(map.get("value"));
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

    public Object toJson() {
        return makeJson(
                "aggregate", AggregateParser.toJson(aggregate),
                "value", aggregate.dataToJson(value));
    }
}
