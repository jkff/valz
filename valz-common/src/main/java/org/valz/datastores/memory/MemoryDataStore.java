package org.valz.datastores.memory;

import org.valz.datastores.AbstractDataStore;
import org.valz.model.Aggregate;
import org.valz.model.Sample;
import org.valz.protocol.messages.BigMapChunkValue;

import java.io.IOException;
import java.util.*;

public class MemoryDataStore extends AbstractDataStore {
    private final Map<String, Sample> aggregates = new HashMap<String, Sample>();
    private final Map<String, MemoryBigMap> bigMaps = new HashMap<String, MemoryBigMap>();

    @Override
    protected <T> void createAggregate(String name, Aggregate<T> aggregate, T value) {
        aggregates.put(name, new Sample<T>(aggregate, value));
    }

    @Override
    protected <T> void setAggregateValue(String name, T newValue) {
        Aggregate<T> aggregate = aggregates.get(name).getAggregate();
        aggregates.put(name, new Sample<T>(aggregate, newValue));
    }

    public Collection<String> listVals() {
        return new ArrayList<String>(aggregates.keySet());
    }

    public <T> Sample<T> getValue(String name) {
        return aggregates.get(name);
    }

    public void removeAggregate(String name) {
        aggregates.remove(name);
    }



    public <T> void createBigMap(String name, Aggregate<T> aggregate, Map<String, T> value) {
        MemoryBigMap<T> bigMap = new MemoryBigMap<T>(aggregate);
        bigMap.append(value);
        bigMaps.put(name, bigMap);
    }

    @Override
    protected <T> void insertBigMapItem(String name, Aggregate<T> aggregate, String key,
                                           T value) {
        bigMaps.get(name).put(key, value);
    }

    @Override
    protected <T> void updateBigMapItem(String name, Aggregate<T> aggregate, String key,
                                           T newValue) {
        bigMaps.get(name).put(key, newValue);
    }

    @Override
    protected <T> T getBigMapItem(String name, Aggregate<T> aggregate, String key) {
        return (T)bigMaps.get(name).get(key);
    }

    public Collection<String> listBigMaps() {
        return new ArrayList<String>(bigMaps.keySet());
    }

    public <T> BigMapChunkValue<T> getBigMapChunk(String name, String fromKey, int count) {
        MemoryBigMap memoryBigMap = bigMaps.get(name);
        return new BigMapChunkValue<T>(
                memoryBigMap.getAggregate(),
                memoryBigMap.getChunk(fromKey, count));
    }

    public <T> Aggregate<T> getBigMapAggregate(String name) {
        return bigMaps.get(name).getAggregate();
    }

    public <T> BigMapChunkValue<T> popBigMapChunk(String name, String fromKey, int count) {
        MemoryBigMap map = bigMaps.get(name);
        TreeMap value = map.popChunk(fromKey, count);
        return new BigMapChunkValue<T>(map.getAggregate(), value);
    }

    public void removeBigMap(String name) {
        bigMaps.remove(name);
    }

    public void close() throws IOException {
        // Nothing
    }
}
