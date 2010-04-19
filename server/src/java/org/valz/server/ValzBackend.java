package org.valz.server;

import org.apache.log4j.Logger;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.ReadBackend;
import org.valz.util.protocol.WriteBackend;

import java.util.*;

public class ValzBackend implements ReadBackend, WriteBackend {
    private static final Logger log = Logger.getLogger(ValzBackend.class);

    private final Map<String, Object> name2val = new HashMap<String, Object>();
    private final Map<String, Aggregate<?>> name2aggregate = new HashMap<String, Aggregate<?>>();

    public ValzBackend() {
    }

    public synchronized <T> void submit(String name, Aggregate<T> aggregate, T value) {
        if (!name2val.containsKey(name)) {
            name2val.put(name, value);
            name2aggregate.put(name, aggregate);
        } else {
            if (!aggregate.equals(name2aggregate.get(name))) {
                throw new IllegalArgumentException("Val with same name and different aggregate already exists.");
            }

            T oldValue = (T) name2val.get(name);
            List<T> list = Arrays.asList(oldValue, value);
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
}
