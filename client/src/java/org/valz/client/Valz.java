package org.valz.client;

import org.valz.backends.*;
import org.valz.datastores.DataStore;
import org.valz.datastores.h2.H2DataStore;
import org.valz.model.Aggregate;
import org.valz.model.AggregateRegistry;

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

    public static synchronized <T> Val<Map<String, T>> registerBigMap(final String name, final Aggregate<T> aggregate) {
        return new Val<Map<String, T>>() {
            public void submit(Map<String, T> sample) throws RemoteWriteException {
                writeBackend.submitBigMap(name, aggregate, sample);
            }
        };
    }

    public static WriteBackend makeDefaultWriteBackend(
            AggregateRegistry aggregateRegistry, ClientConfig clientConfig)
    {
        List<WriteBackend> remoteBackends = new ArrayList<WriteBackend>();
        for (String url : clientConfig.serverUrls) {
            remoteBackends.add(new RemoteWriteBackend(url, aggregateRegistry));
        }
        WriteBackend roundRobinBackend = new RoundRobinWriteBackend(remoteBackends);

        DataStore dataStore = new H2DataStore(clientConfig.temporaryDatabaseFile,
                aggregateRegistry);
        WriteBackend transitionalBackend = new TransitionalWriteBackend(roundRobinBackend,
                dataStore, clientConfig.flushToServerInterval, clientConfig.bigMapChunkSize);

        WriteBackend nonBlockingBackend = new NonBlockingWriteBackend(transitionalBackend);

        return nonBlockingBackend;
    }

    private Valz() {
    }
}
