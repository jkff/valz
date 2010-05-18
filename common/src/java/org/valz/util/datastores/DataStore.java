package org.valz.util.datastores;

import org.valz.util.aggregates.BigMapIterator;
import org.valz.util.aggregates.Value;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.backends.InvalidAggregateException;
import org.valz.util.keytypes.KeyType;
import org.valz.util.protocol.messages.BigMapChunkValue;

import java.util.Collection;
import java.util.Map;

public interface DataStore {

    <T> void submit(String name, Aggregate<T> aggregate, T value) throws InvalidAggregateException;

    Collection<String> listVars();

    <T> Value<T> getValue(String name);

    void removeAggregate(String name);




    <K, T> void submitBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> map) throws InvalidAggregateException;

    Collection<String> listBigMaps();

    <K, T> BigMapChunkValue<K, T> getBigMapChunk(String name, K fromKey, int count);

    <T> Aggregate<T> getBigMapAggregate(String name);

    <K> KeyType<K> getBigMapKeyType(String name);

    <K, T> BigMapChunkValue<K, T> getBigMapChunkForSubmit(String name, K fromKey, int count);

    void removeBigMap(String name);
}
