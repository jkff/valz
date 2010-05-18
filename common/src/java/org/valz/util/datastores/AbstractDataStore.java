package org.valz.util.datastores;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.BigMapIterator;
import org.valz.util.aggregates.Value;
import org.valz.util.backends.InvalidAggregateException;
import org.valz.util.keytypes.KeyType;

import java.util.Map;

public abstract class AbstractDataStore implements DataStore {

    public synchronized <T> void submit(String name, Aggregate<T> aggregate, T value) throws
            InvalidAggregateException {
        Value<T> existingValue = getValue(name);
        if (existingValue == null) {
            createAggregate(name, aggregate, value);
        } else {
            if (!existingValue.getAggregate().equals(aggregate)) {
                throw new InvalidAggregateException(
                        "Val with same name and different aggregate already exists.");
            }

            setAggregateValue(name, aggregate.reduce(existingValue.getValue(), value));
        }
    }

    protected abstract <T> void createAggregate(String name, Aggregate<T> aggregate, T value);

    protected abstract <T> void setAggregateValue(String name, T newValue);



    public synchronized <K, T> void submitBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> map) throws
            InvalidAggregateException {

        // name.toUpperCase() - because h2 database makes uppercase for table names
        name = name.toUpperCase();

        Aggregate existingAggregate = getBigMapAggregate(name);
        if (existingAggregate == null) {
            createBigMap(name, keyType, aggregate, map);
        } else {
            if (!existingAggregate.equals(aggregate)) {
                throw new InvalidAggregateException(
                        "Val with same name and different aggregate already exists.");
            }

            for (Map.Entry<K, T> entry : map.entrySet()) {
                T existingValue = (T)getBigMapItem(name, entry.getKey());
                if (existingValue == null) {
                    insertBigMapItem(name, entry.getKey(), entry.getValue());
                } else {
                    updateBigMapItem(name, entry.getKey(), aggregate.reduce(existingValue, entry.getValue()));
                }
            }
        }
    }

    protected abstract <K, T> void createBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> map);

    protected abstract <K, T> void insertBigMapItem(String name, K key, T value);

    protected abstract <K, T> void updateBigMapItem(String name, K key, T newValue);

    protected abstract <K, T> T getBigMapItem(String name, K key);

    
}
