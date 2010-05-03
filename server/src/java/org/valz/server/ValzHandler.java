package org.valz.server;

import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;
import org.apache.log4j.Logger;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.valz.util.AggregateRegistry;
import org.valz.util.Pair;
import org.valz.util.Value;
import org.valz.util.io.IOUtils;
import org.valz.util.protocol.backends.ReadBackend;
import org.valz.util.protocol.backends.WriteBackend;
import org.valz.util.protocol.messages.InteractionType;
import org.valz.util.protocol.messages.SubmitRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import static org.valz.util.io.IOUtils.readInputStream;

public class ValzHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(ValzHandler.class);

    private final AggregateRegistry registry;
    private final ReadBackend readBackend;
    private final WriteBackend writeBackend;

    public ValzHandler(ReadBackend readBackend, WriteBackend writeBackend, AggregateRegistry registry) {
        this.readBackend = readBackend;
        this.writeBackend = writeBackend;
        this.registry = registry;
    }

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws
            IOException, ServletException {
        response.setContentType("text/html");
        try {
            String reqStr = readInputStream(request.getInputStream(), "UTF-8");

            JSONValue requestJson = new JSONParser(new StringReader(reqStr)).nextValue();
            Pair<InteractionType,Object> typeAndData = InteractionType.requestFromJson(requestJson, registry);

            InteractionType t = typeAndData.first;
            Object data = typeAndData.second;

            if (t == InteractionType.SUBMIT) {
                SubmitRequest submitRequest = (SubmitRequest)data;
                writeBackend.submit(submitRequest.getName(), submitRequest.getAggregate(), submitRequest.getValue());
                answer(response.getOutputStream(), InteractionType.SUBMIT, null);
            } else if (InteractionType.LIST_VARS.equals(t)) {
                answer(response.getOutputStream(), InteractionType.LIST_VARS, readBackend.listVars());
            } else if (InteractionType.GET_VALUE.equals(t)) {
                String name = (String)data;
                answer(response.getOutputStream(), InteractionType.GET_VALUE, ((Value<?>) readBackend.getValue(name)));
            } else if (InteractionType.GET_AGGREGATE.equals(t)) {
                String name = (String)data;
                answer(response.getOutputStream(), InteractionType.GET_AGGREGATE, readBackend.getAggregate(name));
            } else {
                throw new IllegalArgumentException("Unknown request type " + t);
            }

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        ((Request)request).setHandled(true);
    }


    private <T> void answer(OutputStream out, InteractionType<?,T> messageType, T data) throws IOException {
        IOUtils.writeOutputStream(out,
                InteractionType.responseToJson(messageType, data, registry).render(false),
                "utf-8");
    }
}
