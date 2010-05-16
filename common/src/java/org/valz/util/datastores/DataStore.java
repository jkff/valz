package org.valz.util.datastores;

import org.valz.util.aggregates.BigMap;
import org.valz.util.aggregates.BigMapIterator;
import org.valz.util.aggregates.Value;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.messages.BigMapChunkValue;

import java.util.Collection;
import java.util.Map;

public interface DataStore {

    <T> void submit(String name, Aggregate<T> aggregate, T value);

    Collection<String> listVars();

    <T> Value<T> getValue(String name);

    void removeAggregate(String name);




    <T> void submitBigMap(String name, Aggregate<T> aggregate, Map<String, T> value);

    Collection<String> listBigMaps();

    <T> BigMapIterator<T> getBigMapIterator(String name);

    <T> BigMapChunkValue<T> getChunkForSubmit(String name, String fromKey, int count);

    void removeBigMap(String name);
}
