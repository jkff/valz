package org.valz.server;

import org.valz.util.aggregates.Aggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataStore implements DataStore {

    private final Map<String, Object> name2val = new HashMap<String, Object>();
    private final Map<String, Aggregate<?>> name2aggregate = new HashMap<String, Aggregate<?>>();



    public MemoryDataStore() {}
    


    public <T> void createAggregate(String name, Aggregate<T> aggregate, T value) {
        name2aggregate.put(name, aggregate);
        name2val.put(name, value);
    }

    public Collection<String> listVars() {
        return new ArrayList<String>(name2val.keySet());
    }

    public <T> Aggregate<T> getAggregate(String name) {
        return (Aggregate<T>)name2aggregate.get(name);
    }

    public <T> T getValue(String name) {
        return (T)name2val.get(name);
    }

    public <T> void setValue(String name, T value) {
        name2val.put(name, value);
    }
}
