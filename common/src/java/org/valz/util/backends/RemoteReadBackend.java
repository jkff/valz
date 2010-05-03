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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RemoteReadBackend implements ReadBackend {
    private final List<String> readServerUrls;
    private final AggregateRegistry registry;

    public RemoteReadBackend(List<String> readServerUrls, AggregateRegistry registry) {
        this.readServerUrls = readServerUrls;
        this.registry = registry;
    }

    public Aggregate<?> getAggregate(String name) throws RemoteReadException {
        Aggregate<?> prevAggregate = null;
        for (String url : readServerUrls) {
            Aggregate<?> aggregate = getDataResponse(url, InteractionType.GET_AGGREGATE, name);
            if (prevAggregate == null) {
                prevAggregate = aggregate;
            } else if (!aggregate.equals(prevAggregate)) {
                throw new RemoteReadException("Different servers contains different aggregates with same name.");
            }
        }
        return prevAggregate;
    }

    public Value getValue(String name) throws RemoteReadException {
        Value prevValue = null;
        for (String url : readServerUrls) {
            Value<?> value = getDataResponse(url, InteractionType.GET_VALUE, name);
            if (prevValue == null) {
                prevValue = value;
            } else if (!value.getAggregate().equals(prevValue.getAggregate())) {
                throw new RemoteReadException("Different servers contains different aggregates with same name.");
            } else {
                prevValue = new Value(prevValue.getAggregate(),
                        prevValue.getAggregate().reduce(prevValue.getValue(), value.getValue()));
            }
        }
        return prevValue;
    }

    public Collection<String> listVars() throws RemoteReadException {
        Set<String> set = new HashSet<String>();
        for (String url : readServerUrls) {
            Collection<String> collection = getDataResponse(url, InteractionType.LIST_VARS, null);
            set.addAll(collection);
        }
        return set;
    }

    public Void removeAggregate(String name) throws RemoteReadException {
        for (String url : readServerUrls) {
            getDataResponse(url, InteractionType.REMOVE_VALUE, name);
        }
        return null;
    }

    private <I, O> O getDataResponse(String url, InteractionType<I, O> type, I request) throws RemoteReadException {
        try {
            String response =
                    HttpConnector.post(url, InteractionType.requestToJson(type, request, registry).render(false));
            JSONValue responseJson = new JSONParser(new StringReader(response)).nextValue();
            return (O)InteractionType.responseFromJson(responseJson, registry).second;
        } catch (Exception e) {
            throw new RemoteReadException(e);
        }
    }
}
