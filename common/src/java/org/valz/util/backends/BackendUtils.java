package org.valz.util.backends;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.datastores.DataStore;

class BackendUtils {
    public static <T> void submit(DataStore dataStore, String name, Aggregate<T> aggregate, T value) throws
            InvalidAggregateException {
        Aggregate<?> existingAggregate = dataStore.getAggregate(name);
        if (existingAggregate == null) {
            dataStore.createAggregate(name, aggregate, value);
        } else {
            if (!existingAggregate.equals(aggregate)) {
                throw new InvalidAggregateException(
                        "Val with same name and different aggregate already exists.");
            }

            T oldValue = (T)dataStore.getValue(name).getValue();
            Object newValue = aggregate.reduce(oldValue, value);
            dataStore.setValue(name, newValue);
        }
    }

    private BackendUtils() {
    }
}
