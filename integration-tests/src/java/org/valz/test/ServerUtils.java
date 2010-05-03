package org.valz.test;

import org.mortbay.jetty.Server;
import org.valz.server.ValzServer;
import org.valz.server.ValzServerConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerUtils {

    public static List<String> portsToLocalAddresses(int... ports) {
        List<String> list = new ArrayList<String>();
        for (int item : ports) {
            list.add(String.format("http://localhost:%d", item));
        }
        return list;
    }

    public static List<ValzServerConfiguration> getServerConfigs(int... ports) {
        List<ValzServerConfiguration> listConfigs = new ArrayList<ValzServerConfiguration>();
        for (int port : ports) {
            listConfigs.add(ValzServer.getServerConfiguration("h2store" + port, port));
        }
        return listConfigs;
    }

    public static List<Server> startServers(List<ValzServerConfiguration> listConfigs) throws Exception {
        List<Server> listServers = new ArrayList<Server>();
        for (ValzServerConfiguration config : listConfigs) {
            listServers.add(ValzServer.startServer(config));
        }
        return listServers;
    }

    public static void stopServers(List<Server> collection) {
        for (Server server : collection) {
            try {
                server.stop();
                server.join();
            } catch (Exception e) {
                // Ignore
            }
        }
    }


    private ServerUtils() {
    }
}
