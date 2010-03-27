package org.valz.server;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.valz.util.io.IOUtils;
import org.valz.util.protocol.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.valz.util.io.IOUtils.readInputStream;

public class ValzHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(ValzHandler.class);

    private final ValzBackend backend;

    public ValzHandler(ValzBackend backend) {
        this.backend = backend;
    }

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
        response.setContentType("text/html");
        try {
            String reqStr = readInputStream(request.getInputStream(), "UTF-8");
            Message message = Message.parseMessageString(reqStr);

            switch (message.getMessageType()) {
                case SUBMIT_REQUEST: {
                    MessageSubmitRequest recvMsg = (MessageSubmitRequest) message;
                    backend.submit(recvMsg.getName(), recvMsg.getAggregate(), recvMsg.getValue());
                }
                break;
                case LIST_VARS_REQUEST: {
                    MessageListVarsRequest recvMsg = (MessageListVarsRequest) message;
                    Message sendMsg = new MessageListVarsResponse(backend.listVars());
                    IOUtils.writeOutputStream(response.getOutputStream(), sendMsg.toMessageString(), "UTF-8");
                }
                break;
                case GET_VALUE_REQUEST: {
                    MessageGetValueRequest recvMsg = (MessageGetValueRequest) message;
                    Message sendMsg = new MessageGetValueResponse(
                            backend.getAggregate(recvMsg.getName()),
                            backend.getValue(recvMsg.getName()));
                    IOUtils.writeOutputStream(response.getOutputStream(), sendMsg.toMessageString(), "UTF-8");
                }
                break;
                case GET_VALUE_RESPONSE:
                case LIST_VARS_RESPONSE:
                    throw new IllegalArgumentException("Can not serve this request.");
            }

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        ((Request) request).setHandled(true);
    }
}
