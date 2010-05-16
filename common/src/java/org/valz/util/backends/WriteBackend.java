package org.valz.util.backends;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.BigMap;

import java.util.Map;

public interface WriteBackend {
    <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException;

    <T> void submitBigMap(String name, Aggregate<T> aggregate, Map<String, T> value) throws RemoteWriteException;
}