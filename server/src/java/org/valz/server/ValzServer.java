package org.valz.server;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.LongSum;
import org.valz.util.datastores.H2DataStore;
import org.valz.util.datastores.DataStore;
import org.valz.util.protocol.backends.FinalStoreBackend;

public class ValzServer {
    private static final Logger log = Logger.getLogger(ValzServer.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");

        int port = 8080;

        Server server = startServer(port);

        try {
            server.join();
        } catch (InterruptedException e) {
            log.error("Could not stop server", e);
        }
    }

    public static Server startServer(int port) {
        Server server = new Server(port);

        AggregateRegistry registry = new AggregateRegistry();
        registry.register(LongSum.NAME, new LongSum.ConfigParser());

        DataStore dataStore = new H2DataStore("h2store", registry);
        FinalStoreBackend backend = new FinalStoreBackend(dataStore);

        server.addHandler(new ValzHandler(backend, backend, registry));

        try {
            server.start();
        } catch (Exception e) {
            log.error("Could not start server", e);
        }

        log.info("Started server at :" + port);
        return server;
    }


    private ValzServer() {
    }
}
