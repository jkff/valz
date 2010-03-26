package org.valz.api;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.aggregates.AggregateUtils;
import org.valz.util.protocol.MessageType;
import org.valz.util.aggregates.Aggregate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.valz.util.json.JSONBuilder.json;

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
                    JSONObject json = new JSONObject();
                    try {
                        json.put("method", AggregateUtils.findGetMethod(aggregate.getClass()).invoke(null));
                    } catch (IllegalAccessException e) {
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException("Non-serializable aggregate " + aggregate, e);
                    }
                    HttpConnector.post(conf.getServerURL(), json(
                            "messageType",MessageType.SUBMIT.name(),
                            "name",name, "aggregate", json,
                            "value",sample.toString()
                    ).toJSONString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static synchronized <T> T getValue(@NotNull String name, ValueParser<T> valueParser) throws IOException {
        String response = HttpConnector.post(conf.getServerURL(),
                json("messageType", MessageType.GET_VALUE.name(), "name", name).toJSONString());
        try {
            JSONObject jsonResponse = (JSONObject)new JSONParser().parse(response);
            return valueParser.parse(jsonResponse.get("value").toString());
        } catch (ParseException e) {
            throw new IOException("Malformed server response: "+response, e);
        }
    }

    public static synchronized List<String> listVars() throws IOException {
        String response = HttpConnector.post(conf.getServerURL(),
                json("messageType",MessageType.LIST_VARS.name()).toJSONString());
        List<String> res = new ArrayList<String>();
        try {
            for(Object obj : (JSONArray)new JSONParser().parse(response)) res.add(obj.toString());
        } catch (ParseException e) {
            throw new IOException("Malformed server response: "+response, e);
        }
        return res;
    }
}
