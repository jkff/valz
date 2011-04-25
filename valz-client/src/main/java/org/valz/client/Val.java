package org.valz.client;

import org.valz.backends.RemoteWriteException;

public interface Val<T> {
    void submit(T sample) throws RemoteWriteException;
}
