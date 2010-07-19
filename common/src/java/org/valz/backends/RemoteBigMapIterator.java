package org.valz.backends;

import org.jetbrains.annotations.NotNull;
import org.valz.keytypes.KeyType;
import org.valz.model.Aggregate;
import org.valz.model.BigMapIterator;
import org.valz.protocol.messages.BigMapChunkValue;
import org.valz.protocol.messages.GetBigMapChunkRequest;
import org.valz.protocol.messages.InteractionType;

import java.util.*;

/**
 * Created on: 18.07.2010 21:05:45
 */
public class RemoteBigMapIterator<K,T> implements BigMapIterator<K,T> {
    private final List<RemoteConnector> remoteConnectors;
    private final String name;
    private final KeyType<K> keyType;
    private final Aggregate<T> aggregate;

    private PriorityQueue<Cursor> cursors;

    public RemoteBigMapIterator(List<RemoteConnector> remoteConnectors, String name, K fromKey)
        throws RemoteReadException
    {
        this.remoteConnectors = remoteConnectors;
        this.name = name;

        BigMapChunkValue<K,T> probe = remoteConnectors.get(0).getReadDataResponse(
                InteractionType.GET_BIG_MAP_CHUNK,
                new GetBigMapChunkRequest(name, null, null, 0));
        this.keyType = probe.getKeyType();
        this.aggregate = probe.getAggregate();

        this.cursors = new PriorityQueue<Cursor>();

        for (int i = 0; i < remoteConnectors.size(); ++i) {
            tryPullChunk(remoteConnectors, name, fromKey, i);
        }
    }

    private void tryPullChunk(List<RemoteConnector> remoteConnectors, String name, K fromKey, int i) throws RemoteReadException {
        RemoteConnector con = remoteConnectors.get(i);
        BigMapChunkValue<K, T> chunk = con.getReadDataResponse(
                InteractionType.GET_BIG_MAP_CHUNK, new GetBigMapChunkRequest(name, keyType, fromKey, 0));
        TreeMap<K, T> map = chunk.getValue();
        Iterator<Map.Entry<K, T>> it = map.entrySet().iterator();
        if (!it.hasNext())
            return;
        cursors.offer(new Cursor(it.next(), it, i));
    }

    public BigMapChunkValue<K, T> next(int count) throws RemoteReadException {
        TreeMap<K,T> res = new TreeMap<K,T>(keyType);
        Map.Entry<K,T> curEntry = null;
        while(!cursors.isEmpty()) {
            Cursor cursor = cursors.remove();
            if(curEntry == null) {
                curEntry = cursor.firstEntry;
            } else if(keyType.compare(curEntry.getKey(), cursor.firstEntry.getKey()) == 0) {
                curEntry.setValue(aggregate.reduce(curEntry.getValue(), cursor.firstEntry.getValue()));
            } else {
                res.put(curEntry.getKey(), curEntry.getValue());
                curEntry = null;
                if(res.size() == count)
                    break;
            }
            if(cursor.iter.hasNext()) {
                cursor.firstEntry = cursor.iter.next();
                cursors.offer(cursor);
            } else {
                tryPullChunk(remoteConnectors, name, curEntry.getKey(), cursor.originIndex);
            }
        }
        if(res.size() < count)
            res.put(curEntry.getKey(), curEntry.getValue());
        return new BigMapChunkValue<K,T>(keyType, aggregate, res);
    }

    private class Cursor implements Comparable<Cursor> {
        @NotNull
        Map.Entry<K,T> firstEntry;
        Iterator<Map.Entry<K,T>> iter;
        int originIndex;

        private Cursor(Map.Entry<K,T> firstEntry, Iterator<Map.Entry<K, T>> iter, int originIndex) {
            this.firstEntry = firstEntry;
            this.iter = iter;
            this.originIndex = originIndex;
        }

        public int compareTo(Cursor o) {
            int cmp = keyType.compare(this.firstEntry.getKey(), o.firstEntry.getKey());
            if(cmp != 0)
                return cmp;
            if (originIndex > o.originIndex)
                return 1;
            if(originIndex == o.originIndex)
                return 0;
            return -1;
        }
    }
}
