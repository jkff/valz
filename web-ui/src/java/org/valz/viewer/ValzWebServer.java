package org.valz.viewer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.LongSum;
import org.valz.util.backends.ReadBackend;
import org.valz.util.backends.RemoteReadBackend;

import java.util.Arrays;

public class ValzWebServer {
    private static final Logger log = Logger.getLogger(ValzWebServer.class);

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");

        int port = 8081;

        Server server = new Server(port);


        AggregateRegistry registry = new AggregateRegistry();
        registry.register(LongSum.NAME, new LongSum.ConfigParser());

        // For now I'm too lazy to launch 2 distinct servers. Let's test the thing on 2 identical ones.
        ReadBackend readBackend =
                new RemoteReadBackend(Arrays.asList("http://localhost:8080", "http://localhost:8080"), registry);

        ValzWebHandler handler = new ValzWebHandler(readBackend);
        server.addHandler(handler);

        try {
            server.start();
        } catch (Exception e) {
            log.error("Could not start server", e);
            throw e;
        }

        log.info("Started server at :" + port);

        try {
            server.join();
        } catch (InterruptedException e) {
            log.error("Could not stop server", e);
        }
    }
}
