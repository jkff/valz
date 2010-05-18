package org.valz.util.keytypes;

public class KeyTypeRegistryCreator {

    public static KeyTypeRegistry create() {
        KeyTypeRegistry aggregateRegistry = new KeyTypeRegistry();
        aggregateRegistry.register(KeyString.NAME, new KeyString.ConfigFormatter());
        return aggregateRegistry;
    }
}