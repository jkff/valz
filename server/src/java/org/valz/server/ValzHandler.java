package org.valz.server;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.valz.util.MessageType;
import org.valz.util.aggregates.IntSum;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ValzHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(ValzHandler.class);

    private static Map<String, Integer> varsMap = new LinkedHashMap<String, Integer>();

    static {
        PropertyConfigurator.configure("log4j.properties");
    }

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {

        response.setContentType("text/html");
        try {
            String messageString = request.getParameter("messageType");
            MessageType messageType = MessageType.valueOf(messageString);

            switch (messageType) {
                case SUBMIT: {
                    String name = request.getParameter("name");
                    int value = Integer.parseInt(request.getParameter("value"));

                    if (!varsMap.containsKey(name)) {
                        varsMap.put(name, value);
                    } else {
                        Integer oldValue = varsMap.get(name);
                        List<Integer> list = Arrays.asList(oldValue, value);
                        int newValue = new IntSum().reduce(list.iterator());
                        varsMap.put(name, newValue);
                    }
                }
                break;
                case LIST_VARS: {
                    OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
                    try {
                        for (String varName : varsMap.keySet()) {
                            writer.write(varName);
                        }
                    } finally {
                        writer.close();
                    }
                }
                break;
                case GET_VALUE: {
                    String name = request.getParameter("name");

                    OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
                    try {
                        if (!varsMap.containsKey(name)) {
                            writer.write(String.format("%s\n", "None"));
                        } else {
                            writer.write(String.format("%d\n", varsMap.get(name)));
                        }
                    } finally {
                        writer.close();
                    }
                }
                break;
            }

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        ((Request) request).setHandled(true);
    }
}
