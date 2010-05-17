package org.valz.util.keytypes;

import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.ParserException;

public interface KeyTypeConfigFormatter<T extends KeyType<?>> {
    T fromJson(JSONValue jsonValue) throws ParserException;

    JSONValue toJson(T key);
}