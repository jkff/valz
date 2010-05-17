package org.valz.util.keytypes;

import org.valz.util.aggregates.AggregateConfigFormatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KeyTypeRegistry {
    private final Map<String, KeyTypeConfigFormatter<?>> name2keytype =
            new HashMap<String, KeyTypeConfigFormatter<?>>();

    public KeyTypeRegistry() {
    }

    public void register(String name, KeyTypeConfigFormatter<?> configFormatter) {
        if (name2keytype.containsKey(name)) {
            throw new IllegalArgumentException("KeyType with this name already registered.");
        }
        name2keytype.put(name, configFormatter);
    }

    public KeyTypeConfigFormatter get(String name) {
        return name2keytype.get(name);
    }

    public Collection<String> listNames() {
        return new ArrayList<String>(name2keytype.keySet());
    }
}