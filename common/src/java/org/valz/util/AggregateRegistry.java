package org.valz.util;

import org.valz.util.aggregates.AggregateConfigParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AggregateRegistry {
    private final Map<String, AggregateConfigParser<?>> name2agg = new HashMap<String, AggregateConfigParser<?>>();

    public AggregateRegistry() {
    }

    public void register(String name, AggregateConfigParser<?> configParser) {
        if (name2agg.containsKey(name)) {
            throw new IllegalArgumentException("Aggregate with this name already registered.");
        }
        name2agg.put(name, configParser);
    }

    public AggregateConfigParser<?> get(String name) {
        return name2agg.get(name);
    }

    public Collection<String> listNames() {
        return new ArrayList<String>(name2agg.keySet());
    }
}
