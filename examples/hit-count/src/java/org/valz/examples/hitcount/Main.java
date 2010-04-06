package org.valz.examples.hitcount;

import org.valz.client.Val;
import org.valz.client.Valz;
import org.valz.util.aggregates.LongSum;
import org.valz.util.protocol.Backend;
import org.valz.util.protocol.RemoteBackend;
import org.valz.util.protocol.WriteConfiguration;

public class Main {
    public static void main(String[] args) throws Exception {
        WriteConfiguration conf = new WriteConfiguration();
        conf.setServerURL("http://localhost:8080");

        Valz.init(conf);

        Val<Long> hitCount = Valz.register("org.valz.examples.hitcount.hitCount", new LongSum());

        for(int i = 0; i < 10; ++i) {
            hitCount.submit(1L);
            Thread.sleep(50);
        }


        Backend backend = new RemoteBackend("http://localhost:8080");

        System.out.println("Count of vars: " + backend.listVars().size());
    }
}
