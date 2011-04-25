package org.valz.viewer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;

public class ValzWebServer {
    private static final Logger log = Logger.getLogger(ValzWebServer.class);

    private ValzWebServer() {
    }

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");

        ViewerConfig config = ViewerConfig.read();

        Server server = startServer(8900,
                ViewerInternalConfig.getConfig(config.urls, 0));

        try {
            server.join();
        } catch (InterruptedException e) {
            log.error("Could not stop server", e);
        }
    }

    public static Server startServer(int port, ViewerInternalConfig conf) throws Exception {

        Server server = new Server(port);

        ValzWebHandler handler = new ValzWebHandler(conf.readBackend, conf.aggregateRegistry);
        server.addHandler(handler);

        try {
            server.start();
        } catch (Exception e) {
            log.error("Could not start server", e);
            throw e;
        }

        log.info("Started server at :" + port);
        return server;
    }

    
}
