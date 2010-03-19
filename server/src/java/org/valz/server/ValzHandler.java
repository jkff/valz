package org.valz.server;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class ValzHandler extends AbstractHandler {
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        response.getWriter().println("server: Hello world");
        Map map = request.getParameterMap();

        
        response.getWriter().println(String.format("server: map size = %d", map.size()));
        response.getWriter().println("server: URI = " + request.getRequestURI());
        response.getWriter().println("server: name = " + request.getParameter("name"));
        response.getWriter().println("server: value = " + request.getParameter("value"));

        ((Request)request).setHandled(true);
    }
}
