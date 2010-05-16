package org.valz.util.backends;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.protocol.messages.InteractionType;
import org.valz.util.protocol.messages.ResponseParser;
import org.valz.util.protocol.messages.SubmitBigMapRequest;
import org.valz.util.protocol.messages.SubmitRequest;

import java.util.Map;

public class RemoteWriteBackend implements WriteBackend {
    private final ResponseParser responseParser;
    private final AggregateRegistry registry;


    public RemoteWriteBackend(String serverURL, AggregateRegistry registry) {
        this.responseParser = new ResponseParser(serverURL, registry);
        this.registry = registry;
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException {
        getDataResponse(InteractionType.SUBMIT, new SubmitRequest<T>(name, aggregate, value));
    }

    public <T> void submitBigMap(String name, Aggregate<T> aggregate, Map<String, T> value) throws
            RemoteWriteException {
        getDataResponse(InteractionType.SUBMIT_BIG_MAP, new SubmitBigMapRequest<T>(name, aggregate, value));
    }

    private <I, O> O getDataResponse(InteractionType<I, O> type, I request) throws RemoteWriteException {
        return responseParser.getWriteDataResponse(type, request);
    }
}