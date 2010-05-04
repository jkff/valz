package org.valz.util.aggregates;

import com.sdicons.json.model.JSONValue;

public interface AggregateConfigFormatter<T extends Aggregate<?>> {
    T fromJson(JSONValue jsonValue) throws ParserException;

    JSONValue toJson(T aggregate);
}
