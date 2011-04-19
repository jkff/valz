package org.valz.server;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.model.AggregateRegistry;
import org.valz.backends.DatastoreBackend;
import org.valz.backends.NonBlockingWriteBackend;
import org.valz.datastores.DataStore;
import org.valz.datastores.h2.H2DataStore;

public class ValzServer {
    private static final Logger log = Logger.getLogger(ValzServer.class);


    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");
        ServerConfig config = ServerConfig.read();

        Server server = startServer(config);
        try {
            server.join();
        } catch (InterruptedException e) {
            log.error("Could not stop server", e);
        }
    }

    public static Server startServer(ServerConfig config) throws Exception {
        Server server = new Server(config.port);

        server.addHandler(makeValzHandler(config));

        try {
            server.start();
        } catch (Exception e) {
            log.error("Could not start server", e);
            throw e;
        }

        log.info("Started server at :" + config.port);
        return server;
    }

    public static ValzHandler makeValzHandler(ServerConfig config) {
        AggregateRegistry aggregateRegistry = AggregateRegistry.create();

        DataStore dataStore = new H2DataStore(config.dataStoreFile, aggregateRegistry);
        DatastoreBackend datastoreBackend = new DatastoreBackend(dataStore);
        NonBlockingWriteBackend nonBlockingWriteBackend = new NonBlockingWriteBackend(datastoreBackend);

        return new ValzHandler(datastoreBackend, nonBlockingWriteBackend, aggregateRegistry);
    }

    private ValzServer() {
    }
}
