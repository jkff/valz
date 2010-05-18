package org.valz.util.backends;

import org.valz.util.protocol.messages.BigMapChunkValue;
import org.valz.util.protocol.messages.GetBigMapChunkRequest;
import org.valz.util.protocol.messages.InteractionType;
import org.valz.util.protocol.messages.ResponseParser;

class RemoteBigMapIterator<K, T> extends AbstractBigMapIterator<K, T> {

    private final ResponseParser responseParser;

    public RemoteBigMapIterator(ResponseParser responseParser, String name, int chunkSize) {
        super(name, chunkSize);
        this.responseParser = responseParser;
    }

    @Override
    protected BigMapChunkValue<K, T> getNextChunk(String name, K fromKey, int count) throws
            RemoteReadException {
        return (BigMapChunkValue<K, T>)responseParser.getReadDataResponse(InteractionType.GET_BIG_MAP_CHUNK,
                new GetBigMapChunkRequest<K>(name, curKey, chunkSize, keyType));
    }
}