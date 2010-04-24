package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public abstract class AbstractAggregate<T> implements Aggregate<T> {
    @Nullable
    public T reduce(@NotNull Iterator<T> stream) {
        T res = stream.next();
        while (stream.hasNext()) {
            res = reduce(res, stream.next());
        }
        return res;
    }

    @Nullable
    public abstract T reduce(T item1, T item2);


    @Override
    public boolean equals(Object o) {
        if ((o == null) || (o.getClass() != getClass())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}