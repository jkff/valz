package org.valz.util.backends;

import org.valz.util.protocol.messages.BigMapChunkValue;
import org.valz.util.protocol.messages.GetBigMapChunkRequest;
import org.valz.util.protocol.messages.InteractionType;
import org.valz.util.protocol.messages.ResponseParser;

class RemoteBigMapIterator<T> extends AbstractBigMapIterator<T> {

    private final ResponseParser responseParser;

    public RemoteBigMapIterator(ResponseParser responseParser, String name, int chunkSize) {
        super(name, chunkSize);
        this.responseParser = responseParser;
    }

    @Override
    protected BigMapChunkValue<T> getNextChunk(String name, String fromKey, int count) throws
            RemoteReadException {
        return (BigMapChunkValue<T>)responseParser.getReadDataResponse(InteractionType.GET_BIG_MAP_CHUNK,
                new GetBigMapChunkRequest(name, curKey, chunkSize));
    }
}