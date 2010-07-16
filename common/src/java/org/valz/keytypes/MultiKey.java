package org.valz.keytypes;

import com.sdicons.json.model.JSONArray;
import com.sdicons.json.model.JSONValue;
import org.valz.aggregates.ParserException;

import java.util.ArrayList;
import java.util.List;

public class MultiKey implements KeyType<List<?>> {
    public static final String NAME = "MultiKey";

    private final List<KeyType> keys;

    public MultiKey(List<KeyType> collection) {
        this.keys = flat(collection);
    }

    public String getName() {
        return NAME;
    }

    public List<?> getMinValue() {
        List list = new ArrayList();
        for (KeyType key : keys) {
            list.add(key.getMinValue());
        }
        return list;
    }

    public JSONValue dataToJson(List<?> item) {
        if (keys.size() != item.size()) {
            throw new IllegalArgumentException("Size of item must be equal to keys size.");
        }
        JSONArray jsonArray = new JSONArray();
        List<JSONValue> listValue = jsonArray.getValue();
        for (int i=0; i<keys.size(); i++) {
            listValue.add(keys.get(i).dataToJson(item.get(i)));
        }
        return jsonArray;
    }

    public List<?> dataFromJson(JSONValue jsonValue) throws ParserException {
        JSONArray jsonArray = (JSONArray)jsonValue;
        List list = new ArrayList();
        for (int i=0; i<keys.size(); i++) {
            list.add(keys.get(i).dataFromJson(jsonArray.get(i)));
        }
        return list;
    }

    public List<KeyType> getKeys() {
        return keys;
    }

    public int compare(List<?> collection1, List<?> collection2) {
        if ((collection1.size() != collection2.size()) ||
                (keys.size() != collection2.size())) {
            throw new IllegalArgumentException("Size of both collections and keys size must be equals.");
        }

        for (int i=0; i<keys.size(); i++) {
            int res = keys.get(i).compare(collection1.get(i), collection2.get(i));
            if (res != 0) {
                return res;
            }
        }
        return 0;
    }

    public boolean equals(Object o) {
        return (o != null) && (o instanceof MultiKey) && ((MultiKey)o).keys.equals(this.keys);
    }

    public int hashCode() {
        return keys == null ? 0 : keys.hashCode();
    }

    private static List<KeyType> flat(List<KeyType> collection) {
        List<KeyType> res = new ArrayList<KeyType>();
        for (KeyType item : collection) {
            if (item instanceof MultiKey) {
                res.addAll(flat(((MultiKey)item).keys));
            } else {
                res.add(item);
            }
        }
        return res;
    }

    public static class ConfigFormatter implements KeyTypeConfigFormatter<MultiKey> {

        private final KeyTypeRegistry aggregateRegistry;

        public ConfigFormatter(KeyTypeRegistry aggregateRegistry) {
            this.aggregateRegistry = aggregateRegistry;
        }

        public MultiKey fromJson(JSONValue jsonValue) throws ParserException {
            List<KeyType> collection = new ArrayList<KeyType>();
            for (JSONValue item : ((JSONArray)jsonValue).getValue()) {
                collection.add(KeyTypeFormatter.fromJson(aggregateRegistry, item));
            }
            return new MultiKey(collection);
        }

        public JSONValue toJson(MultiKey key) {
            JSONArray json = new JSONArray();
            for (KeyType item : key.keys) {
                json.getValue().add(KeyTypeFormatter.toJson(aggregateRegistry, item));
            }
            return json;
        }
    }
}