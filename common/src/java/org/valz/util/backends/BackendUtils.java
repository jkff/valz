package org.valz.util.backends;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.datastores.Calculator;
import org.valz.util.datastores.DataStore;

class BackendUtils {
    public static <T> void submit(DataStore dataStore, String name, Aggregate<T> aggregate, T value) throws
            InvalidAggregateException {

        final T finalValue = value;
        final Aggregate<T> finalAggregate = aggregate;

        Aggregate<?> existingAggregate = dataStore.getAggregate(name);
        if (existingAggregate == null) {
            dataStore.createAggregate(name, aggregate, value);
        } else {
            if (!existingAggregate.equals(aggregate)) {
                throw new InvalidAggregateException(
                        "Val with same name and different aggregate already exists.");
            }

            dataStore.modify(name, new Calculator<T>() {
                public T calculate(T value) {
                    return finalAggregate.reduce(value, finalValue);
                }
            });
        }
    }

    private BackendUtils() {
    }
}
