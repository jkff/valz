package org.valz.server;

import org.apache.log4j.Logger;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.ReadBackend;
import org.valz.util.protocol.WriteBackend;

import java.util.*;

public class ValzBackend implements ReadBackend, WriteBackend {
    private static final Logger log = Logger.getLogger(ValzBackend.class);

    private final DataStore dataStore = new MemoryDataStore();

    public ValzBackend() {
    }

    public synchronized <T> void submit(String name, Aggregate<T> aggregate, T value) {
        Aggregate<?> existingAggregate = dataStore.getAggregate(name);
        if (existingAggregate == null) {
            dataStore.createAggregate(name, aggregate, value);
        } else {
            if (!existingAggregate.equals(aggregate)) {
                throw new IllegalArgumentException("Val with same name and different aggregate already exists.");
            }

            T oldValue = (T) dataStore.getValue(name);
            List<T> list = Arrays.asList(oldValue, value);
            Object newValue = aggregate.reduce(list.iterator());
            dataStore.setValue(name, newValue);
        }
    }

    public synchronized Collection<String> listVars() {
        return dataStore.listVars();
    }

    public synchronized Object getValue(String name) {
        return dataStore.getValue(name);
    }

    public synchronized Aggregate getAggregate(String name) {
        return dataStore.getAggregate(name);
    }
}
