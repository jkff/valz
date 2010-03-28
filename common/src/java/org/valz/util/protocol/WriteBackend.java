package org.valz.util.protocol;

import org.json.simple.JSONObject;

/**
 * Created on: 28.03.2010 10:43:43
 */
public interface WriteBackend {
    void submit(String name, JSONObject aggregateSpec, Object value) throws RemoteWriteException;
}
