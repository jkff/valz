package org.valz.api;

import org.jetbrains.annotations.NotNull;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.*;

import java.io.IOException;
import java.util.Collection;

public final class Valz {
    private static Configuration conf = null;



    public static synchronized void init(@NotNull Configuration conf) {
        Valz.conf = conf;
    }

    public static synchronized <T> Val<T> register(
            @NotNull final String name, @NotNull final Aggregate<T> aggregate) {

        return new Val<T>() {
            public void submit(T sample) {
                try {
                    HttpConnector.post(conf.getServerURL(),
                            new MessageSubmitRequest(name, aggregate, sample).toMessageString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                    // TODO: save val to queue and try send later 
                }
            }
        };
    }

    public static synchronized Object getValue(@NotNull String name) throws IOException {
        String response = HttpConnector.post(conf.getServerURL(),
                new MessageGetValueRequest(name).toMessageString());
        MessageGetValueResponse message = (MessageGetValueResponse) Message.parseMessageString(response);
        return message.getValue();
    }

    public static synchronized Collection<String> listVars() throws IOException {
        String response = HttpConnector.post(conf.getServerURL(),
                new MessageListVarsRequest().toMessageString());
        MessageListVarsResponse message = (MessageListVarsResponse) Message.parseMessageString(response);
        return message.getVars();
    }



    private Valz() {
    }
}
