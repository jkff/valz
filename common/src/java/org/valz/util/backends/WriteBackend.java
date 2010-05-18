package org.valz.util.backends;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.keytypes.KeyType;

import java.util.Map;

public interface WriteBackend {
    <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException;

    <K, T> void submitBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> value) throws
            RemoteWriteException;
}