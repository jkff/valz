package org.valz.util.backends;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.BigMapIterator;
import org.valz.util.protocol.messages.BigMapChunkValue;
import org.valz.util.protocol.messages.GetBigMapChunkRequest;
import org.valz.util.protocol.messages.InteractionType;
import org.valz.util.protocol.messages.ResponseParser;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractBigMapIterator<T> implements BigMapIterator<T> {

    protected final String name;

    protected Iterator<Map.Entry<String, T>> curIterator = null;
    protected String curKey = "";
    protected final int chunkSize;
    protected Aggregate<T> aggregate;

    public AbstractBigMapIterator(String name, int chunkSize) {
        this.chunkSize = chunkSize;
        this.name = name;
    }

    public boolean hasNext() {
        return getIterator().hasNext();
    }

    public Map.Entry<String, T> next() {
        Map.Entry<String, T> entry = getIterator().next();
        curKey = entry.getKey();
        return entry;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private Iterator<Map.Entry<String, T>> getIterator() {
        if (curIterator == null || !curIterator.hasNext()) {
            try {
                curIterator = getNextIterator(curIterator != null);
            } catch (RemoteReadException e) {
                // return empty iterator
                return new TreeMap<String, T>().entrySet().iterator();
            }
        }
        return curIterator;
    }


    public Iterator<Map.Entry<String, T>> getNextIterator(boolean passFirstItem) throws RemoteReadException {
        BigMapChunkValue<T> chunkValue = getNextChunk(name, curKey, chunkSize);
        aggregate = chunkValue.getAggregate();
        Iterator<Map.Entry<String, T>> iter =
                (Iterator<Map.Entry<String, T>>)chunkValue.getValue().entrySet().iterator();
        if (iter.hasNext() && passFirstItem) {
            iter.next();
        }
        return iter;
    }


    protected abstract BigMapChunkValue<T> getNextChunk(String name, String fromKey, int count) throws
            RemoteReadException;
    
    public Aggregate<T> getAggregate() {
        return aggregate;
    }
}
