package org.valz.examples.hitcount;

import org.valz.util.protocol.WriteConfiguration;
import org.valz.client.Val;
import org.valz.client.Valz;
import org.valz.util.aggregates.LongSum;

public class Main {
    public static void main(String[] args) throws Exception {
        WriteConfiguration conf = new WriteConfiguration();
        conf.setServerURL("http://localhost:8080");
        
        Valz.init(conf);

        Val<Long> hitCount = Valz.register("org.valz.examples.hitcount.hitCount", new LongSum());

        for(int i = 0; i < 1; ++i) {
            hitCount.submit(1L);
            Thread.sleep(50);
        }
    }
}
