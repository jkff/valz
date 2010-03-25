package org.valz.examples.hitcount;

import org.valz.api.Configuration;
import org.valz.api.IntegerParser;
import org.valz.api.Val;
import org.valz.api.Valz;
import org.valz.util.MessageType;
import org.valz.util.aggregates.IntSum;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        conf.setServers(Arrays.asList("localhost:8080"));
        
        Valz.init(conf);

        Val<Integer> hitCount = Valz.register("org.valz.examples.hitcount.hitCount", new IntSum());

        for(int i = 0; i < 1; ++i) {
            hitCount.submit(1);
            Thread.sleep(50);
        }

        for (String name : Valz.listVars()) {
            int value = Valz.getValue(name, new IntegerParser());
            System.out.println(String.format("%s = %d", name, value));
        }
    }
}
