package org.valz.keytypes;

import com.sdicons.json.model.JSONValue;
import org.valz.aggregates.ParserException;

import java.util.Comparator;

public interface KeyType<K> extends Comparator<K> {
    String getName();

    K getMinValue();

    JSONValue dataToJson(K item);

    K dataFromJson(JSONValue jsonValue) throws ParserException;

    /**
     * Will be used if a val is registered several times, to check
     * if it is registered with the same KeyType. 
     */
    boolean equals(Object other);

    int hashCode();
}