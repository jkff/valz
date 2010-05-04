package org.valz.util.aggregates;

import com.sdicons.json.model.JSONInteger;
import com.sdicons.json.model.JSONValue;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Iterator;

public class LongSum extends AbstractAggregate<Long, LongSum> {
    public static final String NAME = "LongSum";

    @Override
    public Long reduce(@NotNull Iterator<Long> stream) {
        long res = 0;
        while (stream.hasNext()) {
            res += stream.next();
        }
        return res;
    }

    @Override
    public Long reduce(Long item1, Long item2) {
        return item1 + item2;
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
        return (other instanceof LongSum);
    }

    public int hashCode() {
        return 0;
    }

    public static class ConfigFormatter implements AggregateConfigFormatter<LongSum> {
        public LongSum fromJson(JSONValue jsonValue) {
            return new LongSum();
        }

        public JSONValue toJson(LongSum aggregate) {
            return null;
        }
    }
}
