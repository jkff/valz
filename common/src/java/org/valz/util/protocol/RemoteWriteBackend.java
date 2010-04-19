package org.valz.util.protocol;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.messages.RequestMessage;
import org.valz.util.protocol.messages.ResponseMessage;
import org.valz.util.protocol.messages.SubmitRequest;

import java.io.IOException;
import java.util.Collection;

public class RemoteWriteBackend implements WriteBackend {
    private final String serverUrl;


    public RemoteWriteBackend(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException {
        getDataResponse(InteractionType.SUBMIT,
                new SubmitRequest<T>(name, aggregate, value));
        // TODO: save val at exception to queue and try send later
    }

    private <I,O> O getDataResponse(InteractionType<I,O> requestType, I request) throws RemoteWriteException {
        try {
            String response = HttpConnector.post(serverUrl, new JSONSerializer().serialize(new RequestMessage<I>(requestType, request)));
            ResponseMessage<O> responseMessage = new JSONDeserializer<ResponseMessage<O>>().deserialize(response);
            return responseMessage.data;
        } catch (IOException e) {
            throw new RemoteWriteException(e);
        }
    }
}