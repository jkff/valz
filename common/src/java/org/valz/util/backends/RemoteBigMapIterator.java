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

class RemoteBigMapIterator<T> implements BigMapIterator<T> {

    private final ResponseParser responseParser;
    private final String name;

    private Iterator<Map.Entry<String, T>> curIterator = null;
    private String curKey = "";
    private final int chunkSize;
    private Aggregate<T> aggregate;

    public RemoteBigMapIterator(ResponseParser responseParser, String name, int chunkSize) {
        this.chunkSize = chunkSize;
        this.responseParser = responseParser;
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

    private Iterator<Map.Entry<String, T>> getNextIterator(boolean passFirstItem) throws RemoteReadException {
        BigMapChunkValue chunkValue = responseParser.getReadDataResponse(InteractionType.GET_BIG_MAP_CHUNK,
                new GetBigMapChunkRequest(name, curKey, chunkSize));
        aggregate = chunkValue.getAggregate();
        Iterator<Map.Entry<String, T>> iter =
                (Iterator<Map.Entry<String, T>>)chunkValue.getValue().entrySet().iterator();
        if (iter.hasNext() && passFirstItem) {
            iter.next();
        }
        return iter;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }
}
