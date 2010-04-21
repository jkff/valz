package org.valz.server;

import org.valz.util.aggregates.Aggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SynchronizedDataStore implements DataStore {

    private final DataStore dataStore;
    private ReadWriteLock lock = new ReentrantReadWriteLock();



    public SynchronizedDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }



    public void createAggregate(String name, Aggregate<?> aggregate, Object value) {
        try {
            lock.writeLock().lock();
            dataStore.createAggregate(name, aggregate, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Collection<String> listVars() {
        try {
            lock.readLock().lock();
            return dataStore.listVars();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Aggregate getAggregate(String name) {
        try {
            lock.readLock().lock();
            return dataStore.getAggregate(name);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Object getValue(String name) {
        try {
            lock.readLock().lock();
            return dataStore.getValue(name);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setValue(String name, Object value) {
        try {
            lock.writeLock().lock();
            dataStore.setValue(name, value);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
