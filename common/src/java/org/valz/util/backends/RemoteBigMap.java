package org.valz.util.backends;

import org.jetbrains.annotations.NotNull;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.BigMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class RemoteBigMap<T> extends BigMap<T> {

    private final SortedMap<String, T> map = new TreeMap<String, T>();

    public RemoteBigMap(@NotNull Aggregate<T> aggregate) {
        super(aggregate);
    }

    @Override
    public synchronized Iterator<Map.Entry<String, T>> iteratorSince(String fromKey) {
        return map.tailMap(fromKey).entrySet().iterator();
    }

    @Override
    public void append(Map<String, T> value) {
        throw new NotImplementedException();
    }
}