package org.valz.util.backends;

import org.valz.util.backends.AbstractBigMapIterator;
import org.valz.util.backends.RemoteReadException;
import org.valz.util.datastores.DataStore;
import org.valz.util.protocol.messages.BigMapChunkValue;
import org.valz.util.protocol.messages.GetBigMapChunkRequest;
import org.valz.util.protocol.messages.InteractionType;
import org.valz.util.protocol.messages.ResponseParser;

import java.util.Iterator;
import java.util.Map;

class DatabaseBigMapIterator<T> extends AbstractBigMapIterator<T> {

    private final DataStore dataStore;

    public DatabaseBigMapIterator(DataStore dataStore, String name, int chunkSize) {
        super(name, chunkSize);
        this.dataStore = dataStore;
    }

    @Override
    protected BigMapChunkValue<T> getNextChunk(String name, String fromKey, int count) throws
            RemoteReadException {
        return dataStore.getBigMapChunk(name, curKey, chunkSize);
    }
}