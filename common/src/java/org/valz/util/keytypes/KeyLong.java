package org.valz.util.keytypes;

import com.sdicons.json.model.JSONValue;

public class KeyLong implements KeyType<Long> {
    public static final String NAME = "KeyLong";


    public String getName() {
        return NAME;
    }

    public boolean equals(Object other) {
        return (other != null) && (other instanceof KeyLong);
    }

    public int hashCode() {
        return 0;
    }

    public static class ConfigFormatter implements KeyTypeConfigFormatter<KeyLong> {
        public KeyLong fromJson(JSONValue jsonValue) {
            return new KeyLong();
        }

        public JSONValue toJson(KeyLong key) {
            return null;
        }
    }
}