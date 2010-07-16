package org.valz.aggregates;

import com.sdicons.json.model.JSONInteger;
import com.sdicons.json.model.JSONValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.Iterator;

public class LongSum extends AbstractAggregate<Long> {
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
        return (other != null) && (other instanceof LongSum);
    }

    public int hashCode() {
        return 0;
    }

    public static class Format extends AggregateFormat<LongSum> {
        public LongSum fromJson(JSONValue jsonValue) {
            return new LongSum();
        }

        @Nullable
        public JSONValue toJson(LongSum aggregate) {
            return null;
        }
    }
}
