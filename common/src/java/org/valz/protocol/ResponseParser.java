package org.valz.protocol;

import com.sdicons.json.model.JSONValue;
import org.valz.aggregates.AggregateRegistry;
import org.valz.backends.RemoteReadException;
import org.valz.backends.RemoteWriteException;
import org.valz.keytypes.KeyTypeRegistry;
import org.valz.protocol.messages.InteractionType;
import org.valz.util.JsonUtils;

public class ResponseParser {

    private final String url;
    private final AggregateRegistry aggregateRegistry;
    private final KeyTypeRegistry keyTypeRegistry;

    public ResponseParser(String url, KeyTypeRegistry keyTypeRegistry, AggregateRegistry aggregateRegistry) {
        this.url = url;
        this.keyTypeRegistry = keyTypeRegistry;
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
    


    private <I, O> O getDataResponse(InteractionType<I, O> type, I request) throws Exception {
        String response =
                HttpConnector.post(url, InteractionType.requestToJson(type, request, keyTypeRegistry, aggregateRegistry).render(false));
        JSONValue responseJson = JsonUtils.jsonFromString(response);
        return (O)InteractionType.responseFromJson(responseJson, keyTypeRegistry, aggregateRegistry).second;
    }
}
