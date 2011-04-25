package org.valz.server;

import com.sdicons.json.model.JSONValue;
import org.apache.log4j.Logger;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.valz.util.JsonUtils;
import org.valz.util.Pair;
import org.valz.model.AggregateRegistry;
import org.valz.model.Sample;
import org.valz.backends.*;
import org.valz.util.IOUtils;
import org.valz.protocol.messages.GetBigMapChunkRequest;
import org.valz.protocol.messages.InteractionType;
import org.valz.protocol.messages.SubmitBigMapRequest;
import org.valz.protocol.messages.SubmitRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static org.valz.util.IOUtils.readInputStream;

public class ValzHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(ValzHandler.class);


    private final AggregateRegistry aggregateRegistry;
    private final ReadBackend readBackend;
    private final WriteBackend writeBackend;

    public ValzHandler(ReadBackend readBackend, WriteBackend writeBackend,
                       AggregateRegistry aggregateRegistry) {
        this.readBackend = readBackend;
        this.writeBackend = writeBackend;
        this.aggregateRegistry = aggregateRegistry;
    }

    public void handle(String target, HttpServletRequest request, HttpServletResponse response,
                       int dispatch) throws IOException, ServletException {

        response.setContentType("text/html");
        try {
            String reqStr = readInputStream(request.getInputStream(), "UTF-8");
            JSONValue requestJson = JsonUtils.jsonFromString(reqStr);
            Pair<InteractionType, Object> typeAndData =
                    InteractionType.requestFromJson(requestJson, aggregateRegistry);
            InteractionType t = typeAndData.first;
            Object data = typeAndData.second;


            if (t == InteractionType.SUBMIT) {
                if (!(data instanceof SubmitRequest)) {
                    throw new BadRequestException("Data is not valid submit request.");
                }
                SubmitRequest submitRequest = (SubmitRequest)data;
                writeBackend.submit(submitRequest.getName(), submitRequest.getAggregate(),
                        submitRequest.getValue());
                answer(response.getOutputStream(), InteractionType.SUBMIT, null);
            } else if (InteractionType.LIST_VALS.equals(t)) {
                answer(response.getOutputStream(), InteractionType.LIST_VALS, readBackend.listVals());
            } else if (InteractionType.GET_VALUE.equals(t)) {
                String name = (String)data;
                answer(response.getOutputStream(), InteractionType.GET_VALUE,
                        ((Sample<?>) readBackend.getValue(name)));
            } else if (InteractionType.SUBMIT_BIG_MAP.equals(t)) {
                if (!(data instanceof SubmitBigMapRequest)) {
                    throw new BadRequestException("Data is not valid submit big map request.");
                }
                SubmitBigMapRequest submitBigMapRequest = (SubmitBigMapRequest)data;
                writeBackend.submitBigMap(submitBigMapRequest.getName(),
                        submitBigMapRequest.getAggregate(), submitBigMapRequest.getValue());
                answer(response.getOutputStream(), InteractionType.SUBMIT_BIG_MAP, null);
            } else if (InteractionType.LIST_BIG_MAPS.equals(t)) {
                answer(response.getOutputStream(), InteractionType.LIST_BIG_MAPS,
                        readBackend.listBigMaps());
            } else if (InteractionType.GET_BIG_MAP_CHUNK.equals(t)) {
                GetBigMapChunkRequest chunkRequest = (GetBigMapChunkRequest)data;
                answer(response.getOutputStream(), InteractionType.GET_BIG_MAP_CHUNK,
                        readBackend.getBigMapIterator(chunkRequest.name, chunkRequest.fromKey)
                                   .next(chunkRequest.count));
            } else {
                throw new BadRequestException("Unknown request type.");
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (BadRequestException e) {
            log.error("BadRequestException.", e);
            IOUtils.writeOutputStream(response.getOutputStream(), e.getMessage(), "utf-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (RemoteReadException e) {
            log.error("RemoteReadException.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (RemoteWriteException e) {
            log.error("RemoteWriteException.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unrecognized error.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        log.info("The request was completed.");
        ((Request)request).setHandled(true);
    }


    private <T> void answer(OutputStream out, InteractionType<?, T> messageType, T data) throws IOException {
        IOUtils.writeOutputStream(out,
                InteractionType.responseToJson(messageType, data, aggregateRegistry).render(false), "utf-8");
    }
}
