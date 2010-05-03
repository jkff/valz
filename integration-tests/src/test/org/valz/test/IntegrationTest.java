package org.valz.test;

import org.junit.Assert;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.valz.client.Val;
import org.valz.client.Valz;
import org.valz.server.ValzServer;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.LongSum;
import org.valz.util.backends.ReadBackend;
import org.valz.util.backends.RemoteReadBackend;
import java.util.Arrays;


public class IntegrationTest {
    @Test
    public void testOneClientOneServerOneVar() throws Exception {
        AggregateRegistry registry = new AggregateRegistry();
        registry.register(LongSum.NAME, new LongSum.ConfigParser());

        Server server = ValzServer.startServer(8080);

        try {
            Valz.init("http://localhost:8080/", registry);

            // Produce a fresh name to avoid using values
            // from previous launches of the program.
            // It would be better to clear the data before test,
            // but this approach will suffice for now. 
            String name = "counter" + Math.random();

            Val<Long> counter = Valz.register(name, new LongSum());

            counter.submit(1L);
            counter.submit(2L);

            ReadBackend readBackend = new RemoteReadBackend(Arrays.asList("http://localhost:8080/"), registry);

            Assert.assertTrue(readBackend.listVars().contains(name));
            Assert.assertEquals(3L, readBackend.getValue(name).getValue());
        } finally {
            server.stop();
            server.join();
        }
    }
}
