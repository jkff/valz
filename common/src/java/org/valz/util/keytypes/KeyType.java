package org.valz.util.keytypes;

public interface KeyType<T> {
    String getName();

    /**
     * Will be used if a val is registered several times, to check
     * if it is registered with the same KeyType. 
     */
    boolean equals(Object other);
}