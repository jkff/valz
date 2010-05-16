package org.valz.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CollectionUtils {
    private CollectionUtils() {
    }

    public static <T> T[] ar(T... ts) {
        return ts;
    }

    public static <T> Set<T> set(T... ts) {
        return new HashSet<T>(Arrays.asList(ts));
    }

    public static <K, V> SortedMap<K, V> sortedMap(@NotNull K[] keys, @NotNull V[] values) {
        TreeMap<K, V> map = new TreeMap<K, V>();
        if (keys.length != values.length) {
            throw new IllegalArgumentException("Lengths of keys and values must be same.");
        }
        for (int i=0; i<keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }
}
