package org.valz.api;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.protocol.MessageType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.valz.util.json.JSONBuilder.makeJson;

public final class Valz {
    private static Configuration conf;

    private Valz() {
    }

    public static synchronized void init(Configuration conf) {
        Valz.conf = conf;
    }

    public static synchronized <T> Val<T> register(
            final String name, final Aggregate<T> aggregate) {
        return new Val<T>() {
            public void submit(T sample) {
                try {
                    HttpConnector.post(conf.getServerURL(), makeJson(
                            "messageType", MessageType.SUBMIT_REQUEST.name(),
                            "name", name,
                            "aggregate", AggregateRegistry.toJson(aggregate),
                            "value", sample
                    ).toJSONString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static synchronized Object getValue(@NotNull String name) throws IOException {
        String response = HttpConnector.post(conf.getServerURL(), makeJson(
                "messageType", MessageType.GET_VALUE_REQUEST.name(),
                "name", name).toJSONString());
        try {
            return new JSONParser().parse(response);
        } catch (ParseException e) {
            throw new IOException("Malformed server response: " + response, e);
        }
    }

    public static synchronized List<String> listVars() throws IOException {
        String response = HttpConnector.post(conf.getServerURL(), makeJson(
                "messageType", MessageType.LIST_VARS_REQUEST.name()).toJSONString());
        List<String> res = new ArrayList<String>();
        try {
            for (Object obj : (JSONArray) new JSONParser().parse(response)) {
                res.add(obj.toString());
            }
        } catch (ParseException e) {
            throw new IOException("Malformed server response: " + response, e);
        }
        return res;
    }
}
