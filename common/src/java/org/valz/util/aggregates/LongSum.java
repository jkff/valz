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

    public Object configToJson() {
        return null;
    }

    public Object dataToJson(Long item) {
        return item;
    }

    public Long parseData(JSONValue jsonValue) throws ParserException {
        return ((JSONInteger)jsonValue).getValue().longValue();
    }



    public static class ConfigParser implements AggregateConfigParser<Long> {
        public LongSum parse(JSONValue jsonValue) {
            return new LongSum();
        }
    }
}
