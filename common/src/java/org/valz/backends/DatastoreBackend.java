package org.valz.backends;

import org.valz.datastores.DataStore;
import org.valz.model.Aggregate;
import org.valz.model.BigMapIterator;
import org.valz.model.Sample;
import org.valz.protocol.messages.BigMapChunkValue;

import java.util.Collection;
import java.util.Map;

public class DatastoreBackend implements ReadBackend, WriteBackend {
    private final DataStore dataStore;

    public DatastoreBackend(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public synchronized <T> void submit(String name, Aggregate<T> aggregate, T value)
            throws RemoteWriteException
    {
        try {
            dataStore.submit(name, aggregate, value);
        } catch (InvalidAggregateException e) {
            throw new RemoteWriteException(e);
        }
    }

    public synchronized <T> void submitBigMap(
            String name, Aggregate<T> aggregate, Map<String, T> value)
            throws RemoteWriteException
    {
        try {
            dataStore.submitBigMap(name, aggregate, value);
        } catch (InvalidAggregateException e) {
            throw new RemoteWriteException(e);
        }
    }

    public synchronized Sample getValue(String name) {
        return dataStore.getValue(name);
    }

    public synchronized Collection<String> listVals() {
        return dataStore.listVals();
    }

    public <T> BigMapIterator<T> getBigMapIterator(final String name, final String fromKey) throws RemoteReadException {
        return new BigMapIterator<T>() {
            private String lastKey = fromKey;
            public BigMapChunkValue<T> next(int count) {
                BigMapChunkValue<T> chunk = dataStore.getBigMapChunk(name, lastKey, count);
                if(!chunk.getValue().isEmpty())
                    lastKey = chunk.getValue().lastKey();
                // otherwise lastKey will not change and all subsequent next()'s
                // will return an empty chunk.
                return chunk;
            }
        };
    }

    public Collection<String> listBigMaps() throws RemoteReadException {
        return dataStore.listBigMaps();
    }
}
