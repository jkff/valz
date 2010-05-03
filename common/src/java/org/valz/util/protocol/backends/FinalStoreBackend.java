package org.valz.util.protocol.backends;

import org.apache.log4j.Logger;
import org.valz.util.Value;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.datastores.DataStore;

import java.util.Collection;

public class FinalStoreBackend implements ReadBackend, WriteBackend {
    private static final Logger log = Logger.getLogger(FinalStoreBackend.class);

    private final DataStore dataStore;

    public FinalStoreBackend(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public synchronized <T> void submit(String name, Aggregate<T> aggregate, T value) {
        try {
            BackendUtils.submit(dataStore, name, aggregate, value);
        } catch (InvalidAggregateException e) {
            log.info("Invalid submit.", e);
        }
    }

    public synchronized Collection<String> listVars() {
        return dataStore.listVars();
    }

    public synchronized Value getValue(String name) {
        return dataStore.getValue(name);
    }

    public synchronized Aggregate getAggregate(String name) {
        return dataStore.getAggregate(name);
    }
}
