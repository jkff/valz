package org.valz.backends;

import org.valz.model.BigMapIterator;
import org.valz.model.Sample;

import java.util.Collection;

public interface ReadBackend {
    Sample getValue(String name) throws RemoteReadException;

    Collection<String> listVars() throws RemoteReadException;

    Collection<String> listBigMaps() throws RemoteReadException;

    <K,T> BigMapIterator<K,T> getBigMapIterator(String name, K fromKey) throws RemoteReadException;
}
