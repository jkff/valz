package org.valz.util.keytypes;

import com.sdicons.json.model.JSONValue;

public class KeyDouble implements KeyType<Double> {
    public static final String NAME = "KeyDouble";


    public String getName() {
        return NAME;
    }

    public boolean equals(Object other) {
        return (other != null) && (other instanceof KeyDouble);
    }

    public int hashCode() {
        return 0;
    }

    public static class ConfigFormatter implements KeyTypeConfigFormatter<KeyDouble> {
        public KeyDouble fromJson(JSONValue jsonValue) {
            return new KeyDouble();
        }

        public JSONValue toJson(KeyDouble key) {
            return null;
        }
    }
}