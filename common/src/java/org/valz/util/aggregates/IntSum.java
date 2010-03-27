package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class IntSum extends AbstractAggregate<Integer> {

    public static IntSum deserialize(Object object, AggregateRegistry registry) {
        return new IntSum();
    }



    @Override
    public Integer reduce(@NotNull Iterator<Integer> stream) {
        int res = 0;
        while (stream.hasNext()) {
            res += stream.next();
        }
        return res;
    }

    @Override
    public Integer reduce(Integer item1, Integer item2) {
        return item1 + item2;
    }
}
