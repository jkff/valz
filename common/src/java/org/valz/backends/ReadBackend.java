package org.valz.backends;

import org.valz.model.BigMapIterator;
import org.valz.model.Sample;

import java.util.Collection;

public interface ReadBackend {
    Sample getValue(String name) throws RemoteReadException;

    Collection<String> listVals() throws RemoteReadException;

    Collection<String> listBigMaps() throws RemoteReadException;

    <T> BigMapIterator<T> getBigMapIterator(String name, String fromKey) throws RemoteReadException;
}
