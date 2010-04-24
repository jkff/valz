package org.valz.client;

import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.*;
import org.valz.util.protocol.ReadBackend;
import org.valz.util.protocol.RemoteReadBackend;

public final class Valz {
    private static WriteBackend writeBackend;
    public final static AggregateRegistry registry = new AggregateRegistry();

    private Valz() {
    }

    public static synchronized void init(WriteConfiguration conf) {
        Valz.writeBackend = new RemoteWriteBackend(conf.getServerURL(), registry);
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

}
