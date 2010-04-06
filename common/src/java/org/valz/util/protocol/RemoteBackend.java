package org.valz.util.protocol;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.messages.RequestMessage;
import org.valz.util.protocol.messages.ResponseMessage;
import org.valz.util.protocol.messages.SubmitRequest;

import java.io.IOException;
import java.util.Collection;

public class RemoteBackend implements Backend {
    private final String serverUrl;


    public RemoteBackend(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteException {
        getDataResponse(InteractionType.SUBMIT,
                new SubmitRequest<T>(name, aggregate, value));
        // TODO: save val at exception to queue and try send later
    }

    public Aggregate<?> getAggregate(String name) throws RemoteException {
        return getDataResponse(InteractionType.GET_AGGREGATE, name);
    }

    public Object getValue(String name) throws RemoteException {
        return getDataResponse(InteractionType.GET_VALUE, name);
    }

    public Collection<String> listVars() throws RemoteException {
        return getDataResponse(InteractionType.LIST_VARS, null);
    }

    private <I,O> O getDataResponse(InteractionType<I,O> requestType, I request) throws RemoteException {
        try {
            String response = HttpConnector.post(serverUrl, new JSONSerializer().serialize(new RequestMessage<I>(requestType, request)));
            ResponseMessage<O> responseMessage = new JSONDeserializer<ResponseMessage<O>>().deserialize(response);
            return responseMessage.data;
        } catch (IOException e) {
            throw new RemoteException(e);
        }
    }
}
