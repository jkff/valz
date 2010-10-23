package org.valz.model;

import com.sdicons.json.model.JSONInteger;
import com.sdicons.json.model.JSONValue;
import org.valz.util.ParserException;

import java.math.BigInteger;

public class LongMin extends AbstractAggregate<Long> {
    public static final String NAME = "LongMin";

    @Override
    public Long reduce(Long item1, Long item2) {
        return Math.min(item1, item2);
    }

    public String getName() {
        return NAME;
    }

    public JSONValue dataToJson(Long item) {
        return new JSONInteger(new BigInteger(item.toString()));
    }

    public Long dataFromJson(JSONValue jsonValue) throws ParserException {
        return ((JSONInteger)jsonValue).getValue().longValue();
    }

    public boolean equals(Object other) {
        return (other != null) && (other instanceof LongMin);
    }

    public int hashCode() {
        return 0;
    }

    public static class Format extends AggregateFormat<LongMin> {
        public LongMin fromJson(JSONValue jsonValue) {
            return new LongMin();
        }

        public JSONValue toJson(LongMin aggregate) {
            return null;
        }
    }
}