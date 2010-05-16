package org.valz.util.backends;

import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.protocol.messages.*;

import java.util.*;

class RemoteBigMapIterator<T> implements Iterator<Map.Entry<String, T>> {

    private final ResponseParser responseParser;
    private final AggregateRegistry registry;
    private final String name;

    private Iterator<Map.Entry<String, T>> curIterator = null;
    private String curKey = "";
    private final int chunkSize;

    public RemoteBigMapIterator(String readServerUrl, AggregateRegistry registry, String name, int chunkSize) {
        this.chunkSize = chunkSize;
        this.responseParser = new ResponseParser(readServerUrl, registry);
        this.registry = registry;
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
        //TODO: make removing by chunks
        try {
            responseParser.getReadDataResponse(InteractionType.REMOVE_BIG_MAP_CHUNK,
                    new RemoveBigMapChunkRequest(name, Arrays.asList(curKey)));
        } catch (RemoteReadException e) {
            throw new RuntimeException(e);
        }
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
        BigMapChunkValue chunkValue = responseParser.getReadDataResponse(InteractionType.GET_BIG_MAP_CHUNK, new GetBigMapChunkRequest(name, curKey, chunkSize));
        Iterator<Map.Entry<String,T>> iter = (Iterator<Map.Entry<String,T>>)chunkValue.getValue().entrySet().iterator();
        if (iter.hasNext() && passFirstItem) {
            iter.next();
        }
        return iter;
    }
}
