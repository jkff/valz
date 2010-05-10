package org.valz.server;

import nl.chess.it.util.config.ConfigValidationResult;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.LongSum;
import org.valz.util.backends.FinalStoreBackend;
import org.valz.util.backends.NonBlockingWriteBackend;
import org.valz.util.datastores.DataStore;
import org.valz.util.datastores.H2DataStore;
import org.valz.util.io.IOUtils;

import java.io.*;
import java.util.Iterator;
import java.util.Properties;

public class ValzServer {
    private static final Logger log = Logger.getLogger(ValzServer.class);


    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");
        ServerConfig config = readServerConfig();

        Server server = startServer(getServerConfig(config.getDataStoreFile(), config.getPort(), config.getDelayForCaching()));
        try {
            server.join();
        } catch (InterruptedException e) {
            log.error("Could not stop server", e);
        }
    }

    public static Server startServer(InternalConfig conf) throws Exception {
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

    public static InternalConfig getServerConfig(String dataStoreFile, int port, int delayForCaching) {
        AggregateRegistry registry = new AggregateRegistry();
        registry.register(LongSum.NAME, new LongSum.ConfigFormatter());

        DataStore dataStore = new H2DataStore(dataStoreFile, registry);
        FinalStoreBackend finalStoreBackend = new FinalStoreBackend(dataStore);
        NonBlockingWriteBackend nonBlockingWriteBackend =
                new NonBlockingWriteBackend(finalStoreBackend, delayForCaching);

        return new InternalConfig(port, finalStoreBackend, nonBlockingWriteBackend, registry);
    }

    private static ServerConfig readServerConfig() {

        Properties properties = new Properties();

        InputStream in = null;
        try {
            in = new FileInputStream(ServerConfig.FILE_NAME);
            properties.load(in);
        } catch (FileNotFoundException e) {
            System.out.println(String.format("File %s not found.", ServerConfig.FILE_NAME));
            System.exit(1);
        } catch (IOException e) {
            System.out.println(String.format("Cannot read file %s.", ServerConfig.FILE_NAME));
            System.exit(1);
        } finally {
            IOUtils.closeSilently(in);
        }

        ServerConfig config = new ServerConfig(properties);

        ConfigValidationResult configResult = config.validateConfiguration();
        if (configResult.thereAreErrors()) {
            System.out.println("Errors in configuration");
            for (Iterator iter = configResult.getErrors().iterator(); iter.hasNext();) {
                System.out.println(" > " + iter.next());
            }
            System.exit(1);
        }
        if (configResult.thereAreUnusedProperties()) {
            System.out.println("Unused properties");
            for (Iterator iter = configResult.getUnusedProperties().iterator(); iter.hasNext();) {
                System.out.println(" > " + iter.next());
            }
        }

        return config;
    }


    private ValzServer() {
    }
}
