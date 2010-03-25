package org.valz.api;

import org.jetbrains.annotations.NotNull;
import org.valz.util.MessageType;
import org.valz.util.aggregates.Aggregate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class Valz {
    private static Configuration conf;

    private Valz() {
    }

    public static synchronized void init(Configuration conf) {
        Valz.conf = conf;
    }

    public static synchronized <T> Val<T> register(
            final String name, final Aggregate<T> aggregate) {

        return new ValImpl<T>(name);
    }

    public static synchronized <T> T getValue(@NotNull String name, ValueParser<T> valueParser) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("messageType=%s", MessageType.GET_VALUE));
        sb.append(String.format("&name=%s", name));

        String response = HttpConnector.post(sb.toString());
        System.out.println(String.format("'%s'", response));
        
        T value = valueParser.parse(response);
        return value;
    }

    public static synchronized List<String> listVars() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("messageType=%s", MessageType.LIST_VARS));

        String response = HttpConnector.post(sb.toString());
        System.out.println(response);

        ArrayList<String> list = new ArrayList<String>();
        BufferedReader rd = new BufferedReader(new StringReader(response));
        for (String line = rd.readLine(); line != null; line = rd.readLine()) {
            list.add(line);
        }

        return list;
    }
}
