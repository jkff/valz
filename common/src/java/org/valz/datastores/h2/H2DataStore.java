package org.valz.datastores.h2;

import org.valz.model.*;
import org.valz.datastores.AbstractDataStore;
import org.valz.keytypes.KeyType;
import org.valz.keytypes.KeyTypeRegistry;
import org.valz.protocol.messages.BigMapChunkValue;

import java.io.IOException;
import java.util.*;

public class H2DataStore extends AbstractDataStore {

    private final Database database;
    private final H2Aggregates aggregates;
    private final H2BigMaps bigMaps;

    public H2DataStore(String dbname, KeyTypeRegistry keyTypeRegistry,
                       AggregateRegistry aggregateRegistry) {

        String connectionString = String.format("jdbc:h2:%s;MVCC=TRUE", dbname);
        database = new Database("org.h2.Driver", connectionString);
        aggregates = new H2Aggregates(database, aggregateRegistry);
        bigMaps = new H2BigMaps(database, keyTypeRegistry, aggregateRegistry);
    }

    public void close() throws IOException {
        database.close();
    }

    @Override
    protected <T> void createAggregate(String name, Aggregate<T> aggregate, T value) {
        aggregates.createAggregate(name, aggregate, value);
    }

    @Override
    protected <T> void setAggregateValue(String name, T newValue) {
        aggregates.setAggregateValue(name, newValue);
    }

    @Override
    protected <K, T> void createBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> map) {
        bigMaps.createBigMap(name, keyType, aggregate, map);
    }

    @Override
    protected <K, T> void insertBigMapItem(String name, KeyType<K> keyType, Aggregate<T> aggregate, K key,
                                           T value) {
        bigMaps.insertBigMapItem(name, keyType, aggregate, key, value);
    }

    @Override
    protected <K, T> void updateBigMapItem(String name, KeyType<K> keyType, Aggregate<T> aggregate, K key,
                                           T newValue) {
        bigMaps.updateBigMapItem(name, keyType, aggregate, key, newValue);
    }

    @Override
    public <K, T> T getBigMapItem(String name, KeyType<K> keyType, Aggregate<T> aggregate, K key) {
        return bigMaps.getBigMapItem(name, keyType, aggregate, key);
    }

    public Collection<String> listVars() {
        return aggregates.listVars();
    }

    public <T> Sample<T> getValue(String name) {
        return aggregates.getValue(name);
    }

    public void removeAggregate(String name) {
        aggregates.removeAggregate(name);
    }

    public Collection<String> listBigMaps() {
        return bigMaps.listBigMaps();
    }

    public <K, T> BigMapChunkValue<K, T> getBigMapChunk(String name, K fromKey, int count) {
        return bigMaps.getBigMapChunk(name, fromKey, count);
    }


    public <T> Aggregate<T> getBigMapAggregate(String name) {
        return bigMaps.getBigMapAggregate(name);
    }

    public <K> KeyType<K> getBigMapKeyType(String name) {
        return bigMaps.getBigMapKeyType(name);
    }

    public <K, T> BigMapChunkValue<K, T> popBigMapChunk(String name, K fromKey, int count) {
        return bigMaps.popBigMapChunk(name, fromKey, count);
    }

    public void removeBigMap(String name) {
        bigMaps.removeBigMap(name);
    }
}