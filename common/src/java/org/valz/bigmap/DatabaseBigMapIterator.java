package org.valz.bigmap;

import org.valz.backends.RemoteReadException;
import org.valz.datastores.DataStore;
import org.valz.protocol.messages.BigMapChunkValue;

public class DatabaseBigMapIterator<K, T> extends AbstractBigMapIterator<K, T> {

    private final DataStore dataStore;

    public DatabaseBigMapIterator(DataStore dataStore, String name, int chunkSize) {
        super(name, chunkSize);
        this.dataStore = dataStore;
    }

    public BigMapChunkValue<K, T> getNextChunk(String name, K fromKey, int count) throws
            RemoteReadException {
        return (BigMapChunkValue<K, T>)dataStore.getBigMapChunk(name, curKey, chunkSize);
    }
}