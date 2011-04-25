package org.valz.datastores.h2;

import org.valz.model.*;
import org.valz.datastores.AbstractDataStore;
import org.valz.protocol.messages.BigMapChunkValue;

import java.io.IOException;
import java.util.*;

// TODO Extract into a separate module
public class H2DataStore extends AbstractDataStore {

    private final Database database;
    private final H2Aggregates aggregates;
    private final H2BigMaps bigMaps;

    public H2DataStore(String dbname, AggregateRegistry aggregateRegistry) {
        String connectionString = String.format("jdbc:h2:%s;MVCC=TRUE", dbname);
        database = new Database("org.h2.Driver", connectionString);
        aggregates = new H2Aggregates(database, aggregateRegistry);
        bigMaps = new H2BigMaps(database, aggregateRegistry);
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
    protected <T> void createBigMap(String name, Aggregate<T> aggregate, Map<String, T> map) {
        bigMaps.createBigMap(name, aggregate, map);
    }

    @Override
    protected <T> void insertBigMapItem(String name, Aggregate<T> aggregate, String key, T value) {
        bigMaps.insertBigMapItem(name, aggregate, key, value);
    }

    @Override
    protected <T> void updateBigMapItem(String name, Aggregate<T> aggregate, String key, T newValue) {
        bigMaps.updateBigMapItem(name, aggregate, key, newValue);
    }

    @Override
    public <T> T getBigMapItem(String name, Aggregate<T> aggregate, String key) {
        return bigMaps.getBigMapItem(name, aggregate, key);
    }

    public Collection<String> listVals() {
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

    public <T> BigMapChunkValue<T> getBigMapChunk(String name, String fromKey, int count) {
        return bigMaps.getBigMapChunk(name, fromKey, count);
    }


    public <T> Aggregate<T> getBigMapAggregate(String name) {
        return bigMaps.getBigMapAggregate(name);
    }

    public <T> BigMapChunkValue<T> popBigMapChunk(String name, String fromKey, int count) {
        return bigMaps.popBigMapChunk(name, fromKey, count);
    }

    public void removeBigMap(String name) {
        bigMaps.removeBigMap(name);
    }
}