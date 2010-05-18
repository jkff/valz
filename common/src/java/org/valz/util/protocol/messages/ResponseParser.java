package org.valz.util.protocol.messages;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;
import org.valz.util.JsonUtils;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.ParserException;
import org.valz.util.backends.RemoteReadException;
import org.valz.util.backends.RemoteWriteException;
import org.valz.util.keytypes.KeyTypeRegistry;
import org.valz.util.protocol.ConnectionException;
import org.valz.util.protocol.HttpConnector;

import java.io.StringReader;

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
