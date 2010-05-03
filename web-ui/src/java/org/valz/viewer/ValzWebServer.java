package org.valz.viewer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.util.backends.ReadBackend;

public class ValzWebServer {
    private static final Logger log = Logger.getLogger(ValzWebServer.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");

        int port = 8081;

        Server server = new Server(port);

        // For now I'm too lazy to launch 2 distinct servers. Let's test the thing on 2 identical ones.
        //Arrays.asList("http://localhost:8080", "http://localhost:8080")

        ReadBackend readBackend = null; //new RemoteReadBackend(conf);

        ValzWebHandler handler = new ValzWebHandler(readBackend);
        server.addHandler(handler);

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
