package org.valz.util;

import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.ParserException;

public interface JsonParser<T> {
    T fromJson(JSONValue jsonValue) throws ParserException;
}