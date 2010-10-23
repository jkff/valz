package org.valz.keytypes;

import com.sdicons.json.model.JSONInteger;
import com.sdicons.json.model.JSONNull;
import com.sdicons.json.model.JSONValue;
import org.valz.util.ParserException;

import java.math.BigInteger;

public class LongKey implements KeyType<Long> {
    public static final String NAME = "LongKey";

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
        return (other != null) && (other instanceof LongKey);
    }

    public int hashCode() {
        return 0;
    }

    public static class Format extends KeyTypeFormat<LongKey> {
        public LongKey dataFromJson(JSONValue jsonValue) {
            return new LongKey();
        }

        public JSONValue dataToJson(LongKey key) {
            return new JSONNull();
        }
    }
}