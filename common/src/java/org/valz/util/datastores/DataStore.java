package org.valz.util.datastores;

import org.valz.util.aggregates.BigMapIterator;
import org.valz.util.aggregates.Value;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.backends.InvalidAggregateException;
import org.valz.util.protocol.messages.BigMapChunkValue;

import java.util.Collection;
import java.util.Map;

public interface DataStore {

    <T> void submit(String name, Aggregate<T> aggregate, T value) throws InvalidAggregateException;

    Collection<String> listVars();

    <T> Value<T> getValue(String name);

    void removeAggregate(String name);




    <T> void submitBigMap(String name, Aggregate<T> aggregate, Map<String, T> map) throws InvalidAggregateException;

    Collection<String> listBigMaps();

    <T> BigMapChunkValue<T> getBigMapChunk(String name, String fromKey, int count);

    <T> Aggregate<T> getBigMapAggregate(String name);

    <T> BigMapChunkValue<T> getBigMapChunkForSubmit(String name, String fromKey, int count);

    void removeBigMap(String name);
}
