package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class MinInt extends AbstractAggregate<Integer> {

    public static MinInt deserialize(Object object, AggregateRegistry registry) {
        return new MinInt();
    }



    @Override
    public Integer reduce(@NotNull Iterator<Integer> stream) {
        int res = stream.next();
        while (stream.hasNext()) {
            int value = stream.next();
            if (value < res) {
                res = value;
            }
        }
        return res;
    }

    @Override
    public Integer reduce(Integer item1, Integer item2) {
        return item1 < item2 ? item1 : item2;
    }
}