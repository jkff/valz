package org.valz.util.protocol;

import flexjson.JSONDeserializer;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.messages.*;

import java.io.IOException;
import java.util.Collection;

public class RemoteBackend implements Backend {
    private final String serverUrl;


    public RemoteBackend(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void submit(String name, Aggregate<?> aggregate, Object value) throws RemoteException {
        getDataResponse(RequestType.SUBMIT,
                new SubmitRequest(name, aggregate, value));
        // TODO: save val at exception to queue and try send later
    }

    public Aggregate<?> getAggregate(String name) throws RemoteException {
        return (Aggregate<?>)getDataResponse(RequestType.GET_AGGREGATE, name);
    }

    public Object getValue(String name) throws RemoteException {
        return getDataResponse(RequestType.GET_VALUE, name);
    }

    public Collection<String> listVars() throws RemoteException {
        return (Collection<String>)getDataResponse(RequestType.LIST_VARS, null);
    }

    private Object getDataResponse(RequestType requestType, Object data) throws RemoteException {
        try {
            String response = HttpConnector.post(serverUrl,
                        requestType,
                        data);
            ResponseMessage responseMessage = new JSONDeserializer<ResponseMessage>().deserialize(response);
            if (responseMessage.type != requestType.getResponseType()) {
                throw new RemoteException("Invalid requestMessage type.");
            }
            return responseMessage.data;
        } catch (IOException e) {
            throw new RemoteException(e);
        }
    }
}
