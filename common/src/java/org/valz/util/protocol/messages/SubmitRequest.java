package org.valz.util.protocol.messages;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.AggregateParser;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateConfigParser;
import org.valz.util.aggregates.ParserException;

import static org.valz.util.Utils.makeJson;

public class SubmitRequest<T> {

    public static SubmitRequest parse(AggregateRegistry registry, JSONValue json) throws ParserException {
        JSONObject jsonObject = (JSONObject)json;
        String name = ((JSONString)jsonObject.get("name")).getValue();
        Aggregate aggregate = AggregateParser.parse(registry, jsonObject.get("aggregate"));
        Object value = aggregate.parseData(jsonObject.get("value"));

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

    public JSONValue toJson() {
        return makeJson(
                "name", name,
                "aggregate", AggregateParser.toJson(aggregate),
                "value", aggregate.dataToJson(value));
    }
}
