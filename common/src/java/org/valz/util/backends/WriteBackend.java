package org.valz.util.backends;

import org.valz.util.aggregates.Aggregate;

public interface WriteBackend {
    <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException;
}