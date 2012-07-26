package org.valz.examples;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.backends.*;
import org.valz.client.Val;
import org.valz.client.Valz;
import org.valz.datastores.h2.H2DataStore;
import org.valz.model.AggregateRegistry;
import org.valz.model.LongSum;
import org.valz.server.ServerConfig;
import org.valz.server.ServerUtils;
import org.valz.viewer.ValzWebServer;
import org.valz.viewer.ViewerConfig;
import org.valz.viewer.ViewerInternalConfig;

import java.util.ArrayList;
import java.util.List;

public class AllServersStarter {
    private static final Logger log = Logger.getLogger(AllServersStarter.class);


    public static void main(String[] args) throws Exception {

        PropertyConfigurator.configure("log4j.properties");

        int[] ports = {8800, 8801};
        int delayForCaching = 100;
        List<ServerConfig> configs = ServerUtils.getServerConfigs(delayForCaching, ports);
        List<Server> servers = ServerUtils.startServers(configs);

        Server valzWebServer = ValzWebServer
                .startServer(8900, ViewerInternalConfig.getConfig(ViewerConfig.read().urls, 0));

        AggregateRegistry registry = AggregateRegistry.create();

        // init client
        {
            List<WriteBackend> writeBackends = new ArrayList<WriteBackend>();
            for (ServerConfig config : configs) {
                //Roundrobin between remotes; put nb + transitional on top.
                final H2DataStore h2DataStore = new H2DataStore(config.dataStoreFile, registry);

                final NonBlockingWriteBackend backend = new NonBlockingWriteBackend(new TransitionalWriteBackend(
                        new DatastoreBackend(h2DataStore), h2DataStore, 100L, 1024));

                writeBackends.add(backend);
            }

            WriteBackend clientWriteBackend = new RoundRobinWriteBackend(writeBackends);
            Valz.init(clientWriteBackend);
        }

        // send data
        {
            String name = "counter" + Math.random();
            Val<Long> counter = Valz.register(name, new LongSum());

            final int SUBMITS_COUNT = 100;
            for (int i = 0; i < SUBMITS_COUNT; i++) {
                counter.submit(1L);
            }
        }

        try {
            valzWebServer.join();
        } catch (InterruptedException e) {
            log.error("Could not stop valz web server", e);
        }
        ServerUtils.stopServers(servers);
    }

}
