package org.valz.util.protocol.backends;

import org.apache.log4j.Logger;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.datastores.DataStore;

public class TransitionalWriteBackend implements WriteBackend {
    private static final Logger log = Logger.getLogger(FinalStoreBackend.class);

    private final DataStore dataStore;

    protected DataStore getDataStore() {
        return dataStore;
    }

    public TransitionalWriteBackend(WriteBackend writeBackend, DataStore dataStore, long intervalMillis) {
        this.dataStore = dataStore;
        new Thread(new TransitionalSubmitter(writeBackend, dataStore, intervalMillis)).start();
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) {
        synchronized (dataStore) {
            try {
                BackendUtils.submit(dataStore, name, aggregate, value);
            } catch (InvalidAggregateException e) {
                log.info("Invalid submit.", e);
            }
        }
    }
}
