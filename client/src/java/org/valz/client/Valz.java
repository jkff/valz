package org.valz.client;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.backends.RemoteWriteBackend;
import org.valz.util.backends.RemoteWriteException;
import org.valz.util.backends.RoundRobinWriteBackend;
import org.valz.util.backends.WriteBackend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Valz {
    private static WriteBackend writeBackend = null;



    public static synchronized void init(WriteBackend writeBackend) {
        Valz.writeBackend = writeBackend;
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

    public static synchronized <T> Val<Map<String, T>> registerBigMap(final String name, final Aggregate<T> mergeConflictsAggregate) {
        return new Val<Map<String, T>>() {
            public void submit(Map<String, T> sample) {
                try {
                    writeBackend.submitBigMap(name, mergeConflictsAggregate, sample);
                } catch (RemoteWriteException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static WriteBackend getWriteBackend(AggregateRegistry registry, String... serverURLs) {
        List<WriteBackend> writeBackends = new ArrayList<WriteBackend>();
        for (String url : serverURLs) {
            writeBackends.add(new RemoteWriteBackend(url, registry));
        }
        return new RoundRobinWriteBackend(writeBackends);
    }

    private Valz() {
    }
}
