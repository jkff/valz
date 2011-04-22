package org.valz.examples;

import org.apache.log4j.Logger;
import org.valz.client.ClientConfig;
import org.valz.client.Val;
import org.valz.client.Valz;
import org.valz.model.LongSum;

/**
 * Static class for declaring and creating aggregates
 */
class ValzAggregates {

    public static Val<Long> longCounter = Valz.register("longCounter", new LongSum());
}

/**
 * 1. Start server.
 * 2. Start this client.
 * 3. Open page 127.0.0.1:9125 in your browser and watch submitted results.
 * 4. For on-line watching submitted results reload page in your browser.
 *
 * Of course, you can restart client without server.
 */
public class HelloWorldTutorial {
    private static final Logger LOG = Logger.getLogger(HelloWorldTutorial.class);

    public static void main(String[] args) throws Exception {
        // init client
        ClientConfig clientConfig = new ClientConfig();
        Valz.init(clientConfig);

        for (int i = 0; i < 100; i++) {
            LOG.info("Submit new aggregate " + i);
            // use aggregate
            ValzAggregates.longCounter.submit(1L);
            Thread.sleep(300);
        }
    }
}
