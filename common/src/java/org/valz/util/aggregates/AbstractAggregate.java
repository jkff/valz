package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public abstract class AbstractAggregate<T> implements Aggregate<T> {
    @NotNull
    public T reduce(Iterator<T> stream) {
        T res = stream.next();
        while (stream.hasNext()) {
            res = reduce(res, stream.next());
        }
        return res;
    }

    public abstract T reduce(T item1, T item2);

    public Object toSerialized() {
        return null;
    }
}