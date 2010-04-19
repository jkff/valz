package org.valz.util.protocol;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.messages.RequestMessage;
import org.valz.util.protocol.messages.ResponseMessage;
import org.valz.util.protocol.messages.SubmitRequest;

import java.io.IOException;
import java.util.Collection;

public class RemoteReadBackend implements ReadBackend {
    private final String serverUrl;


    public RemoteReadBackend(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public Aggregate<?> getAggregate(String name) throws RemoteReadException {
        return getDataResponse(InteractionType.GET_AGGREGATE, name);
    }

    public Object getValue(String name) throws RemoteReadException {
        return getDataResponse(InteractionType.GET_VALUE, name);
    }

    public Collection<String> listVars() throws RemoteReadException {
        return getDataResponse(InteractionType.LIST_VARS, null);
    }

    private <I,O> O getDataResponse(InteractionType<I,O> requestType, I request) throws RemoteReadException {
        try {
            String response = HttpConnector.post(serverUrl, new JSONSerializer().serialize(new RequestMessage<I>(requestType, request)));
            ResponseMessage<O> responseMessage = new JSONDeserializer<ResponseMessage<O>>().deserialize(response);
            return responseMessage.data;
        } catch (IOException e) {
            throw new RemoteReadException(e);
        }
    }
}
