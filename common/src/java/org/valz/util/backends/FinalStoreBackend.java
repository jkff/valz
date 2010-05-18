package org.valz.util.backends;

import org.apache.log4j.Logger;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.BigMapIterator;
import org.valz.util.aggregates.Value;
import org.valz.util.datastores.DataStore;
import org.valz.util.keytypes.KeyType;
import org.valz.util.protocol.messages.BigMapChunkValue;

import java.util.Collection;
import java.util.Map;

public class FinalStoreBackend implements ReadChunkBackend, WriteBackend {
    private static final Logger log = Logger.getLogger(FinalStoreBackend.class);


    private final DataStore dataStore;
    private int chunkSize;

    public FinalStoreBackend(DataStore dataStore, int chunkSize) {
        this.dataStore = dataStore;
        this.chunkSize = chunkSize;
    }

    public synchronized <T> void submit(String name, Aggregate<T> aggregate, T value) throws
            RemoteWriteException {
        try {
            dataStore.submit(name, aggregate, value);
        } catch (InvalidAggregateException e) {
            throw new RemoteWriteException(e);
        }
    }

    public synchronized <K, T> void submitBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate,
                                              Map<K, T> value) throws RemoteWriteException {
        try {
            dataStore.submitBigMap(name, keyType, aggregate, value);
        } catch (InvalidAggregateException e) {
            throw new RemoteWriteException(e);
        }
    }

    public synchronized Value getValue(String name) {
        return dataStore.getValue(name);
    }

    public synchronized Collection<String> listVars() {
        return dataStore.listVars();
    }

    public synchronized void removeAggregate(String name) throws RemoteReadException {
        dataStore.removeAggregate(name);
    }

    public <K, T> BigMapIterator<K, T> getBigMapIterator(String name) throws RemoteReadException {
        return new DatabaseBigMapIterator<K, T>(dataStore, name, chunkSize);
    }

    public Collection<String> listBigMaps() throws RemoteReadException {
        return dataStore.listBigMaps();
    }

    public void removeBigMap(String name) throws RemoteReadException {
        dataStore.removeBigMap(name);
    }

    public <K, T> BigMapChunkValue<K, T> getBigMapChunk(String name, K fromKey, int count) throws
            RemoteReadException {
        return dataStore.getBigMapChunk(name, fromKey, count);
    }
}
