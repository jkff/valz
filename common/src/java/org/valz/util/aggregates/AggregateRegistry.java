package org.valz.util.aggregates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AggregateRegistry {
    private final Map<String, AggregateConfigFormatter<?>> name2agg =
            new HashMap<String, AggregateConfigFormatter<?>>();

    public AggregateRegistry() {
    }

    public void register(String name, AggregateConfigFormatter<?> configFormatter) {
        if (name2agg.containsKey(name)) {
            throw new IllegalArgumentException("Aggregate with this name already registered.");
        }
        name2agg.put(name, configFormatter);
    }

    public AggregateConfigFormatter<?> get(String name) {
        return name2agg.get(name);
    }

    public Collection<String> listNames() {
        return new ArrayList<String>(name2agg.keySet());
    }
}
