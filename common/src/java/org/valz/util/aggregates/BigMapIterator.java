package org.valz.util.aggregates;

import org.valz.util.keytypes.KeyType;

import java.util.Iterator;
import java.util.Map;

public interface BigMapIterator<K,T> extends Iterator<Map.Entry<K, T>> {
    Aggregate<T> getAggregate();
    KeyType<K> getKeyType();
}