package org.valz.util.keytypes;

import com.sdicons.json.model.JSONNull;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.ParserException;

public class KeyString implements KeyType<String> {
    public static final String NAME = "KeyString";

    public String getName() {
        return NAME;
    }

    public String getMinValue() {
        return "";
    }

    public JSONValue dataToJson(String item) {
        return new JSONString(item);
    }

    public String dataFromJson(JSONValue jsonValue) throws ParserException {
        return ((JSONString)jsonValue).getValue();
    }

    public int compare(String s1, String s2) {
        return s1.compareTo(s2);
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
            return new JSONNull();
        }
    }
}
