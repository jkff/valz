package org.valz.api;

import org.valz.util.MessageType;

import java.io.IOException;

public class ValImpl<T> implements Val<T> {
    private final String _name;

    ValImpl(String name) {
        _name = name;
    }


    public void submit(T sample) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("messageType=%s", MessageType.SUBMIT));
            sb.append(String.format("&name=%s", _name));
            sb.append(String.format("&value=%s", sample));

            String response = HttpConnector.post(sb.toString());
            System.out.println(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
