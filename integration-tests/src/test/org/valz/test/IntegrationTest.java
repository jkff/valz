package org.valz.test;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.valz.client.ClientConfig;
import org.valz.model.AggregateRegistry;
import org.valz.model.LongSum;
import org.valz.backends.ReadBackend;
import org.valz.backends.RemoteReadBackend;
import org.valz.backends.RoundRobinWriteBackend;
import org.valz.backends.WriteBackend;
import org.valz.client.Val;
import org.valz.client.Valz;
import org.valz.protocol.messages.BigMapChunkValue;
import org.valz.server.InternalConfig;
import org.valz.server.ServerUtils;
import org.valz.server.ValzServer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class IntegrationTest {
    private final AggregateRegistry aggregateRegistry = AggregateRegistry.create();

    private void removeFiles(String dbname) {
        new File(dbname + ".h2.db").delete();
        new File(dbname + ".lock.db").delete();
        new File(dbname + ".trace.db").delete();
    }

    @Test
    public void testOneClientOneServerOneVar() throws Exception {
        // todo: change port in all places to 9125
        int port = 8800;
        int delayForCaching = 100;
        int chunkSize = 100;

        InternalConfig config = ValzServer.makeInternalServerConfig(ServerUtils.getDbName(port), port);
        Server server = ValzServer.startServer(config);

        try {
            // init client
            Valz.init(Valz.makeDefaultWriteBackend(aggregateRegistry, new ClientConfig()));

            // init viewer
            ReadBackend readBackend =
                    new RemoteReadBackend(ServerUtils.portsToLocalAddresses(port), aggregateRegistry);

            // Produce a fresh name to avoid using values
            // from previous launches of the program.
            // It would be better to clear the data before test,
            // but this approach will suffice for now.
            String name = "counter" + Math.random();
            Val<Long> counter = Valz.register(name, new LongSum());


            // submit data 
            counter.submit(1L);
            counter.submit(2L);


            // delay for sending samples by daemon threads
            Thread.sleep(delayForCaching * 3);

            Assert.assertTrue(readBackend.listVals().contains(name));
            Assert.assertEquals(3L, readBackend.getValue(name).getValue());
        } finally {
            server.stop();
            server.join();

            removeFiles(ServerUtils.getDbName(port));
        }
    }

    @Test
    public void testOneClientManyServersOneVar() throws Exception {

        PropertyConfigurator.configure("log4j.properties");

        // init and start valz servers
        int[] ports = {8800, 8801};
        int delayForCaching = 100;
        int chunkSize = 100;

        List<InternalConfig> listConfigs = ServerUtils.getServerConfigs(chunkSize, delayForCaching, ports);
        List<Server> listServers = ServerUtils.startServers(listConfigs);

        try {
            // init client
            {
                List<WriteBackend> listWriteBackends = new ArrayList<WriteBackend>();
                for (InternalConfig config : listConfigs) {
                    listWriteBackends.add(config.writeBackend);
                }
                WriteBackend clientWriteBackend = new RoundRobinWriteBackend(listWriteBackends);
                Valz.init(clientWriteBackend);
            }

            // init viewer
            ReadBackend readBackend =
                    new RemoteReadBackend(
                            ServerUtils.portsToLocalAddresses(ports),
                            aggregateRegistry);

            // Produce a fresh name to avoid using values
            // from previous launches of the program.
            // It would be better to clear the data before test,
            // but this approach will suffice for now.
            String name = "counter" + (1 + Math.random());
            Val<Long> counter = Valz.register(name, new LongSum());

            // submit data
            final int SUBMITS_COUNT = 200;
            for (int i = 0; i < SUBMITS_COUNT; i++) {
                counter.submit(1L);
            }

            // delay for sending samples by daemon threads
            Thread.sleep(SUBMITS_COUNT * 10);

            // check values
            Assert.assertTrue(readBackend.listVals().contains(name));
            Assert.assertEquals((long)SUBMITS_COUNT, readBackend.getValue(name).getValue());

        } finally {
            // stop servers
            ServerUtils.stopServers(listServers);
            for (String item : ServerUtils.getMultipleDbNames(ports)) {
                removeFiles(item);
            }            
        }
    }

    @Test
    public void testOneClientOneServerOneBigMap() throws Exception {
        int port = 8800;
        int delayForCaching = 100;
        int chunkSize = 100;

        InternalConfig config = ValzServer.makeInternalServerConfig(ServerUtils.getDbName(port), port);
        Server server = ValzServer.startServer(config);

        try {
            // init client
            Valz.init(Valz.makeDefaultWriteBackend(aggregateRegistry,
                    new ClientConfig()));

            // init viewer
            ReadBackend readBackend =
                    new RemoteReadBackend(
                            ServerUtils.portsToLocalAddresses(port),
                            aggregateRegistry);

            // Produce a fresh name to avoid using values
            // from previous launches of the program.
            // It would be better to clear the data before test,
            // but this approach will suffice for now.
            String name = ("MAP" + (1 + Math.random())).replace('.', '_');
            Val<Map<String, Long>> map = Valz.registerBigMap(name, new LongSum());

            // submit data
            map.submit(Collections.singletonMap("foo", 1L));
            //map.submit(Collections.singletonMap("foo", 2L));


            // delay for sending samples by daemon threads
            Thread.sleep(delayForCaching * 3);

            Assert.assertTrue(readBackend.listBigMaps().size() > 0);
            Assert.assertTrue(readBackend.listBigMaps().contains(name));
            Assert.assertEquals(Long.valueOf(1L),
                    readBackend.getBigMapIterator(name, null).next(1).getValue().get("foo"));
        } finally {
            server.stop();
            server.join();

            removeFiles(ServerUtils.getDbName(port));
        }
    }


    @Test
    public void testOneClientManyServersOneBigMap() throws Exception {

        PropertyConfigurator.configure("log4j.properties");

        // init and start valz servers
        int[] ports = {8950, 8951};
        int delayForCaching = 100;
        int chunkSize = 100;

        List<InternalConfig> listConfigs = ServerUtils.getServerConfigs(chunkSize, delayForCaching, ports);
        List<Server> listServers = ServerUtils.startServers(listConfigs);

        try {
            // init client
            {
                List<WriteBackend> listWriteBackends = new ArrayList<WriteBackend>();
                for (InternalConfig config : listConfigs) {
                    listWriteBackends.add(config.writeBackend);
                }
                WriteBackend clientWriteBackend = new RoundRobinWriteBackend(listWriteBackends);
                Valz.init(clientWriteBackend);
            }

            // init viewer
            ReadBackend readBackend =
                    new RemoteReadBackend(ServerUtils.portsToLocalAddresses(ports),
                            aggregateRegistry);

            // Produce a fresh name to avoid using values
            // from previous launches of the program.
            // It would be better to clear the data before test,
            // but this approach will suffice for now.
            String name = ("MAP" + (1 + Math.random())).replace('.', '_');
            Val<Map<String, Long>> map = Valz.registerBigMap(name, new LongSum());


            // submit data
            final int SUBMITS_COUNT = 2;
            for (int i = 0; i < SUBMITS_COUNT; i++) {
                map.submit(Collections.singletonMap("foo", 1L));
            }

            // delay for sending samples by daemon threads
            Thread.sleep(SUBMITS_COUNT * 50);

            // TODO: check listBigMaps
            System.out.println(readBackend.listBigMaps().size());
            System.out.println(readBackend.listBigMaps().toArray()[0]);

            // check values
            Assert.assertTrue(readBackend.listBigMaps().contains(name));

            BigMapChunkValue<Object> chunk = readBackend.getBigMapIterator(name, null).next(5);

            Assert.assertEquals(1, chunk.getValue().size());
            Assert.assertEquals(Long.valueOf(SUBMITS_COUNT), chunk.getValue().values().iterator().next());

        } finally {
            // stop servers
            ServerUtils.stopServers(listServers);
            for (String item : ServerUtils.getMultipleDbNames(ports)) {
                removeFiles(item);
            }
        }
    }
}
