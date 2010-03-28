package org.valz.util.protocol;

import org.json.simple.JSONObject;

import java.util.Collection;

/**
 * Created on: 28.03.2010 10:43:29
 */
public interface ReadBackend {
    JSONObject getAggregateDescription(String name) throws RemoteReadException;

    Object getValue(String name) throws RemoteReadException;

    Collection<String> listVars() throws RemoteReadException;
}
