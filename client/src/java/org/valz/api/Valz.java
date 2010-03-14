package org.valz.api;

import org.valz.util.aggregates.Aggregate;

public final class Valz {
    private static Configuration conf;

    private Valz() {}

    public static synchronized void init(Configuration conf) {
        Valz.conf = conf;
    }

    public static synchronized <T> Val<T> register(
            final String name, final Aggregate<T> aggregate)
    {
        throw new UnsupportedOperationException();
    }
}
