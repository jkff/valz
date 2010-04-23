package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class MinLong extends AbstractAggregate<Long> {
    @Override
    public Long reduce(@NotNull Iterator<Long> stream) {
        long res = stream.next();
        while (stream.hasNext()) {
            long value = stream.next();
            if (value < res) {
                res = value;
            }
        }
        return res;
    }

    @Override
    public Long reduce(Long item1, Long item2) {
        return Math.min(item1, item2);
    }
}