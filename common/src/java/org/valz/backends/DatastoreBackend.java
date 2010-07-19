package org.valz.backends;

import org.valz.datastores.DataStore;
import org.valz.keytypes.KeyType;
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

    public synchronized <K, T> void submitBigMap(
            String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> value)
            throws RemoteWriteException
    {
        try {
            dataStore.submitBigMap(name, keyType, aggregate, value);
        } catch (InvalidAggregateException e) {
            throw new RemoteWriteException(e);
        }
    }

    public synchronized Sample getValue(String name) {
        return dataStore.getValue(name);
    }

    public synchronized Collection<String> listVars() {
        return dataStore.listVars();
    }

    public <K, T> BigMapIterator<K,T> getBigMapIterator(final String name, final K fromKey) throws RemoteReadException {
        return new BigMapIterator<K, T>() {
            private K lastKey = fromKey;
            public BigMapChunkValue<K, T> next(int count) {
                BigMapChunkValue<K, T> chunk = dataStore.getBigMapChunk(name, lastKey, count);
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
