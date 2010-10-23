package org.valz.client;

import org.valz.model.Aggregate;
import org.valz.model.AggregateRegistry;
import org.valz.backends.RemoteWriteBackend;
import org.valz.backends.RemoteWriteException;
import org.valz.backends.RoundRobinWriteBackend;
import org.valz.backends.WriteBackend;
import org.valz.keytypes.KeyType;
import org.valz.keytypes.KeyTypeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Valz {
    private static WriteBackend writeBackend = null;



    public static synchronized void init(WriteBackend writeBackend) {
        Valz.writeBackend = writeBackend;
    }

    public static synchronized void init(ClientConfig clientConfig) {
        WriteBackend writeBackend = makeWriteBackend(
                KeyTypeRegistry.create(),
                AggregateRegistry.create(clientConfig.aggregatesDirectory),
                clientConfig.serverUrls);
        init(writeBackend);
    }

    public static synchronized <T> Val<T> register(
            final String name, final Aggregate<T> aggregate) {
        return new Val<T>() {
            public void submit(T sample) throws RemoteWriteException {
                writeBackend.submit(name, aggregate, sample);
            }
        };
    }

    public static synchronized <K, T> Val<Map<K, T>> registerBigMap(final String name, final KeyType<K> keyType, final Aggregate<T> aggregate) {
        return new Val<Map<K, T>>() {
            public void submit(Map<K, T> sample) throws RemoteWriteException {
                writeBackend.submitBigMap(name, keyType, aggregate, sample);
            }
        };
    }

    public static WriteBackend makeWriteBackend(
            KeyTypeRegistry keyTypeRegistry, AggregateRegistry aggregateRegistry, String... serverURLs)
    {
        List<WriteBackend> writeBackends = new ArrayList<WriteBackend>();
        for (String url : serverURLs) {
            writeBackends.add(new RemoteWriteBackend(url, keyTypeRegistry, aggregateRegistry));
        }
        return new RoundRobinWriteBackend(writeBackends);
    }

    private Valz() {
    }
}
