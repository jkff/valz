package org.valz.server;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.valz.util.aggregates.Aggregate;

import java.util.*;

public class ValzBackend {
    private static final Logger log = Logger.getLogger(ValzBackend.class);

    private AggregateParser parser = new AggregateParser();
    private Map<String, Object> name2val = new HashMap<String, Object>();

    void submit(String name, JSONObject aggregateSpec, Object value) {
        Aggregate aggregate;
        try {
            aggregate = parser.parse(aggregateSpec);
        } catch (Exception e) {
            log.error("Malformed aggregate spec: " + aggregateSpec.toJSONString(), e);
            return;
        }
        if (!name2val.containsKey(name)) {
            name2val.put(name, value);
        } else {
            Object oldValue = name2val.get(name);
            List<Object> list = Arrays.asList(oldValue, value);
            Object newValue = aggregate.reduce(list.iterator());
            name2val.put(name, newValue);
        }
    }

    Collection<String> listVars() {
        return name2val.keySet();
    }

    Object getValue(String name) {
        return name2val.get(name);
    }

    public void registerSupportedAggregate(Class<? extends Aggregate<?>> clazz) {
        parser.registerSupportedAggregate(clazz);
    }
}
