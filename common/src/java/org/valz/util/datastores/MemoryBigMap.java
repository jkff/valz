package org.valz.util.datastores;

import org.jetbrains.annotations.NotNull;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.BigMap;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MemoryBigMap<T> extends BigMap<T> {

    private final SortedMap<String, T> map = new TreeMap<String, T>();

    public MemoryBigMap(@NotNull Aggregate<T> aggregate) {
        super(aggregate);
    }

    @Override
    public synchronized Iterator<Map.Entry<String, T>> iteratorSince(String fromKey) {
        return map.tailMap(fromKey).entrySet().iterator();
    }

    @Override
    public synchronized void append(Map<String, T> value) {
        for (Map.Entry<String, T> entry : value.entrySet()) {
            if (!map.containsKey(entry.getKey())) {
                map.put(entry.getKey(), entry.getValue());
            } else {
                V existingValue = map.get(entry.getKey());
                V newValue = aggregate.reduce(existingValue, entry.getValue());
                map.put(entry.getKey(), newValue);
            }
        }
    }
}
