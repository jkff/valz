package org.valz.server;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.valz.util.io.IOUtils;
import org.valz.util.protocol.Backend;
import org.valz.util.protocol.messages.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.valz.util.io.IOUtils.readInputStream;

public class ValzHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(ValzHandler.class);

    private Backend backend;

    public ValzHandler(Backend backend) {
        this.backend = backend;
    }

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
        response.setContentType("text/html");
        try {
            String reqStr = readInputStream(request.getInputStream(), "UTF-8");
            Message message = Message.parseMessageString(reqStr);

            switch (message.getMessageType()) {
            case SUBMIT_REQUEST: {
                SubmitRequest recvMsg = (SubmitRequest) message;
                backend.submit(recvMsg.getData().name, recvMsg.getData().aggregate, recvMsg.getData().value);
            }
            break;

            case LIST_VARS_REQUEST: {
                ListVarsRequest recvMsg = (ListVarsRequest) message;
                Message sendMsg = new ListVarsResponse(backend.listVars());
                IOUtils.writeOutputStream(response.getOutputStream(), sendMsg.toMessageString(), "UTF-8");
            }
            break;

            case GET_VALUE_REQUEST: {
                GetValueRequest recvMsg = (GetValueRequest) message;
                Message sendMsg = new GetValueResponse(
                        backend.getValue(recvMsg.getData()));
                IOUtils.writeOutputStream(response.getOutputStream(), sendMsg.toMessageString(), "UTF-8");
            }
            break;

            case GET_AGGREGATE_REQUEST: {
                GetAggregateRequest recvMsg = (GetAggregateRequest) message;
                Message sendMsg = new GetAggregateResponse(
                        backend.getAggregate(recvMsg.getData()));
                IOUtils.writeOutputStream(response.getOutputStream(), sendMsg.toMessageString(), "UTF-8");
            }
            break;
            case GET_VALUE_RESPONSE:
            case LIST_VARS_RESPONSE:
            case GET_AGGREGATE_RESPONSE:
                throw new IllegalArgumentException("Can not serve this request.");
            }

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        ((Request) request).setHandled(true);
    }
}
