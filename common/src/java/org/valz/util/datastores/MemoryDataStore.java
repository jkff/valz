package org.valz.util.datastores;

import org.valz.util.aggregates.Value;
import org.valz.util.aggregates.Aggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataStore implements DataStore {

    private final Map<String, Object> name2val = new HashMap<String, Object>();
    private final Map<String, Aggregate<?>> name2aggregate = new HashMap<String, Aggregate<?>>();


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

    public <T> Value<T> getValue(String name) {
        return new Value(name2aggregate.get(name), name2val.get(name));
    }

    public <T> void setValue(String name, T value) {
        name2val.put(name, value);
    }

    public synchronized <T> void modify(String name, Calculator<T> calculator) {
        T value = (T)name2val.get(name);
        T newValue = calculator.calculate(value);
        name2val.put(name, newValue);
    }

    public void removeAggregate(String name) {
        name2val.remove(name);
        name2aggregate.remove(name);
    }
}
