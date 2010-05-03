package org.valz.util.datastores;

import org.valz.util.Value;
import org.valz.util.aggregates.Aggregate;

import java.util.Collection;

public interface DataStore {

    <T> void createAggregate(String name, Aggregate<T> aggregate, T value);

    Collection<String> listVars();

    <T> Aggregate<T> getAggregate(String name);

    <T> Value<T> getValue(String name);

    <T> void setValue(String name, T value);

    void removeAggregate(String name);
}
