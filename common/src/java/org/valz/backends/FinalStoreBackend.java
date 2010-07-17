package org.valz.backends;

import org.valz.aggregates.Aggregate;
import org.valz.aggregates.Sample;
import org.valz.datastores.DataStore;
import org.valz.keytypes.KeyType;
import org.valz.protocol.messages.BigMapChunkValue;

import java.util.Collection;
import java.util.Map;

public class FinalStoreBackend implements ReadBackend, WriteBackend {
    private final DataStore dataStore;

    public FinalStoreBackend(DataStore dataStore) {
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

    public synchronized void removeAggregate(String name) throws RemoteReadException {
        dataStore.removeAggregate(name);
    }

    public <K, T> BigMapChunkValue<K, T> getBigMapChunk(String name, K fromKey, int count) throws RemoteReadException {
        return dataStore.getBigMapChunk(name, fromKey, count);
    }

    public Collection<String> listBigMaps() throws RemoteReadException {
        return dataStore.listBigMaps();
    }
}
