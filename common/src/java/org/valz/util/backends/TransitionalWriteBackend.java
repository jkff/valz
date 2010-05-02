package org.valz.util.backends;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.WriteBackend;

import java.util.Arrays;
import java.util.List;

public class TransitionalWriteBackend implements WriteBackend {
    private final DataStore dataStore;

    public TransitionalWriteBackend(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) {
        synchronized (dataStore) {
            Aggregate<?> existingAggregate = dataStore.getAggregate(name);
            if (existingAggregate == null) {
                dataStore.createAggregate(name, aggregate, value);
            } else {
                if (!existingAggregate.equals(aggregate)) {
                    //throw new IllegalArgumentException("Val with same name and different aggregate already exists.");
                    // TODO: write log - ?
                }

                T oldValue = (T)dataStore.getValue(name).getValue();
                List<T> list = Arrays.asList(oldValue, value);
                Object newValue = aggregate.reduce(list.iterator());
                dataStore.setValue(name, newValue);
            }
        }
    }
}
