package org.valz.util.protocol;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.protocol.HttpConnector;
import org.valz.util.protocol.ReadBackend;
import org.valz.util.protocol.RemoteReadException;
import org.valz.util.protocol.messages.*;

import java.io.IOException;
import java.util.*;

/**
 * Created on: 28.03.2010 10:44:42
 */
public class RemoteReadBackend implements ReadBackend {
    private ReadConfiguration conf;
    private AggregateRegistry aggregateRegistry;

    public RemoteReadBackend(ReadConfiguration conf, AggregateRegistry aggregateRegistry) {
        this.conf = conf;
        this.aggregateRegistry = aggregateRegistry;
    }

    public Aggregate<?> getAggregate(String name) throws RemoteReadException {
        return getAggregate(conf.getServerUrls().get(0), name);
    }

    public Object getValue(String name) throws RemoteReadException {
        List<Object> parts = new ArrayList<Object>();
        for(String serverUrl : conf.getServerUrls()) {
            parts.add(getValue(serverUrl, name));
        }

        Aggregate ag = getAggregate(name);
        return ag.reduce(parts.iterator());
    }

    public Collection<String> listVars() throws RemoteReadException {
        Set<String> res = new TreeSet<String>();
        for(String serverUrl : conf.getServerUrls()) {
            res.addAll(listVars(serverUrl));
        }
        return res;
    }

    private Aggregate<?> getAggregate(String serverUrl, String name) throws RemoteReadException {
        String response;
        try {
            response = HttpConnector.post(serverUrl, new GetAggregateRequest(name).toMessageString());
            GetAggregateResponse message = (GetAggregateResponse) Message.parseMessageString(response);
            return message.getData();
        } catch (IOException e) {
            throw new RemoteReadException("Server unreachable: " + serverUrl, e);
        }
    }

    private Object getValue(String serverUrl, String name) throws RemoteReadException {
        String response;
        try {
            response = HttpConnector.post(serverUrl, new GetValueRequest(name).toMessageString());
            GetValueResponse message = (GetValueResponse) Message.parseMessageString(response);
            return message.getData();
        } catch (IOException e) {
            throw new RemoteReadException("Server unreachable: " + serverUrl, e);
        }
    }

    private Collection<String> listVars(String serverUrl) throws RemoteReadException {
        String response;
        try {
            response = HttpConnector.post(serverUrl, new ListVarsRequest().toMessageString());
            ListVarsResponse message = (ListVarsResponse) Message.parseMessageString(response);
            return message.getData();
        } catch (IOException e) {
            throw new RemoteReadException("Server unreachable: " + serverUrl, e);
        }
    }
}
