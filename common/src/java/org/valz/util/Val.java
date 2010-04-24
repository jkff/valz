package org.valz.util;

import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.Aggregate;

import static org.valz.util.Utils.makeJson;

public class Val<T> {
    private Aggregate<T> aggregate;
    private T value;



    public Val(Aggregate<T> aggregate, T value) {
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
