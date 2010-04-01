package org.valz.client;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.Backend;
import org.valz.util.protocol.RemoteBackend;
import org.valz.util.protocol.RemoteException;
import org.valz.util.protocol.WriteConfiguration;

public final class Valz {
    private static Backend backend;

    private Valz() {
    }

    public static synchronized void init(WriteConfiguration conf) {
        Valz.backend = new RemoteBackend(conf.getServerURL());
    }

    public static synchronized <T> Val<T> register(
            final String name, final Aggregate<T> aggregate) {
        return new Val<T>() {
            public void submit(T sample) {
                try {
                    backend.submit(name, aggregate, sample);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

}
