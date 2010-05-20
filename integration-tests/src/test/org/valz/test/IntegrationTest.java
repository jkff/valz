package org.valz.test;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.valz.client.Val;
import org.valz.client.Valz;
import org.valz.server.InternalConfig;
import org.valz.server.ServerUtils;
import org.valz.server.ValzServer;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.AggregateRegistryCreator;
import org.valz.util.aggregates.BigMapIterator;
import org.valz.util.aggregates.LongSum;
import org.valz.util.backends.ReadBackend;
import org.valz.util.backends.RemoteReadBackend;
import org.valz.util.backends.RoundRobinWriteBackend;
import org.valz.util.backends.WriteBackend;
import org.valz.util.keytypes.KeyString;
import org.valz.util.keytypes.KeyTypeRegistry;
import org.valz.util.keytypes.KeyTypeRegistryCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class IntegrationTest {

    private final String dbname = "h2test";
    private KeyTypeRegistry keyTypeRegistry;
    private AggregateRegistry aggregateRegistry;

    private void removeFiles(String dbname) {
        new File(dbname + ".h2.db").delete();
        new File(dbname + ".lock.db").delete();
        new File(dbname + ".trace.db").delete();
    }

    @Before
    public void setUp() {
        removeFiles(dbname);
        aggregateRegistry = AggregateRegistryCreator.create();
        keyTypeRegistry = KeyTypeRegistryCreator.create();
    }

    @After
    public void tearDown() {
        removeFiles(dbname);
    }

    @Test
    public void testOneClientOneServerOneVar() throws Exception {
        int port = 8800;
        int delayForCaching = 100;
        int chunkSize = 100;

        InternalConfig config = ValzServer.getInternalServerConfig(dbname, port, delayForCaching, chunkSize);
        Server server = ValzServer.startServer(config);

        try {
            // init client
            Valz.init(Valz.getWriteBackend(keyTypeRegistry, aggregateRegistry,
                    String.format("http://localhost:%d", port)));

            // init viewer
            ReadBackend readBackend =
                    new RemoteReadBackend(ServerUtils.portsToLocalAddresses(port), keyTypeRegistry,
                            aggregateRegistry, chunkSize);

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

            Assert.assertTrue(readBackend.listVars().contains(name));
            Assert.assertEquals(3L, readBackend.getValue(name).getValue());
        } finally {
            server.stop();
            server.join();
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
                    new RemoteReadBackend(ServerUtils.portsToLocalAddresses(ports), keyTypeRegistry,
                            aggregateRegistry, chunkSize);

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
            Assert.assertTrue(readBackend.listVars().contains(name));
            Assert.assertEquals((long)SUBMITS_COUNT, readBackend.getValue(name).getValue());

        } finally {
            // stop servers
            ServerUtils.stopServers(listServers);
        }
    }

    @Test
    public void testOneClientOneServerOneBigMap() throws Exception {
        int port = 8800;
        int delayForCaching = 100;
        int chunkSize = 100;

        InternalConfig config = ValzServer.getInternalServerConfig(dbname, port, delayForCaching, chunkSize);
        Server server = ValzServer.startServer(config);

        try {
            // init client
            Valz.init(Valz.getWriteBackend(keyTypeRegistry, aggregateRegistry,
                    String.format("http://localhost:%d", port)));

            // init viewer
            ReadBackend readBackend =
                    new RemoteReadBackend(ServerUtils.portsToLocalAddresses(port), keyTypeRegistry,
                            aggregateRegistry, chunkSize);

            // Produce a fresh name to avoid using values
            // from previous launches of the program.
            // It would be better to clear the data before test,
            // but this approach will suffice for now.
            String name = ("MAP" + (1 + Math.random())).replace('.', '_');
            Val<Map<String, Long>> map = Valz.registerBigMap(name, new KeyString(), new LongSum());


            // submit data
            map.submit(Collections.singletonMap("foo", 1L));
            //map.submit(Collections.singletonMap("foo", 2L));


            // delay for sending samples by daemon threads
            Thread.sleep(delayForCaching * 3);

            Assert.assertTrue(readBackend.listBigMaps().size() > 0);
            Assert.assertTrue(readBackend.listBigMaps().contains(name));
            Assert.assertEquals(1L, readBackend.getBigMapIterator(name).next().getValue());
        } finally {
            server.stop();
            server.join();
        }
    }


    @Test
    public void testOneClientManyServersOneBigMap() throws Exception {

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
                    new RemoteReadBackend(ServerUtils.portsToLocalAddresses(ports), keyTypeRegistry,
                            aggregateRegistry, chunkSize);

            // Produce a fresh name to avoid using values
            // from previous launches of the program.
            // It would be better to clear the data before test,
            // but this approach will suffice for now.
            String name = ("MAP" + (1 + Math.random())).replace('.', '_');
            Val<Map<String, Long>> map = Valz.registerBigMap(name, new KeyString(), new LongSum());


            // submit data
            final int SUBMITS_COUNT = 2;
            for (int i = 0; i < SUBMITS_COUNT; i++) {
                map.submit(Collections.singletonMap("foo", 1L));
            }

            // delay for sending samples by daemon threads
            Thread.sleep(SUBMITS_COUNT * 10);

            // TODO: check listBigMaps
            // TODO: remove h2store after tests

            // check values
            Assert.assertTrue(readBackend.listBigMaps().contains(name));
            Assert.assertNotNull(readBackend.getBigMapIterator(name).next());

            BigMapIterator<String, Long> iter = readBackend.getBigMapIterator(name);

            Assert.assertEquals((long)SUBMITS_COUNT, (long)iter.next().getValue());
            Assert.assertEquals(false, iter.hasNext());

        } finally {
            // stop servers
            ServerUtils.stopServers(listServers);
        }
    }
}
