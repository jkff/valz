package org.valz.datastores.memory;

import org.valz.aggregates.Aggregate;
import org.valz.aggregates.Sample;
import org.valz.datastores.AbstractDataStore;
import org.valz.keytypes.KeyType;
import org.valz.protocol.messages.BigMapChunkValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataStore extends AbstractDataStore {

    private final Map<String, Sample> aggregates = new HashMap<String, Sample>();
    private final Map<String, MemoryBigMap> bigMaps = new HashMap<String, MemoryBigMap>();

    @Override
    protected <T> void createAggregate(String name, Aggregate<T> aggregate, T value) {
        aggregates.put(name, new Sample(aggregate, value));
    }

    @Override
    protected <T> void setAggregateValue(String name, T newValue) {
        Aggregate aggregate = aggregates.get(name).getAggregate();
        aggregates.put(name, new Sample(aggregate, newValue));
    }

    public Collection<String> listVars() {
        return new ArrayList<String>(aggregates.keySet());
    }

    public <T> Sample<T> getValue(String name) {
        return aggregates.get(name);
    }

    public void removeAggregate(String name) {
        aggregates.remove(name);
    }



    public <K, T> void createBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> value) {
        MemoryBigMap<K,T> bigMap = new MemoryBigMap<K,T>(keyType, aggregate);
        bigMap.append(value);
        bigMaps.put(name, bigMap);
    }

    @Override
    protected <K, T> void insertBigMapItem(String name, KeyType<K> keyType, Aggregate<T> aggregate, K key,
                                           T value) {
        bigMaps.get(name).put(key, value);
    }

    @Override
    protected <K, T> void updateBigMapItem(String name, KeyType<K> keyType, Aggregate<T> aggregate, K key,
                                           T newValue) {
        bigMaps.get(name).put(key, newValue);
    }

    @Override
    protected <K, T> T getBigMapItem(String name, KeyType<K> keyType, Aggregate<T> aggregate, K key) {
        return (T)bigMaps.get(name).get(key);
    }

    public Collection<String> listBigMaps() {
        return new ArrayList<String>(bigMaps.keySet());
    }

    public <K,T> BigMapChunkValue<K,T> getBigMapChunk(String name, K fromKey, int count) {
        MemoryBigMap<K,T> memoryBigMap = bigMaps.get(name);
        return new BigMapChunkValue<K,T>(memoryBigMap.getKeyType(), memoryBigMap.getAggregate(), memoryBigMap.getChunk(fromKey, count));
    }

    public <T> Aggregate<T> getBigMapAggregate(String name) {
        return bigMaps.get(name).getAggregate();
    }

    public <K> KeyType<K> getBigMapKeyType(String name) {
        return bigMaps.get(name).getKeyType();
    }

    public <K,T> BigMapChunkValue<K,T> popBigMapChunk(String name, K fromKey, int count) {
        MemoryBigMap map = bigMaps.get(name);
        Map value = map.popChunk(fromKey, count);
        return new BigMapChunkValue<K,T>(map.getKeyType(), map.getAggregate(), value);
    }

    public void removeBigMap(String name) {
        bigMaps.remove(name);
    }

    public void close() throws IOException {
        // Nothing
    }
}
