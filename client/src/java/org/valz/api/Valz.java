package org.valz.api;

import org.valz.util.aggregates.Aggregate;

import java.util.LinkedHashMap;

public final class Valz {
    private static Configuration conf;

    private static LinkedHashMap<String, Aggregate> map = new LinkedHashMap<String, Aggregate>();

    private Valz() {}

    public static synchronized void init(Configuration conf) {
        Valz.conf = conf;
    }

    public static synchronized <T> Val<T> register(
            final String name, final Aggregate<T> aggregate)
    {
        if (map.containsKey(name)) {
            throw new IllegalArgumentException("Val with this name already exists.");
        }
        map.put(name, aggregate);
        return new ValImpl<T>(name);
        //throw new UnsupportedOperationException();
    }

    static <T> Aggregate<T> getAggregate(String name) {
        return map.get(name);
    }
}
