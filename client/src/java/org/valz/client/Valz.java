package org.valz.client;

import org.valz.model.Aggregate;
import org.valz.model.AggregateRegistry;
import org.valz.backends.RemoteWriteBackend;
import org.valz.backends.RemoteWriteException;
import org.valz.backends.RoundRobinWriteBackend;
import org.valz.backends.WriteBackend;

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
            public void submit(T sample) throws RemoteWriteException {
                writeBackend.submit(name, aggregate, sample);
            }
        };
    }

    public static synchronized <T> Val<Map<String, T>> registerBigMap(final String name, final Aggregate<T> aggregate) {
        return new Val<Map<String, T>>() {
            public void submit(Map<String, T> sample) throws RemoteWriteException {
                writeBackend.submitBigMap(name, aggregate, sample);
            }
        };
    }

    public static WriteBackend makeWriteBackend(AggregateRegistry aggregateRegistry, String... serverURLs)
    {
        List<WriteBackend> writeBackends = new ArrayList<WriteBackend>();
        for (String url : serverURLs) {
            writeBackends.add(new RemoteWriteBackend(url, aggregateRegistry));
        }
        return new RoundRobinWriteBackend(writeBackends);
    }

    private Valz() {
    }
}
