package org.valz.util.backends;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.keytypes.KeyType;
import org.valz.util.keytypes.KeyTypeRegistry;
import org.valz.util.protocol.messages.InteractionType;
import org.valz.util.protocol.messages.ResponseParser;
import org.valz.util.protocol.messages.SubmitBigMapRequest;
import org.valz.util.protocol.messages.SubmitRequest;

import java.util.Map;

public class RemoteWriteBackend implements WriteBackend {
    private final ResponseParser responseParser;


    public RemoteWriteBackend(String serverURL, KeyTypeRegistry keyTypeRegistry, AggregateRegistry aggregateRegistry) {
        this.responseParser = new ResponseParser(serverURL, keyTypeRegistry, aggregateRegistry);
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException {
        getDataResponse(InteractionType.SUBMIT, new SubmitRequest<T>(name, aggregate, value));
    }

    public <K, T> void submitBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> value) throws
            RemoteWriteException {
        getDataResponse(InteractionType.SUBMIT_BIG_MAP, new SubmitBigMapRequest<K, T>(name, keyType, aggregate, value));
    }

    private <I, O> O getDataResponse(InteractionType<I, O> type, I request) throws RemoteWriteException {
        return responseParser.getWriteDataResponse(type, request);
    }
}