package org.valz.keytypes;

import com.sdicons.json.model.JSONValue;
import org.valz.aggregates.ParserException;

public interface KeyTypeConfigFormatter<T extends KeyType<?>> {
    T fromJson(JSONValue jsonValue) throws ParserException;

    JSONValue toJson(T key);
}