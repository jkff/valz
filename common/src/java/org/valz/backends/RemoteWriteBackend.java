package org.valz.backends;

import org.valz.model.Aggregate;
import org.valz.model.AggregateRegistry;
import org.valz.protocol.messages.InteractionType;
import org.valz.protocol.messages.SubmitBigMapRequest;
import org.valz.protocol.messages.SubmitRequest;

import java.util.Map;

public class RemoteWriteBackend implements WriteBackend {
    private final RemoteConnector remoteConnector;


    public RemoteWriteBackend(String serverURL, AggregateRegistry aggregateRegistry) {
        this.remoteConnector = new RemoteConnector(serverURL, aggregateRegistry);
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException {
        getDataResponse(InteractionType.SUBMIT, new SubmitRequest<T>(name, aggregate, value));
    }

    public <T> void submitBigMap(String name, Aggregate<T> aggregate, Map<String, T> value) throws
            RemoteWriteException {
        getDataResponse(InteractionType.SUBMIT_BIG_MAP, new SubmitBigMapRequest<T>(name, aggregate, value));
    }

    private <I, O> O getDataResponse(InteractionType<I, O> type, I request) throws RemoteWriteException {
        return remoteConnector.getWriteDataResponse(type, request);
    }
}
