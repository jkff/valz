package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class MinInt extends AbstractAggregate<Integer> {
    @NotNull
    public Integer reduce(Iterator<Integer> stream) {
        int res = stream.next();
        while (stream.hasNext()) {
            int value = stream.next();
            if (value < res) {
                res = value;
            }
        }
        return res;
    }

    public Integer reduce(Integer item1, Integer item2) {
        return item1 < item2 ? item1 : item2;
    }

    public static MinInt deserialize(Object object, AggregateRegistry registry) {
        return new MinInt();
    }
}