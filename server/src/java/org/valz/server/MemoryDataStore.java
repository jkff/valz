package org.valz.server;

import org.valz.util.aggregates.Aggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataStore implements DataStore {

    private static MemoryDataStore instance = null;



    public static synchronized MemoryDataStore getInstance() {
        if (instance == null) {
            instance = new MemoryDataStore();
        }
        return instance;
    }



    private final Map<String, Object> name2val = new HashMap<String, Object>();
    private final Map<String, Aggregate<?>> name2aggregate = new HashMap<String, Aggregate<?>>();



    private MemoryDataStore() {}
    


    public void createAggregate(String name, Aggregate<?> aggregate, Object value) {
        name2aggregate.put(name, aggregate);
        name2val.put(name, value);
    }

    public Collection<String> listVars() {
        return new ArrayList<String>(name2val.keySet());
    }

    public Aggregate getAggregate(String name) {
        return name2aggregate.get(name);
    }

    public Object getValue(String name) {
        return name2val.get(name);
    }

    public void setValue(String name, Object value) {
        name2val.put(name, value);
    }
}
