package org.valz.util.backends;

import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;
import org.valz.util.AggregateRegistry;
import org.valz.util.Value;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.HttpConnector;
import org.valz.util.protocol.messages.InteractionType;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;

public class RemoteReadBackend implements ReadBackend {
    private final List<String> serverUrls;
    private final AggregateRegistry registry;

    public RemoteReadBackend(List<String> serverUrls, AggregateRegistry registry) {
        this.serverUrls = serverUrls;
        this.registry = registry;
    }

    public Aggregate<?> getAggregate(String name) throws RemoteReadException {
        return getDataResponse(InteractionType.GET_AGGREGATE, name);
    }

    public Value getValue(String name) throws RemoteReadException {
        return getDataResponse(InteractionType.GET_VALUE, name);
    }

    public Collection<String> listVars() throws RemoteReadException {
        return getDataResponse(InteractionType.LIST_VARS, null);
    }

    public Void removeAggregate(String name) throws RemoteReadException {
        return getDataResponse(InteractionType.REMOVE_VALUE, name);
    }

    private <I, O> O getDataResponse(InteractionType<I, O> type, I request) throws RemoteReadException {
        try {
            String response = HttpConnector.post(serverUrls.get(0),
                    InteractionType.requestToJson(type, request, registry).render(false));
            JSONValue responseJson = new JSONParser(new StringReader(response)).nextValue();
            return (O)InteractionType.responseFromJson(responseJson, registry).second;
        } catch (Exception e) {
            throw new RemoteReadException(e);
        }
    }
}
