package org.valz.util.datastores;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.Value;
import org.valz.util.keytypes.KeyType;
import org.valz.util.protocol.messages.BigMapChunkValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataStore extends AbstractDataStore {

    private final Map<String, Value> aggregates = new HashMap<String, Value>();
    private final Map<String, MemoryBigMap> bigMaps = new HashMap<String, MemoryBigMap>();

    @Override
    protected <T> void createAggregate(String name, Aggregate<T> aggregate, T value) {
        aggregates.put(name, new Value(aggregate, value));
    }

    @Override
    protected <T> void setAggregateValue(String name, T newValue) {
        Aggregate aggregate = aggregates.get(name).getAggregate();
        aggregates.put(name, new Value(aggregate, newValue));
    }

    public Collection<String> listVars() {
        return new ArrayList<String>(aggregates.keySet());
    }

    public <T> Value<T> getValue(String name) {
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
    protected <K, T> void insertBigMapItem(String name, K key, T value) {
        bigMaps.get(name).put(key, value);
    }

    @Override
    protected <K, T> void updateBigMapItem(String name, K key, T newValue) {
        bigMaps.get(name).put(key, newValue);
    }

    @Override
    protected <K, T> T getBigMapItem(String name, K key) {
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

    public <K,T> BigMapChunkValue<K,T> getBigMapChunkForSubmit(String name, K fromKey, int count) {

        return new BigMapChunkValue<K,T>(bigMaps.get(name).getKeyType(), bigMaps.get(name).getAggregate(), bigMaps.get(name).getChunkForSubmit(fromKey, count));
    }

    public void removeBigMap(String name) {
        bigMaps.remove(name);
    }
}
