package org.valz.datastores;

import org.valz.aggregates.Sample;
import org.valz.aggregates.Aggregate;
import org.valz.backends.InvalidAggregateException;
import org.valz.keytypes.KeyType;
import org.valz.protocol.messages.BigMapChunkValue;

import java.util.Collection;
import java.util.Map;

public interface DataStore {
    <T> void submit(String name, Aggregate<T> aggregate, T value) throws InvalidAggregateException;

    Collection<String> listVars();

    <T> Sample<T> getValue(String name);

    void removeAggregate(String name);

    <K, T> void submitBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> map)
            throws InvalidAggregateException;

    Collection<String> listBigMaps();

    <K, T> BigMapChunkValue<K, T> getBigMapChunk(String name, K fromKey, int count);

    <T> Aggregate<T> getBigMapAggregate(String name);

    <K> KeyType<K> getBigMapKeyType(String name);

    // TODO: This is not transactional!! Must be fixed to use a completely different mechanism.
    <K, T> BigMapChunkValue<K, T> popBigMapChunk(String name, K fromKey, int count);
}
