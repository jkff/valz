package org.valz.util.keytypes;

import com.sdicons.json.model.JSONValue;

public class KeyString implements KeyType<String> {
    public static final String NAME = "KeyString";


    public String getName() {
        return NAME;
    }

    public boolean equals(Object other) {
        return (other != null) && (other instanceof KeyString);
    }

    public int hashCode() {
        return 0;
    }

    public static class ConfigFormatter implements KeyTypeConfigFormatter<KeyString> {
        public KeyString fromJson(JSONValue jsonValue) {
            return new KeyString();
        }

        public JSONValue toJson(KeyString key) {
            return null;
        }
    }
}