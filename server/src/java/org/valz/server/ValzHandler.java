package org.valz.server;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.valz.util.protocol.MessageType;
import org.valz.util.aggregates.IntSum;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.valz.util.io.IOUtils.readInputStream;
import static org.valz.util.json.JSONBuilder.json;

public class ValzHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(ValzHandler.class);

    private ValzBackend backend;
    
    public ValzHandler(ValzBackend backend) {
        this.backend = backend;
    }

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
        response.setContentType("text/html");
        try {
            String reqStr = readInputStream(request.getInputStream(), "UTF-8");
            JSONObject obj = (JSONObject)new JSONParser().parse(reqStr);
            String messageString = (String) obj.get("messageType");
            MessageType messageType = MessageType.valueOf(messageString);

            switch (messageType) {
                case SUBMIT: {
                    String name = (String) obj.get("name");
                    JSONObject spec = (JSONObject) obj.get("aggregate");
                    int value = Integer.parseInt((String) obj.get("value"));

                    backend.submit(name, spec, value);
                }
                break;
                case LIST_VARS: {
                    JSONArray arr = new JSONArray();
                    arr.addAll(backend.listVars());
                    Writer out = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
                    arr.writeJSONString(out);
                    out.close();
                }
                break;
                case GET_VALUE: {
                    String name = (String) obj.get("name");
                    Object value = backend.getValue(name);
                    if(value == null) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    } else {
                        Writer out = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
                        json("value", value).writeJSONString(out);
                        out.close();
                    }
                }
                break;
            }

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        ((Request) request).setHandled(true);
    }
}
