package org.valz.util.keytypes;

import com.sdicons.json.model.JSONInteger;
import com.sdicons.json.model.JSONNull;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.ParserException;

import java.math.BigInteger;

public class KeyLong implements KeyType<Long> {
    public static final String NAME = "KeyLong";

    public String getName() {
        return NAME;
    }

    public Long getMinValue() {
        return Long.MIN_VALUE;
    }

    public JSONValue dataToJson(Long item) {
        return new JSONInteger(new BigInteger(item + ""));
    }

    public Long dataFromJson(JSONValue json) throws ParserException {
        return ((JSONInteger)json).getValue().longValue();
    }

    public int compare(Long s1, Long s2) {
        return s1.compareTo(s2);
    }

    public boolean equals(Object other) {
        return (other != null) && (other instanceof KeyLong);
    }

    public int hashCode() {
        return 0;
    }

    public static class ConfigFormatter implements KeyTypeConfigFormatter<KeyLong> {
        public KeyLong fromJson(JSONValue jsonValue) {
            return new KeyLong();
        }

        public JSONValue toJson(KeyLong key) {
            return new JSONNull();
        }
    }
}