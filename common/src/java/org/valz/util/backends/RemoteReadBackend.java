package org.valz.util.backends;

import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;
import org.valz.util.aggregates.*;
import org.valz.util.protocol.ConnectionException;
import org.valz.util.protocol.HttpConnector;
import org.valz.util.protocol.messages.InteractionType;
import org.valz.util.protocol.messages.ResponseParser;

import java.io.StringReader;
import java.util.*;

public class RemoteReadBackend implements ReadBackend {
    private final List<ResponseParser> responseParsers = new ArrayList<ResponseParser>();
    private final AggregateRegistry registry;

    public RemoteReadBackend(List<String> readServerUrls, AggregateRegistry registry) {
        for (String url : readServerUrls) {
            responseParsers.add(new ResponseParser(url, registry));
        }
        this.registry = registry;
    }

    public Aggregate<?> getAggregate(String name) throws RemoteReadException {
        Aggregate<?> prevAggregate = null;
        for (int i=0; i<responseParsers.size(); i++) {
            Aggregate<?> aggregate = responseParsers.get(i).getReadDataResponse(InteractionType.GET_AGGREGATE, name);
            if (prevAggregate == null) {
                prevAggregate = aggregate;
            } else if (!aggregate.equals(prevAggregate)) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("%s", responseParsers.get(i).getUrl()));
                for (int j=1; j<i; j++) {
                    sb.append(String.format(", %s", responseParsers.get(i).getUrl()));
                }
                throw new RemoteReadException(String.format(
                        "Server %s contains aggregate %s with description, differs from servers: %s.",
                        responseParsers.get(i).getUrl(), name, sb.toString()));
            }
        }
        return prevAggregate;
    }

    public Value getValue(String name) throws RemoteReadException {
        Value prevValue = null;
        for (int i=0; i<responseParsers.size(); i++) {
            Value<?> value = responseParsers.get(i).getReadDataResponse(InteractionType.GET_VALUE, name);
            if (prevValue == null) {
                prevValue = value;
            } else if (!value.getAggregate().equals(prevValue.getAggregate())) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("%s", responseParsers.get(i).getUrl()));
                for (int j=1; j<i; j++) {
                    sb.append(String.format(", %s", responseParsers.get(i).getUrl()));
                }
                throw new RemoteReadException(String.format(
                        "Server %s contains aggregate %s with description, differs from servers: %s.",
                        responseParsers.get(i).getUrl(), name, sb.toString()));
            } else {
                prevValue = new Value(prevValue.getAggregate(),
                        prevValue.getAggregate().reduce(prevValue.getValue(), value.getValue()));
            }
        }
        return prevValue;
    }

    public Collection<String> listVars() throws RemoteReadException {
        Set<String> set = new HashSet<String>();
        for (ResponseParser responseParser : responseParsers) {
            Collection<String> collection = responseParser.getReadDataResponse(InteractionType.LIST_VARS, null);
            set.addAll(collection);
        }
        return set;
    }

    public void removeAggregate(String name) throws RemoteReadException {
        for (ResponseParser responseParser : responseParsers) {
            responseParser.getReadDataResponse(InteractionType.REMOVE_VALUE, name);
        }
    }

    public BigMap<?> getBigMap(String name) throws RemoteReadException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<String> listBigMaps() throws RemoteReadException {
        Set<String> set = new HashSet<String>();
        for (ResponseParser responseParser : responseParsers) {
            Collection<String> collection = responseParser.getReadDataResponse(InteractionType.LIST_BIG_MAPS, null);
            set.addAll(collection);
        }
        return set;
    }

    public void removeBigMap(String name) throws RemoteReadException {
        for (ResponseParser responseParser : responseParsers) {
            responseParser.getReadDataResponse(InteractionType.REMOVE_BIG_MAP, name);
        }
    }
}
