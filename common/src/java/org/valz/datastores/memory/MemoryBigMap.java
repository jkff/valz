package org.valz.datastores.memory;

import org.jetbrains.annotations.NotNull;
import org.valz.model.Aggregate;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

class MemoryBigMap<T> {
    private final Aggregate<T> aggregate;
    private final SortedMap<String, T> map = new TreeMap<String, T>();

    public MemoryBigMap(@NotNull Aggregate<T> aggregate) {
        this.aggregate = aggregate;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }

    public synchronized void append(Map<String, T> value) {
        for (Map.Entry<String, T> entry : value.entrySet()) {
            if (!map.containsKey(entry.getKey())) {
                map.put(entry.getKey(), entry.getValue());
            } else {
                T existingValue = map.get(entry.getKey());
                T newValue = aggregate.reduce(existingValue, entry.getValue());
                map.put(entry.getKey(), newValue);
            }
        }
    }

    public T get(String key) {
        return map.get(key);
    }

    public T put(String key, T value) {
        return map.put(key, value);
    }

    public TreeMap<String, T> popChunk(String fromKey, int count) {
        TreeMap<String, T> res = new TreeMap<String, T>();

        for (Iterator<Map.Entry<String, T>> it = map.tailMap(fromKey).entrySet().iterator();
             it.hasNext() && count > 0; --count)
        {
            Map.Entry<String, T> entry = it.next();
            res.put(entry.getKey(), entry.getValue());
            it.remove();
        }

        return res;
    }

    public TreeMap<String, T> getChunk(String fromKey, int count) {
        TreeMap<String, T> res = new TreeMap<String, T>();

        for (Iterator<Map.Entry<String, T>> it = map.tailMap(fromKey).entrySet().iterator();
             it.hasNext() && count > 0; --count)
        {
            Map.Entry<String, T> entry = it.next();
            res.put(entry.getKey(), entry.getValue());
        }

        return res;
    }

}
