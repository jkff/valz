package org.valz.util.backends;

import org.valz.util.protocol.messages.BigMapChunkValue;

public interface ReadChunkBackend extends ReadBackend {
    <K, T> BigMapChunkValue<K, T> getBigMapChunk(String name, K fromKey, int count) throws RemoteReadException;
}
