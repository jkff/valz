package org.valz.server;

import org.valz.util.aggregates.Aggregate;

import java.util.Map;

public interface DataStore {
    Map<String, Aggregate<?>> loadName2Aggregate();
    void createAggregate(String name, Aggregate<?> aggregate);

    Object getValue(String name);
    void setValue(String name, Object value);
    void createValue(String name, Object value);
}
