package org.valz.util.backends;

import org.valz.util.aggregates.Value;
import org.valz.util.aggregates.Aggregate;

import java.util.Collection;

public interface ReadBackend {
    Aggregate<?> getAggregate(String name) throws RemoteReadException;

    Value getValue(String name) throws RemoteReadException;

    Collection<String> listVars() throws RemoteReadException;

    void removeAggregate(String name) throws RemoteReadException;
}
