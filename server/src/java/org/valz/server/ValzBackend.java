package org.valz.server;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;

import java.util.*;

public class ValzBackend {
    private static final Logger log = Logger.getLogger(ValzBackend.class);

    private AggregateRegistry _registry = new AggregateRegistry();
    private Map<String, Object> name2val = new HashMap<String, Object>();

    synchronized void submit(String name, JSONObject aggregateSpec, Object value) {
        Aggregate aggregate;
        try {
            aggregate = _registry.parseAggregateString(aggregateSpec);
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

    synchronized Collection<String> listVars() {
        return new ArrayList<String>(name2val.keySet());
    }

    synchronized Object getValue(String name) {
        return name2val.get(name);
    }

    public synchronized void registerSupportedAggregate(Class<? extends Aggregate<?>> clazz) {
        _registry.registerSupportedAggregate(clazz);
    }
}
