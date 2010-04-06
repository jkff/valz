package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapMerge<K, V> extends AbstractAggregate<Map<K,V>> {
    public final Aggregate<? super V> mergeConflictsAggregate;

    public MapMerge(@NotNull Aggregate<? super V> mergeConflictsAggregate) {
        this.mergeConflictsAggregate = mergeConflictsAggregate;
    }

    @Override
    @NotNull
    public Map<K, V> reduce(@NotNull Iterator<Map<K,V>> stream) {
        Map<K, V> res = new HashMap<K, V>();
        while (stream.hasNext()) {
            for (Object entryObject : stream.next().entrySet()) {
                Map.Entry<K, V> entry = (Map.Entry<K, V>)entryObject;
                V existingValue = res.get(entry.getKey());
                if (existingValue == null) {
                    res.put(entry.getKey(), entry.getValue());
                } else {
                    res.put(entry.getKey(),
                            (V)mergeConflictsAggregate.reduce(existingValue, entry.getValue()));
                }
            }
        }
        return res;
    }

    @Override
    public Map<K, V> reduce(Map<K,V> item1, Map<K,V> item2) {
        return reduce(Arrays.asList(item1, item2).iterator());
    }
}
