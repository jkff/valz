package org.valz.datastores;

import org.valz.model.Sample;
import org.valz.model.Aggregate;
import org.valz.backends.InvalidAggregateException;
import org.valz.protocol.messages.BigMapChunkValue;

import java.util.Collection;
import java.util.Map;

public interface DataStore {
    <T> void submit(String name, Aggregate<T> aggregate, T value) throws InvalidAggregateException;

    Collection<String> listVals();

    <T> Sample<T> getValue(String name);

    void removeAggregate(String name);

    <T> void submitBigMap(String name, Aggregate<T> aggregate, Map<String, T> map)
            throws InvalidAggregateException;

    Collection<String> listBigMaps();

    // TODO Why doesn't BigMapChunkValue include aggregate
    <T> BigMapChunkValue<T> getBigMapChunk(String name, String fromKey, int count);

    <T> Aggregate<T> getBigMapAggregate(String name);

    // TODO This is not transactional!! Must be fixed to use a completely different mechanism.
    <T> BigMapChunkValue<T> popBigMapChunk(String name, String fromKey, int count);
}
