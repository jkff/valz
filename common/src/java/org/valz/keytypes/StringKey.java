package org.valz.keytypes;

import com.sdicons.json.model.JSONNull;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.ParserException;

public class StringKey implements KeyType<String> {
    public static final String NAME = "StringKey";

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
        if (s1 == null) {
            return s2 == null ? 0 : -1;
        }
        return s1.compareTo(s2);
    }

    public boolean equals(Object other) {
        return (other != null) && (other instanceof StringKey);
    }

    public int hashCode() {
        return 0;
    }

    public static class Format extends KeyTypeFormat<StringKey> {
        public StringKey fromJson(JSONValue jsonValue) {
            return new StringKey();
        }

        public JSONValue toJson(StringKey key) {
            return new JSONNull();
        }
    }
}
