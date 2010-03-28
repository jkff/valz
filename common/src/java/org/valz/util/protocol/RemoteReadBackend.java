package org.valz.util.protocol;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateParser;
import org.valz.util.protocol.HttpConnector;
import org.valz.util.protocol.MessageType;
import org.valz.util.protocol.ReadBackend;
import org.valz.util.protocol.RemoteReadException;

import java.io.IOException;
import java.util.*;

import static org.valz.util.json.JSONBuilder.makeJson;

/**
 * Created on: 28.03.2010 10:44:42
 */
public class RemoteReadBackend implements ReadBackend {
    private ReadConfiguration conf;
    private AggregateParser aggregateParser;

    public RemoteReadBackend(ReadConfiguration conf, AggregateParser aggregateParser) {
        this.conf = conf;
        this.aggregateParser = aggregateParser;
    }

    public JSONObject getAggregateDescription(String name) throws RemoteReadException {
        return getAggregateDescription(conf.getServerUrls().get(0), name);
    }

    public Object getValue(String name) throws RemoteReadException {
        List<Object> parts = new ArrayList<Object>();
        for(String serverUrl : conf.getServerUrls()) {
            parts.add(getValue(serverUrl, name));
        }

        Aggregate agg = aggregateParser.parse(getAggregateDescription(name));
       
        return agg.reduce(parts.iterator());
    }

    public Collection<String> listVars() throws RemoteReadException {
        Set<String> res = new TreeSet<String>();
        for(String serverUrl : conf.getServerUrls()) {
            res.addAll(listVars(serverUrl));
        }
        return res;
    }

    private JSONObject getAggregateDescription(String serverUrl, String name) throws RemoteReadException {
        String response;
        try {
            response = HttpConnector.post(serverUrl,
                    makeJson("messageType", MessageType.GET_AGGREGATE.name(), "name", name).toJSONString());
        } catch (IOException e) {
            throw new RemoteReadException("Server unreachable: " + serverUrl, e);
        }
        try {
            return (JSONObject) new JSONParser().parse(response);
        } catch (ParseException e) {
            throw new RemoteReadException("Malformed server response: "+response, e);
        }

    }

    private Object getValue(String serverUrl, String name) throws RemoteReadException {
        String response;
        try {
            response = HttpConnector.post(serverUrl,
                    makeJson("messageType", MessageType.GET_VALUE.name(), "name", name).toJSONString());
        } catch (IOException e) {
            throw new RemoteReadException("Server unreachable: " + serverUrl, e);
        }
        try {
            return ((JSONObject) new JSONParser().parse(response)).get("value");
        } catch (ParseException e) {
            throw new RemoteReadException("Malformed server response: "+response, e);
        }
    }

    private List<String> listVars(String serverUrl) throws RemoteReadException {
        String response = null;
        try {
            response = HttpConnector.post(serverUrl,
                    makeJson("messageType", MessageType.LIST_VARS.name()).toJSONString());
        } catch (IOException e) {
            throw new RemoteReadException("Server unreachable: " + serverUrl, e);
        }
        List<String> res = new ArrayList<String>();
        try {
            for(Object obj : (JSONArray)new JSONParser().parse(response)) res.add(obj.toString());
        } catch (ParseException e) {
            throw new RemoteReadException("Malformed server response: "+response, e);
        }
        return res;
    }
}
