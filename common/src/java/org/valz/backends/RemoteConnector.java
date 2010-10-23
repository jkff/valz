package org.valz.backends;

import com.sdicons.json.model.JSONValue;
import org.valz.model.AggregateRegistry;
import org.valz.protocol.ConnectionException;
import org.valz.protocol.HttpConnector;
import org.valz.protocol.messages.InteractionType;
import org.valz.util.JsonUtils;

public class RemoteConnector {

    private final String url;
    private final AggregateRegistry aggregateRegistry;

    public RemoteConnector(String url, AggregateRegistry aggregateRegistry) {
        this.url = url;
        this.aggregateRegistry = aggregateRegistry;
    }

    public String getUrl() {
        return url;
    }

    public <I, O> O getReadDataResponse(InteractionType<I, O> type, I request) throws RemoteReadException {
        try {
            return getDataResponse(type, request);
        } catch (Exception e) {
            throw new RemoteReadException(e);
        }
    }

    public <I, O> O getWriteDataResponse(InteractionType<I, O> type, I request) throws RemoteWriteException {
        try {
            return getDataResponse(type, request);
        } catch (Exception e) {
            throw new RemoteWriteException(e);
        }
    }
    


    private <I, O> O getDataResponse(InteractionType<I, O> type, I request) throws ConnectionException {
        JSONValue json = InteractionType.requestToJson(
                type, request, aggregateRegistry);
        String response = HttpConnector.post(url, json.render(false));
        JSONValue responseJson = JsonUtils.jsonFromString(response);
        return (O)InteractionType.responseFromJson(responseJson, aggregateRegistry).second;
    }
}
