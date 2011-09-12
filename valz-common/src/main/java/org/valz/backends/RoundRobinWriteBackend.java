package org.valz.backends;

import org.valz.model.Aggregate;

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
                // TODO This doesn't actually mean that the update has not been performed.
                // Maybe it has, but we failed to receive the server's confirmation.
                // TODO we need some sort of global consistency here.
                // Ignore, proceed to next
            }
        }
        throw new RemoteWriteException("All backends are down.");
    }

    public <T> void submitBigMap(String name, Aggregate<T> aggregate, Map<String, T> value) throws
            RemoteWriteException {
        for (int i = 0; i < writeBackends.size(); i++) {
            nextBackend = (nextBackend + 1) % writeBackends.size();
            try {
                writeBackends.get(nextBackend).submitBigMap(name, aggregate, value);
                return;
            } catch (RemoteWriteException e) {
                // Ignore, proceed to next
            }
        }
        throw new RemoteWriteException("All backends are down.");
    }
}
