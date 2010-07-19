package org.valz.keytypes;

import java.util.HashMap;
import java.util.Map;

public class KeyTypeRegistry {
    private final Map<String, KeyTypeFormat<?>> name2keytype =
            new HashMap<String, KeyTypeFormat<?>>();

    public KeyTypeRegistry() {
    }

    public void register(String name, KeyTypeFormat<?> format) {
        if (name2keytype.containsKey(name)) {
            throw new IllegalArgumentException("KeyType with this name already registered.");
        }
        name2keytype.put(name, format);
    }

    public KeyTypeFormat get(String name) {
        return name2keytype.get(name);
    }

    public static KeyTypeRegistry create() {
        KeyTypeRegistry keyTypeRegistry = new KeyTypeRegistry();
        keyTypeRegistry.register(StringKey.NAME, new StringKey.Format());
        keyTypeRegistry.register(LongKey.NAME, new LongKey.Format());
        keyTypeRegistry.register(MultiKey.NAME, new MultiKey.Format(keyTypeRegistry));
        return keyTypeRegistry;
    }
}