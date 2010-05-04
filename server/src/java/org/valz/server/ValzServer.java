package org.valz.server;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.LongSum;
import org.valz.util.backends.FinalStoreBackend;
import org.valz.util.backends.NonBlockingWriteBackend;
import org.valz.util.datastores.DataStore;
import org.valz.util.datastores.H2DataStore;

public class ValzServer {
    private static final Logger log = Logger.getLogger(ValzServer.class);

    
    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");

        Server server = startServer(getServerConfiguration("h2store", 8080));
        try {
            server.join();
        } catch (InterruptedException e) {
            log.error("Could not stop server", e);
        }
    }

    public static Server startServer(ValzServerConfiguration conf) throws Exception {
        Server server = new Server(conf.port);

        server.addHandler(new ValzHandler(conf.readBackend, conf.writeBackend, conf.registry));

        try {
            server.start();
        } catch (Exception e) {
            log.error("Could not start server", e);
            throw e;
        }

        log.info("Started server at :" + conf.port);
        return server;
    }

    public static ValzServerConfiguration getServerConfiguration(String storefile, int port) {
        AggregateRegistry registry = new AggregateRegistry();
        registry.register(LongSum.NAME, new LongSum.ConfigFormatter());

        DataStore dataStore = new H2DataStore(storefile, registry);
        FinalStoreBackend finalStoreBackend = new FinalStoreBackend(dataStore);
        NonBlockingWriteBackend nonBlockingWriteBackend = new NonBlockingWriteBackend(finalStoreBackend, 100);

        return new ValzServerConfiguration(port, finalStoreBackend, nonBlockingWriteBackend, registry);
    }


    private ValzServer() { }
}
