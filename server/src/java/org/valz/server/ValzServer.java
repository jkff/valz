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

        Server server = startServer(
                makeInternalServerConfig(config.dataStoreFile, config.port));
        try {
            server.join();
        } catch (InterruptedException e) {
            log.error("Could not stop server", e);
        }
    }

    public static Server startServer(InternalConfig conf) throws Exception {
        Server server = new Server(conf.port);

        server.addHandler(new ValzHandler(conf.readBackend, conf.writeBackend, conf.aggregateRegistry));

        try {
            server.start();
        } catch (Exception e) {
            log.error("Could not start server", e);
            throw e;
        }

        log.info("Started server at :" + conf.port);
        return server;
    }

    public static InternalConfig makeInternalServerConfig(String dataStoreFile, int port) {
        AggregateRegistry aggregateRegistry = AggregateRegistry.create();

        DataStore dataStore = new H2DataStore(dataStoreFile, aggregateRegistry);
        DatastoreBackend datastoreBackend = new DatastoreBackend(dataStore);
        NonBlockingWriteBackend nonBlockingWriteBackend = new NonBlockingWriteBackend(datastoreBackend);

        return new InternalConfig(port, datastoreBackend, nonBlockingWriteBackend, aggregateRegistry);
    }

    private ValzServer() {
    }
}
