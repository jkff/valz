package org.valz.backends;

import org.valz.aggregates.Aggregate;
import org.valz.keytypes.KeyType;

import java.util.Map;

public interface WriteBackend {
    <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException;

    <K, T> void submitBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> value) throws
            RemoteWriteException;
}