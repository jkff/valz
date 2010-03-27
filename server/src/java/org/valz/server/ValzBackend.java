package org.valz.server;

import org.apache.log4j.Logger;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;

import java.util.*;

public class ValzBackend {
    private static final Logger log = Logger.getLogger(ValzBackend.class);



    private final AggregateRegistry registry = new AggregateRegistry();
    private final Map<String, Object> name2val = new HashMap<String, Object>();
    private final Map<String, Aggregate<?>> name2aggregate = new HashMap<String, Aggregate<?>>();



    public ValzBackend() {
    }

    

    public synchronized void submit(String name, Aggregate aggregate, Object value) {
        if (!name2val.containsKey(name)) {
            name2val.put(name, value);
            name2aggregate.put(name, aggregate);
        } else {
            if (!aggregate.equals(name2aggregate.get(name))) {
                throw new IllegalArgumentException("Val with same name and different aggregate already exists.");
            }

            Object oldValue = name2val.get(name);
            List<Object> list = Arrays.asList(oldValue, value);
            Object newValue = aggregate.reduce(list.iterator());
            name2val.put(name, newValue);
        }
    }

    public synchronized Collection<String> listVars() {
        return new ArrayList<String>(name2val.keySet());
    }

    public synchronized Object getValue(String name) {
        return name2val.get(name);
    }

    public synchronized Aggregate getAggregate(String name) {
        return name2aggregate.get(name);
    }

    public synchronized void registerSupportedAggregate(Class<? extends Aggregate<?>> clazz) {
        registry.registerSupportedAggregate(clazz);
    }
}
