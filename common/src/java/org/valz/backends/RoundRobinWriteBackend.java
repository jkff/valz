package org.valz.backends;

import org.valz.model.Aggregate;
import org.valz.keytypes.KeyType;

import java.util.List;
import java.util.Map;

public class RoundRobinWriteBackend implements WriteBackend {
    private final List<WriteBackend> writeBackends;
    private int nextBackend = 0;

    public RoundRobinWriteBackend(List<WriteBackend> writeBackends) {
        this.writeBackends = writeBackends;
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException {
        for (int i = 0; i < writeBackends.size(); i++) {
            nextBackend = (nextBackend + 1) % writeBackends.size();
            try {
                writeBackends.get(nextBackend).submit(name, aggregate, value);
                return;
            } catch (RemoteWriteException e) {
                // Ignore, proceed to next
            }
        }
        throw new RemoteWriteException("All backends are down.");
    }

    public <K, T> void submitBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> value) throws
            RemoteWriteException {
        for (int i = 0; i < writeBackends.size(); i++) {
            nextBackend = (nextBackend + 1) % writeBackends.size();
            try {
                writeBackends.get(nextBackend).submitBigMap(name, keyType, aggregate, value);
                return;
            } catch (RemoteWriteException e) {
                // Ignore, proceed to next
            }
        }
        throw new RemoteWriteException("All backends are down.");
    }
}
