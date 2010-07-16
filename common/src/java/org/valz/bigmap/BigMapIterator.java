package org.valz.bigmap;

import org.valz.aggregates.Aggregate;
import org.valz.backends.RemoteReadException;
import org.valz.keytypes.KeyType;
import org.valz.protocol.messages.BigMapChunkValue;

import java.util.Iterator;
import java.util.Map;

public interface BigMapIterator<K,T> extends Iterator<Map.Entry<K, T>> {
    Aggregate<T> getAggregate();
    KeyType<K> getKeyType();

    BigMapChunkValue<K, T> getNextChunk(String name, K fromKey, int count) throws
            RemoteReadException;
}