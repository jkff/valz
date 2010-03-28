package org.valz.util.protocol;

import org.json.simple.JSONObject;
import org.valz.util.aggregates.Aggregate;

/**
 * Created on: 28.03.2010 10:43:43
 */
public interface WriteBackend {
    void submit(String name, Aggregate<?> aggregate, Object value) throws RemoteWriteException;
}
