package org.valz.util.protocol;

import org.valz.util.aggregates.Aggregate;

/**
 * Created on: 27.03.2010 23:58:39
 */
public interface WriteBackend {
    <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException;
}