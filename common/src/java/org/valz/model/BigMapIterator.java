package org.valz.model;

import org.valz.backends.RemoteReadException;
import org.valz.protocol.messages.BigMapChunkValue;

/**
 * Created on: 18.07.2010 20:56:23
 */
public interface BigMapIterator<T> {
    BigMapChunkValue<T> next(int count) throws RemoteReadException;
}
