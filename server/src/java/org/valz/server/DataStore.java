package org.valz.server;

import org.valz.util.aggregates.Aggregate;

import java.util.Collection;
import java.util.Map;

public interface DataStore {

    void createAggregate(String name, Aggregate<?> aggregate, Object value);
    Collection<String> listVars();
    Aggregate getAggregate(String name);

    Object getValue(String name);
    void setValue(String name, Object value);
}
