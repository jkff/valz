package org.valz.util.aggregates;

import com.sdicons.json.model.JSONInteger;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Iterator;

public class LongSum extends AbstractAggregate<Long> {

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
        return "LongSum";
    }

    public JSONValue toJson() {
        return new JSONString("");
    }

    public JSONValue dataToJson(Long item) {
        return new JSONInteger(new BigInteger(item.toString()));
    }

    public Long parseData(JSONValue json) {
        return ((JSONInteger)json).getValue().longValue();
    }



    public static class Parser implements AggregateParser<Long> {
        public LongSum parse(JSONValue json) {
            return new LongSum();
        }
    }
}
