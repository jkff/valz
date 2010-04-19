package org.valz.util.protocol;

import org.valz.util.aggregates.Aggregate;

import java.util.Collection;

/**
 * Created on: 27.03.2010 23:58:39
 */
public interface ReadBackend {
    Aggregate<?> getAggregate(String name) throws RemoteReadException;

    Object getValue(String name) throws RemoteReadException;

    Collection<String> listVars() throws RemoteReadException;
}
