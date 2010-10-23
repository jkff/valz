package org.valz.datastores;

import org.valz.model.Aggregate;
import org.valz.model.Sample;
import org.valz.backends.InvalidAggregateException;

import java.io.Closeable;
import java.util.Map;

public abstract class AbstractDataStore implements DataStore, Closeable {
    public synchronized <T> void submit(String name, Aggregate<T> aggregate, T value) throws
            InvalidAggregateException {
        Sample<T> existingSample = getValue(name);
        if (existingSample == null) {
            createAggregate(name, aggregate, value);
        } else {
            if (!existingSample.getAggregate().equals(aggregate)) {
                throw new InvalidAggregateException(
                        "Val with same name and different aggregate already exists.");
            }

            setAggregateValue(name, aggregate.reduce(existingSample.getValue(), value));
        }
    }

    protected abstract <T> void createAggregate(String name, Aggregate<T> aggregate, T value);

    protected abstract <T> void setAggregateValue(String name, T newValue);



    public synchronized <T> void submitBigMap(
            String name, Aggregate<T> aggregate, Map<String, T> map)
            throws InvalidAggregateException 
    {
        // name.toUpperCase() - because h2 database makes uppercase for table names
        name = name.toUpperCase();

        Aggregate existingAggregate = getBigMapAggregate(name);
        if (existingAggregate == null) {
            createBigMap(name, aggregate, map);
        } else {
            if (!existingAggregate.equals(aggregate)) {
                throw new InvalidAggregateException(
                        "Val with same name and different aggregate already exists.");
            }

            for (Map.Entry<String, T> entry : map.entrySet()) {
                T existingValue = getBigMapItem(name, aggregate, entry.getKey());
                if (existingValue == null) {
                    insertBigMapItem(name, aggregate, entry.getKey(), entry.getValue());
                } else {
                    updateBigMapItem(name, aggregate, entry.getKey(), aggregate.reduce(existingValue, entry.getValue()));
                }
            }
        }
    }

    protected abstract <T> void createBigMap(String name, Aggregate<T> aggregate, Map<String, T> map);

    protected abstract <T> void insertBigMapItem(String name, Aggregate<T> aggregate, String key, T value);

    protected abstract <T> void updateBigMapItem(String name, Aggregate<T> aggregate, String key, T newValue);

    protected abstract <T> T getBigMapItem(String name, Aggregate<T> aggregate, String key);
}
