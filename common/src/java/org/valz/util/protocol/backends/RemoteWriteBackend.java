package org.valz.util.protocol.backends;

import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.ConnectionException;
import org.valz.util.protocol.HttpConnector;
import org.valz.util.protocol.messages.InteractionType;
import org.valz.util.protocol.messages.SubmitRequest;

import java.io.StringReader;

public class RemoteWriteBackend implements WriteBackend {
    private final WriteConfiguration conf;
    private final AggregateRegistry registry;


    public RemoteWriteBackend(WriteConfiguration conf, AggregateRegistry registry) {
        this.conf = conf;
        this.registry = registry;
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException {
        getDataResponse(InteractionType.SUBMIT, new SubmitRequest<T>(name, aggregate, value));
    }

    private <I, O> O getDataResponse(InteractionType<I, O> type, I request) throws RemoteWriteException {
        try {
            String response = HttpConnector
                    .post(conf.getServerURL(), InteractionType.requestToJson(type, request, registry).render(false));
            JSONValue responseJson = new JSONParser(new StringReader(response)).nextValue();
            return (O)InteractionType.responseFromJson(responseJson, registry).second;
        } catch (ConnectionException e) {
            throw new ConnectionRemoteWriteException(e);
        } catch (Exception e) {
            throw new RemoteWriteException(e);
        }
    }
}