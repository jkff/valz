package org.valz.datastores;

import org.valz.model.Aggregate;
import org.valz.model.Sample;
import org.valz.backends.InvalidAggregateException;
import org.valz.keytypes.KeyType;

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



    public synchronized <K, T> void submitBigMap(
            String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> map)
            throws InvalidAggregateException 
    {
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
                T existingValue = (T)getBigMapItem(name, keyType, aggregate, entry.getKey());
                if (existingValue == null) {
                    insertBigMapItem(name, keyType, aggregate, entry.getKey(), entry.getValue());
                } else {
                    updateBigMapItem(name, keyType, aggregate, entry.getKey(), aggregate.reduce(existingValue, entry.getValue()));
                }
            }
        }
    }

    protected abstract <K, T> void createBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> map);

    protected abstract <K, T> void insertBigMapItem(String name, KeyType<K> keyType, Aggregate<T> aggregate, K key, T value);

    protected abstract <K, T> void updateBigMapItem(String name, KeyType<K> keyType, Aggregate<T> aggregate, K key, T newValue);

    protected abstract <K, T> T getBigMapItem(String name, KeyType<K> keyType, Aggregate<T> aggregate, K key);

    
}
