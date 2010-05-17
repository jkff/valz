package org.valz.server;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.AggregateRegistryCreator;
import org.valz.util.aggregates.LongSum;
import org.valz.util.backends.FinalStoreBackend;
import org.valz.util.backends.NonBlockingWriteBackend;
import org.valz.util.datastores.DataStore;
import org.valz.util.datastores.H2DataStore;

public class ValzServer {
    private static final Logger log = Logger.getLogger(ValzServer.class);


    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");
        ServerConfig config = ServerConfig.read();

        Server server = startServer(getInternalServerConfig(config.dataStoreFile, config.port, config.delayForCaching,
                config.chunkSize));
        try {
            server.join();
        } catch (InterruptedException e) {
            log.error("Could not stop server", e);
        }
    }

    public static Server startServer(InternalConfig conf) throws Exception {
        Server server = new Server(conf.port);

        server.addHandler(new ValzHandler(conf.readChunkBackend, conf.writeBackend, conf.registry));

        try {
            server.start();
        } catch (Exception e) {
            log.error("Could not start server", e);
            throw e;
        }

        log.info("Started server at :" + conf.port);
        return server;
    }

    public static InternalConfig getInternalServerConfig(String dataStoreFile, int port, int delayForCaching,
                                                         int chunkSize) {
        AggregateRegistry registry = AggregateRegistryCreator.create();

        DataStore dataStore = new H2DataStore(dataStoreFile, registry);
        FinalStoreBackend finalStoreBackend = new FinalStoreBackend(dataStore, chunkSize);
        NonBlockingWriteBackend nonBlockingWriteBackend =
                new NonBlockingWriteBackend(finalStoreBackend, delayForCaching);

        return new InternalConfig(port, finalStoreBackend, nonBlockingWriteBackend, registry);
    }
    


    private ValzServer() {
    }
}
