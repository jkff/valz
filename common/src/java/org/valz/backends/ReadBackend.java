package org.valz.backends;

import org.valz.aggregates.Sample;
import org.valz.protocol.messages.BigMapChunkValue;

import java.util.Collection;

public interface ReadBackend {
    Sample getValue(String name) throws RemoteReadException;

    Collection<String> listVars() throws RemoteReadException;

    void removeAggregate(String name) throws RemoteReadException, RemoteWriteException;

    Collection<String> listBigMaps() throws RemoteReadException;

    <K,T> BigMapChunkValue<K, T> getBigMapChunk(String name, K fromKey, int count) throws RemoteReadException;
}
