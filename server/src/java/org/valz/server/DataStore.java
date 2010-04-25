package org.valz.server;

import org.valz.util.Value;
import org.valz.util.aggregates.Aggregate;

import java.util.Collection;
import java.util.Map;

public interface DataStore {

    <T> void createAggregate(String name, Aggregate<T> aggregate, T value);
    Collection<String> listVars();
    <T> Aggregate<T> getAggregate(String name);

    <T> Value<T> getValue(String name);
    <T> void setValue(String name, T value);
}
