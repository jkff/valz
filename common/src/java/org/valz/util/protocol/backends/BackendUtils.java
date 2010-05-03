package org.valz.util.protocol.backends;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.datastores.DataStore;

import java.util.Arrays;
import java.util.List;

class BackendUtils {
    public static <T> void submit(DataStore dataStore, String name, Aggregate<T> aggregate, T value) throws InvalidAggregateException {
        Aggregate<?> existingAggregate = dataStore.getAggregate(name);
        if (existingAggregate == null) {
            dataStore.createAggregate(name, aggregate, value);
        } else {
            if (!existingAggregate.equals(aggregate)) {
                throw new InvalidAggregateException("Val with same name and different aggregate already exists.");
            }

            T oldValue = (T)dataStore.getValue(name).getValue();
            List<T> list = Arrays.asList(oldValue, value);
            Object newValue = aggregate.reduce(list.iterator());
            dataStore.setValue(name, newValue);
        }
    }

    private BackendUtils() {
    }
}
