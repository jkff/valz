package org.valz.util.protocol.messages;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.ParserException;
import org.valz.util.backends.RemoteReadException;
import org.valz.util.backends.RemoteWriteException;
import org.valz.util.protocol.ConnectionException;
import org.valz.util.protocol.HttpConnector;

import java.io.StringReader;

public class ResponseParser {

    private final String url;
    private final AggregateRegistry registry;

    public ResponseParser(String url, AggregateRegistry registry) {
        this.url = url;
        this.registry = registry;
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
                HttpConnector.post(url, InteractionType.requestToJson(type, request, registry).render(false));
        JSONValue responseJson = null;
        responseJson = new JSONParser(new StringReader(response)).nextValue();
        return (O)InteractionType.responseFromJson(responseJson, registry).second;
    }
}
