package org.valz.util.protocol;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.HttpConnector;
import org.valz.util.protocol.RemoteWriteException;
import org.valz.util.protocol.WriteBackend;
import org.valz.util.protocol.messages.SubmitRequest;

import java.io.IOException;

/**
 * Created on: 28.03.2010 10:50:36
 */
public class RemoteWriteBackend implements WriteBackend {
    private WriteConfiguration conf;

    public RemoteWriteBackend(WriteConfiguration conf) {
        this.conf = conf;
    }

    public void submit(String name, Aggregate<?> aggregate, Object value) throws RemoteWriteException {
        try {
            HttpConnector.post(conf.getServerURL(),
                    new SubmitRequest(name, aggregate, value).toMessageString());
        } catch (IOException e) {
            throw new RemoteWriteException("Server unreachable: " + conf.getServerURL(), e);
            // TODO: save val to queue and try send later
        }
    }
}
