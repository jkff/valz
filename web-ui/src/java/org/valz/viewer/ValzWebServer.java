package org.valz.viewer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.LongSum;
import org.valz.util.backends.ReadBackend;
import org.valz.util.backends.RemoteReadBackend;

import java.util.Arrays;
import java.util.List;

public class ValzWebServer {
    private static final Logger log = Logger.getLogger(ValzWebServer.class);

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");

        Server server = startServer(
                getWebServerConfiguration(8900, Arrays.asList("http://localhost:8080", "http://localhost:8080")));

        try {
            server.join();
        } catch (InterruptedException e) {
            log.error("Could not stop server", e);
        }
    }

    public static Server startServer(ValzWebServerConfiguration conf) throws Exception {

        Server server = new Server(conf.port);

        ValzWebHandler handler = new ValzWebHandler(conf.readBackend, conf.registry);
        server.addHandler(handler);

        try {
            server.start();
        } catch (Exception e) {
            log.error("Could not start server", e);
            throw e;
        }

        log.info("Started server at :" + conf.port);
        return server;
    }

    public static ValzWebServerConfiguration getWebServerConfiguration(int port, List<String> readServerUrls) {
        AggregateRegistry registry = new AggregateRegistry();
        registry.register(LongSum.NAME, new LongSum.ConfigFormatter());
        ReadBackend readBackend = new RemoteReadBackend(readServerUrls, registry);

        return new ValzWebServerConfiguration(port, readBackend, registry);
    }
}
