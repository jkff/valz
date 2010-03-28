package org.valz.util.protocol;

import org.json.simple.JSONObject;
import org.valz.util.protocol.HttpConnector;
import org.valz.util.protocol.MessageType;
import org.valz.util.protocol.RemoteWriteException;
import org.valz.util.protocol.WriteBackend;

import java.io.IOException;

import static org.valz.util.json.JSONBuilder.makeJson;

/**
 * Created on: 28.03.2010 10:50:36
 */
public class RemoteWriteBackend implements WriteBackend {
    private WriteConfiguration conf;

    public RemoteWriteBackend(WriteConfiguration conf) {
        this.conf = conf;
    }

    public void submit(String name, JSONObject aggregateSpec, Object value) throws RemoteWriteException {
        try {
            HttpConnector.post(conf.getServerURL(), makeJson(
                    "messageType", MessageType.SUBMIT.name(),
                    "name",name, "aggregate", aggregateSpec,
                    "value", value
            ).toJSONString());
        } catch (IOException e) {
            throw new RemoteWriteException("Server unreachable: " + conf.getServerURL(), e);
        }
    }
}
