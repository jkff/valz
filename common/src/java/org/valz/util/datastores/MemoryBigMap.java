package org.valz.util.datastores;

import org.jetbrains.annotations.NotNull;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.BigMapIterator;
import org.valz.util.protocol.messages.BigMapChunkValue;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

class MemoryBigMap<T> {

    public Aggregate<T> getAggregate() {
        return aggregate;
    }

    private final Aggregate<T> aggregate;
    private final SortedMap<String, T> map = new TreeMap<String, T>();

    public MemoryBigMap(@NotNull Aggregate<T> aggregate) {
        this.aggregate = aggregate;
    }

    public BigMapIterator<T> iterator() {
        return iteratorSince("");
    }

    public synchronized BigMapIterator<T> iteratorSince(String fromKey) {
        final Iterator<Map.Entry<String, T>> iter = map.tailMap(fromKey).entrySet().iterator();

        return new BigMapIterator<T>() {
            public Aggregate<T> getAggregate() {
                return aggregate;
            }

            public boolean hasNext() {
                return iter.hasNext();
            }

            public Map.Entry<String, T> next() {
                return iter.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
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

    public Map<String, T> getChunkForSubmit(String fromKey, int count) {
        Map<String, T> res = new TreeMap<String, T>();

        SortedMap<String, T> tailMap = map.tailMap(fromKey);
        for (Map.Entry<String, T> entry : tailMap.entrySet()) {
            if (count <= 0) {
                break;
            }
            count--;

            res.put(entry.getKey(), entry.getValue());
            map.remove(entry.getKey());
        }

        return res;
    }
}
