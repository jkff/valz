package org.valz.datastores.memory;

import org.jetbrains.annotations.NotNull;
import org.valz.aggregates.Aggregate;
import org.valz.keytypes.KeyType;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

class MemoryBigMap<K, T> {

    private final Aggregate<T> aggregate;
    private final KeyType<K> keyType;
    private final SortedMap<K, T> map = new TreeMap<K, T>();

    public MemoryBigMap(KeyType<K> keyType, @NotNull Aggregate<T> aggregate) {
        this.keyType = keyType;
        this.aggregate = aggregate;
    }

    public KeyType<K> getKeyType() {
        return keyType;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }

    public synchronized void append(Map<K, T> value) {
        for (Map.Entry<K, T> entry : value.entrySet()) {
            if (!map.containsKey(entry.getKey())) {
                map.put(entry.getKey(), entry.getValue());
            } else {
                T existingValue = map.get(entry.getKey());
                T newValue = aggregate.reduce(existingValue, entry.getValue());
                map.put(entry.getKey(), newValue);
            }
        }
    }

    public T get(K key) {
        return map.get(key);
    }

    public T put(K key, T value) {
        return map.put(key, value);
    }

    public Map<K, T> getChunkForSubmit(K fromKey, int count) {
        Map<K, T> res = new TreeMap<K, T>();

        SortedMap<K, T> tailMap = map.tailMap(fromKey);
        for (Map.Entry<K, T> entry : tailMap.entrySet()) {
            if (count <= 0) {
                break;
            }
            count--;

            res.put(entry.getKey(), entry.getValue());
            map.remove(entry.getKey());
        }

        return res;
    }

    public Map<K, T> getChunk(K fromKey, int count) {
        Map<K, T> res = new TreeMap<K, T>();

        SortedMap<K, T> tailMap = map.tailMap(fromKey);
        for (Map.Entry<K, T> entry : tailMap.entrySet()) {
            if (count <= 0) {
                break;
            }
            count--;

            res.put(entry.getKey(), entry.getValue());
        }

        return res;
    }

}
