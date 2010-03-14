package org.valz.server;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ValzServer {
    private static final Logger log = Logger.getLogger(ValzServer.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");

        int port = 8080;
        
        Server server = new Server(port);

        server.addHandler(new ValzHandler());

        try {
            server.start();
        } catch (Exception e) {
            log.error("Could not start server", e);
        }

        log.info("Started server at :" + port);

        try {
            server.join();
        } catch (InterruptedException e) {
            log.error("Could not stop server", e);
        }
    }
}
