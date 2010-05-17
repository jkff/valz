package org.valz.util.keytypes;

import com.sdicons.json.model.JSONArray;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.ParserException;

import java.util.ArrayList;
import java.util.Collection;

public class MultiKey implements KeyType<Collection> {
    public static final String NAME = "MultiKey";

    private final Collection<KeyType> keys;


    public MultiKey(Collection<KeyType> collection) {
        this.keys = collection;
    }

    public String getName() {
        return NAME;
    }

    public boolean equals(Object o) {
        return (o != null) && (o instanceof MultiKey) && ((MultiKey)o).keys.equals(this.keys);
    }

    public int hashCode() {
        return 0;
    }

    public static class ConfigFormatter implements KeyTypeConfigFormatter<MultiKey> {

        private final KeyTypeRegistry registry;

        public ConfigFormatter(KeyTypeRegistry registry) {
            this.registry = registry;
        }

        public MultiKey fromJson(JSONValue jsonValue) throws ParserException {
            Collection<KeyType> collection = new ArrayList<KeyType>();
            for (JSONValue item : ((JSONArray)jsonValue).getValue()) {
                collection.add(KeyTypeFormatter.fromJson(registry, item));
            }
            return new MultiKey(collection);
        }

        public JSONValue toJson(MultiKey key) {
            JSONArray json = new JSONArray();
            for (KeyType item : key.keys) {
                json.getValue().add(KeyTypeFormatter.toJson(registry, item));
            }
            return json;
        }
    }
}