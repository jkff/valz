package org.valz.server;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;

public class ValzServer {
    private static final Logger log = Logger.getLogger(ValzServer.class);



    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");

        int port = 8080;

        Server server = new Server(port);

        ValzBackend backend = new ValzBackend();

        server.addHandler(new ValzHandler(backend, backend));

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


    
    private ValzServer() {
    }
}
