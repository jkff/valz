package org.valz.model;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public abstract class AbstractAggregate<T> implements Aggregate<T> {
    public T reduce(@NotNull Iterator<T> stream) {
        T res = stream.next();
        while (stream.hasNext()) {
            res = reduce(res, stream.next());
        }
        return res;
    }

    public abstract T reduce(T item1, T item2);
}