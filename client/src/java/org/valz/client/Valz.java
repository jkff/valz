package org.valz.client;

import org.valz.backends.*;
import org.valz.datastores.DataStore;
import org.valz.datastores.h2.H2DataStore;
import org.valz.model.Aggregate;
import org.valz.model.AggregateRegistry;
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
        WriteBackend writeBackend = makeDefaultWriteBackend(
                KeyTypeRegistry.create(),
                AggregateRegistry.create(clientConfig.aggregatesDirectory),
                clientConfig);
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

    public static WriteBackend makeDefaultWriteBackend(
            KeyTypeRegistry keyTypeRegistry, AggregateRegistry aggregateRegistry, ClientConfig clientConfig)
    {
        List<WriteBackend> remoteBackends = new ArrayList<WriteBackend>();
        for (String url : clientConfig.serverUrls) {
            remoteBackends.add(new RemoteWriteBackend(url, keyTypeRegistry, aggregateRegistry));
        }
        WriteBackend roundRobinBackend = new RoundRobinWriteBackend(remoteBackends);

        DataStore dataStore = new H2DataStore(clientConfig.temporaryDatabaseFile,
                keyTypeRegistry, aggregateRegistry);
        WriteBackend transitionalBackend = new TransitionalWriteBackend(roundRobinBackend,
                dataStore, clientConfig.flushToServerInterval, clientConfig.bigMapChunkSize);

        WriteBackend nonBlockingBackend = new NonBlockingWriteBackend(transitionalBackend);

        return nonBlockingBackend;
    }

    private Valz() {
    }
}
