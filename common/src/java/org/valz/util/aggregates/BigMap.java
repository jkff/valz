package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

public abstract class BigMap<T> implements Iterable<Map.Entry<String, T>> {

    public final Aggregate<T> aggregate;

    public BigMap(@NotNull Aggregate<T> aggregate) {
        this.aggregate = aggregate;
    }

    public abstract Iterator<Map.Entry<String, T>> iteratorSince(String fromKey);

    public Iterator<Map.Entry<String, T>> iterator() {
        return iteratorSince("");
    }

    public abstract void append(final Map<String, T> value);
}
