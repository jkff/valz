package org.valz.util.aggregates;

import com.sdicons.json.model.JSONValue;

public interface AggregateParser<T> {
    Aggregate<T> parse(JSONValue json) throws ParserException;
}
