package org.valz.util.datastores;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.BigMapIterator;
import org.valz.util.aggregates.Value;
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



    public <T> void createBigMap(String name, Aggregate<T> aggregate, Map<String, T> value) {
        MemoryBigMap<T> bigMap = new MemoryBigMap<T>(aggregate);
        bigMap.append(value);
        bigMaps.put(name, bigMap);
    }

    @Override
    protected <T> void setBigMapItem(String name, String key, T newValue) {
        bigMaps.get(name).put(key, newValue);
    }

    @Override
    protected <T> T getBigMapItem(String name, String key) {
        return (T)bigMaps.get(name).get(key);
    }

    public Collection<String> listBigMaps() {
        return new ArrayList<String>(bigMaps.keySet());
    }

    public <T> BigMapIterator<T> getBigMapIterator(String name) {
        return bigMaps.get(name).iterator();
    }

    public <T> BigMapChunkValue<T> getChunkForSubmit(String name, String fromKey, int count) {

        return new BigMapChunkValue<T>(bigMaps.get(name).getAggregate(),
                bigMaps.get(name).getChunkForSubmit(fromKey, count));
    }

    public void removeBigMap(String name) {
        bigMaps.remove(name);
    }
}
