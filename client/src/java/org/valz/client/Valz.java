package org.valz.client;

import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.*;

public final class Valz {
    private static WriteBackend writeBackend = null;




    public static synchronized void init(WriteConfiguration conf, AggregateRegistry registry) {
        Valz.writeBackend = new RemoteWriteBackend(conf, registry);
    }

    public static synchronized <T> Val<T> register(
            final String name, final Aggregate<T> aggregate) {
        return new Val<T>() {
            public void submit(T sample) {
                try {
                    writeBackend.submit(name, aggregate, sample);
                } catch (RemoteWriteException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private Valz() {
    }
}
