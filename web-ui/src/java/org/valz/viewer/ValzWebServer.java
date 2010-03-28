package org.valz.viewer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.LongSum;
import org.valz.util.protocol.ReadBackend;
import org.valz.util.protocol.ReadConfiguration;
import org.valz.util.protocol.RemoteReadBackend;

import java.util.Arrays;

/**
 * Created on: 27.03.2010 23:27:32
 */
public class ValzWebServer {
    private static final Logger log = Logger.getLogger(ValzWebServer.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");

        int port = 8081;

        Server server = new Server(port);

        ReadConfiguration conf = new ReadConfiguration();
        // For now I'm too lazy to launch 2 distinct servers. Let's test the thing on 2 identical ones.
        conf.setServerUrls(Arrays.asList("http://localhost:8080", "http://localhost:8080"));

        AggregateRegistry.INSTANCE.registerSupportedAggregate(LongSum.class);

        ReadBackend backend = new RemoteReadBackend(conf, AggregateRegistry.INSTANCE);

        ValzWebHandler handler = new ValzWebHandler(backend);
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
