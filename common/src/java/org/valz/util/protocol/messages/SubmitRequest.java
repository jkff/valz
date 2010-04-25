package org.valz.util.protocol.messages;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.AggregateParser;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.*;

import java.util.Map;

import static org.valz.util.Utils.makeJson;

public class SubmitRequest<T> {

    public static SubmitRequest parse(AggregateRegistry registry, JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        Map<String, JSONValue> map = jsonObject.getValue();
        String name = ((JSONString)map.get("name")).getValue();
        Aggregate aggregate = AggregateParser.parse(registry, map.get("aggregate"));
        Object value = aggregate.parseData(map.get("value"));

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

    public Object toJson() {
        return makeJson(
                "name", name,
                "aggregate", AggregateParser.toJson(aggregate),
                "value", aggregate.dataToJson(value));
    }
}
