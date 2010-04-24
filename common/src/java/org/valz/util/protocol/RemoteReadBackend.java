package org.valz.util.protocol;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.sdicons.json.mapper.MapperException;
import com.sdicons.json.parser.JSONParser;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.messages.RequestMessage;
import org.valz.util.protocol.messages.ResponseMessage;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

public class RemoteReadBackend implements ReadBackend {
    private final String serverUrl;
    private final AggregateRegistry registry;


    public RemoteReadBackend(String serverUrl, AggregateRegistry registry) {
        this.serverUrl = serverUrl;
        this.registry = registry;
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

    private <I, O> O getDataResponse(InteractionType<I, O> requestType, I request) throws RemoteReadException {
        try {
            String response =
                    HttpConnector.post(serverUrl, new RequestMessage<I>(requestType, request).toJson().render(false));
            ResponseMessage<O> responseMessage = ResponseMessage.parse(registry, new JSONParser(new StringReader(response)) .nextValue());
            return responseMessage.getData();
        } catch (IOException e) {
            throw new RemoteReadException(e);
        } catch (MapperException e) {
            throw new RemoteReadException(e);
        } catch (RecognitionException e) {
            throw new RemoteReadException(e);
        } catch (TokenStreamException e) {
            throw new RemoteReadException(e);
        }
    }
}
