package org.valz.backends;

import org.valz.aggregates.Sample;
import org.valz.bigmap.BigMapIterator;

import java.util.Collection;

public interface ReadBackend {
    Sample getValue(String name) throws RemoteReadException;

    Collection<String> listVars() throws RemoteReadException;

    void removeAggregate(String name) throws RemoteReadException;

    Collection<String> listBigMaps() throws RemoteReadException;

    <K, T> BigMapIterator<K, T> getBigMapIterator(String name) throws RemoteReadException;
}
