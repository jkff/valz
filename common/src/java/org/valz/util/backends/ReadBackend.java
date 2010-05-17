package org.valz.util.backends;

import org.valz.util.aggregates.BigMapIterator;
import org.valz.util.aggregates.Value;
import org.valz.util.protocol.messages.BigMapChunkValue;

import java.util.Collection;

public interface ReadBackend {
    Value getValue(String name) throws RemoteReadException;

    Collection<String> listVars() throws RemoteReadException;

    void removeAggregate(String name) throws RemoteReadException;



    Collection<String> listBigMaps() throws RemoteReadException;

    <T> BigMapIterator<T> getBigMapIterator(String name) throws RemoteReadException;

    void removeBigMap(String name) throws RemoteReadException;
}
