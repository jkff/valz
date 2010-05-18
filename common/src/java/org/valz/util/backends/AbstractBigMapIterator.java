package org.valz.util.backends;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.BigMapIterator;
import org.valz.util.keytypes.KeyType;
import org.valz.util.protocol.messages.BigMapChunkValue;
import org.valz.util.protocol.messages.GetBigMapChunkRequest;
import org.valz.util.protocol.messages.InteractionType;
import org.valz.util.protocol.messages.ResponseParser;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractBigMapIterator<K,T> implements BigMapIterator<K,T> {

    protected final String name;

    protected Iterator<Map.Entry<K, T>> curIterator = null;
    protected K curKey = null;
    protected final int chunkSize;
    protected KeyType<K> keyType;
    protected Aggregate<T> aggregate;

    public AbstractBigMapIterator(String name, int chunkSize) {
        this.chunkSize = chunkSize;
        this.name = name;
    }

    public boolean hasNext() {
        return getIterator().hasNext();
    }

    public Map.Entry<K, T> next() {
        Map.Entry<K, T> entry = getIterator().next();
        curKey = entry.getKey();
        return entry;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private Iterator<Map.Entry<K, T>> getIterator() {
        if (curIterator == null || !curIterator.hasNext()) {
            try {
                curIterator = getNextIterator(curIterator != null);
            } catch (RemoteReadException e) {
                // return empty iterator
                return new TreeMap<K, T>().entrySet().iterator();
            }
        }
        return curIterator;
    }


    public Iterator<Map.Entry<K, T>> getNextIterator(boolean passFirstItem) throws RemoteReadException {
        BigMapChunkValue<K, T> chunkValue = getNextChunk(name, curKey, chunkSize);
        keyType = chunkValue.getKeyType();
        aggregate = chunkValue.getAggregate();
        Iterator<Map.Entry<K, T>> iter =
                chunkValue.getValue().entrySet().iterator();
        if (iter.hasNext() && passFirstItem) {
            iter.next();
        }
        return iter;
    }


    protected abstract BigMapChunkValue<K, T> getNextChunk(String name, K fromKey, int count) throws
            RemoteReadException;
    

    public KeyType<K> getKeyType() {
        return keyType;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }
}
