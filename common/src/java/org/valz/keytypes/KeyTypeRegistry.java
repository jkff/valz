package org.valz.keytypes;

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

    public static KeyTypeRegistry create() {
        KeyTypeRegistry keyTypeRegistry = new KeyTypeRegistry();
        keyTypeRegistry.register(StringKey.NAME, new StringKey.ConfigFormatter());
        keyTypeRegistry.register(LongKey.NAME, new LongKey.ConfigFormatter());
        keyTypeRegistry.register(MultiKey.NAME, new MultiKey.ConfigFormatter(keyTypeRegistry));
        return keyTypeRegistry;
    }
}