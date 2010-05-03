package org.valz.util.backends;

import org.valz.util.aggregates.Aggregate;

import java.util.List;

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
                break;
            } catch (RemoteWriteException e) {
                // Ignore
            }
        }
    }
}
