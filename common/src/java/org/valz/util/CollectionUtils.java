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

    public static <K, T> SortedMap<K, T> sortedMap(@NotNull K[] keys, @NotNull V[] values) {
        TreeMap<K, T> map = new TreeMap<K, T>();
        if (keys.length != values.length) {
            throw new IllegalArgumentException("Lengths of keys and values must be same.");
        }
        for (int i=0; i<keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }
}
