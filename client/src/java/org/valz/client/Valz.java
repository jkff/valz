package org.valz.client;

import org.json.simple.JSONObject;
import org.valz.util.aggregates.AggregateUtils;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.WriteConfiguration;
import org.valz.util.protocol.RemoteWriteBackend;
import org.valz.util.protocol.RemoteWriteException;
import org.valz.util.protocol.WriteBackend;

public final class Valz {
    private static WriteBackend backend;

    private Valz() {
    }

    public static synchronized void init(WriteConfiguration conf) {
        Valz.backend = new RemoteWriteBackend(conf);
    }

    public static synchronized <T> Val<T> register(
            final String name, final Aggregate<T> aggregate) {
        return new Val<T>() {
            public void submit(T sample) {
                try {
                    backend.submit(name, AggregateUtils.toJson(aggregate), sample);
                } catch (RemoteWriteException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

}
