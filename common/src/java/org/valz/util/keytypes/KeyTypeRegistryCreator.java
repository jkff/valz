package org.valz.util.keytypes;

import org.valz.util.aggregates.*;

public class KeyTypeRegistryCreator {

    public static KeyTypeRegistry create() {
        KeyTypeRegistry registry = new KeyTypeRegistry();
        registry.register(KeyLong.NAME, new KeyLong.ConfigFormatter());
        return registry;
    }
}