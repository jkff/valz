package org.valz.util.protocol;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.sdicons.json.mapper.JSONMapper;
import com.sdicons.json.mapper.MapperException;
import com.sdicons.json.parser.JSONParser;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.ParserException;
import org.valz.util.protocol.messages.InteractionType;
import org.valz.util.protocol.messages.RequestMessage;
import org.valz.util.protocol.messages.ResponseMessage;
import org.valz.util.protocol.messages.SubmitRequest;

import java.io.IOException;
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
        // TODO: save val at exception to queue and try send later
    }

    private <I, O> O getDataResponse(InteractionType<I, O> requestType, I request) throws RemoteWriteException {
        try {
            String response = HttpConnector.post(conf.getServerURL(),
                    JSONMapper.toJSON(new RequestMessage<I>(requestType, request).toJson()).render(false));
            ResponseMessage<O> responseMessage =
                    ResponseMessage.parse(registry, new JSONParser(new StringReader(response)).nextValue());
            return responseMessage.getData();
        } catch (IOException e) {
            throw new RemoteWriteException(e);
        } catch (RecognitionException e) {
            throw new RemoteWriteException(e);
        } catch (TokenStreamException e) {
            throw new RemoteWriteException(e);
        } catch (ParserException e) {
            throw new RemoteWriteException(e);
        } catch (MapperException e) {
            throw new RemoteWriteException(e);
        }
    }
}