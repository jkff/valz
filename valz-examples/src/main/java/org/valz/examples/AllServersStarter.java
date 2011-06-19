package org.valz.examples;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.backends.RoundRobinWriteBackend;
import org.valz.backends.WriteBackend;
import org.valz.client.Val;
import org.valz.client.Valz;
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


        // init client
        {
            List<WriteBackend> listWriteBackends = new ArrayList<WriteBackend>();
            for (ServerConfig config : configs) {

            }
            WriteBackend clientWriteBackend = new RoundRobinWriteBackend(listWriteBackends);
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
