package org.valz.util.protocol;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.sdicons.json.mapper.JSONMapper;
import com.sdicons.json.mapper.MapperException;
import com.sdicons.json.parser.JSONParser;
import org.valz.util.AggregateRegistry;
import org.valz.util.Value;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.ParserException;
import org.valz.util.protocol.messages.InteractionType;
import org.valz.util.protocol.messages.RequestMessage;
import org.valz.util.protocol.messages.ResponseMessage;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

public class RemoteReadBackend implements ReadBackend {
    private final ReadConfiguration conf;
    private final AggregateRegistry registry;


    public RemoteReadBackend(ReadConfiguration conf, AggregateRegistry registry) {
        this.conf = conf;
        this.registry = registry;
    }

    public Aggregate<?> getAggregate(String name) throws RemoteReadException {
        return getDataResponse(InteractionType.GET_AGGREGATE, name);
    }

    public Value getValue(String name) throws RemoteReadException {
        return getDataResponse(InteractionType.GET_VALUE, name);
    }

    public Collection<String> listVars() throws RemoteReadException {
        return getDataResponse(InteractionType.LIST_VARS, null);
    }

    private <I, O> O getDataResponse(InteractionType<I, O> requestType, I request) throws RemoteReadException {
        try {
            String response =
                    HttpConnector.post(
                            conf.getServerUrls().get(0),
                            JSONMapper.toJSON(new RequestMessage<I>(requestType, request).toJson()).render(false));
            ResponseMessage<O> responseMessage = ResponseMessage.parse(registry, new JSONParser(new StringReader(response)).nextValue());
            return responseMessage.getData();
        } catch (IOException e) {
            throw new RemoteReadException(e);
        } catch (ParserException e) {
            throw new RemoteReadException(e);
        } catch (RecognitionException e) {
            throw new RemoteReadException(e);
        } catch (TokenStreamException e) {
            throw new RemoteReadException(e);
        } catch (MapperException e) {
            throw new RemoteReadException(e);
        }
    }
}
