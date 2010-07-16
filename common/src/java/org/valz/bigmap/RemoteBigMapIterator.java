package org.valz.bigmap;

import org.valz.backends.RemoteReadException;
import org.valz.protocol.ResponseParser;
import org.valz.protocol.messages.BigMapChunkValue;
import org.valz.protocol.messages.GetBigMapChunkRequest;
import org.valz.protocol.messages.InteractionType;

public class RemoteBigMapIterator<K, T> extends AbstractBigMapIterator<K, T> {

    private final ResponseParser responseParser;

    public RemoteBigMapIterator(ResponseParser responseParser, String name, int chunkSize) {
        super(name, chunkSize);
        this.responseParser = responseParser;
    }

    public BigMapChunkValue<K, T> getNextChunk(String name, K fromKey, int count) throws
            RemoteReadException {
        return (BigMapChunkValue<K, T>)responseParser.getReadDataResponse(InteractionType.GET_BIG_MAP_CHUNK,
                new GetBigMapChunkRequest<K>(name, curKey, chunkSize, keyType));
    }
}