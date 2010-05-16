package org.valz.util.backends;

import org.apache.log4j.Logger;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.datastores.DataStore;

import java.util.Map;

public class TransitionalWriteBackend implements WriteBackend {
    private static final Logger log = Logger.getLogger(TransitionalWriteBackend.class);

    private final DataStore dataStore;

    protected DataStore getDataStore() {
        return dataStore;
    }

    public TransitionalWriteBackend(WriteBackend writeBackend, DataStore dataStore, long intervalMillis) {
        this.dataStore = dataStore;
        new Thread(new TransitionalSubmitter(writeBackend, dataStore, intervalMillis)).start();
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException {
        try {
            dataStore.submit(name, aggregate, value);
        } catch (InvalidAggregateException e) {
            throw new RemoteWriteException(e);
        }
    }

    public <T> void submitBigMap(String name, Aggregate<T> aggregate, Map<String, T> value) throws
            RemoteWriteException {
        try {
            dataStore.submitBigMap(name, aggregate, value);
        } catch (InvalidAggregateException e) {
            throw new RemoteWriteException(e);
        }
    }
}
