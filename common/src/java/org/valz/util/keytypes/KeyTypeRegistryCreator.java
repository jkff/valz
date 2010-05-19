package org.valz.util.keytypes;

public class KeyTypeRegistryCreator {

    public static KeyTypeRegistry create() {
        KeyTypeRegistry keyTypeRegistry = new KeyTypeRegistry();
        keyTypeRegistry.register(KeyString.NAME, new KeyString.ConfigFormatter());
        keyTypeRegistry.register(KeyLong.NAME, new KeyLong.ConfigFormatter());
        keyTypeRegistry.register(MultiKey.NAME, new MultiKey.ConfigFormatter(keyTypeRegistry));
        return keyTypeRegistry;
    }
}