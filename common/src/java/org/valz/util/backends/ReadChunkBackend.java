package org.valz.util.backends;

import org.valz.util.protocol.messages.BigMapChunkValue;

public interface ReadChunkBackend extends ReadBackend {
    <T> BigMapChunkValue<T> getBigMapChunk(String name, String fromKey, int count) throws RemoteReadException;
}
