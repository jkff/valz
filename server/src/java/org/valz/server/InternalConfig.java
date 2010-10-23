package org.valz.server;

import org.valz.model.AggregateRegistry;
import org.valz.backends.ReadBackend;
import org.valz.backends.WriteBackend;

public class InternalConfig {
    public final int port;
    public final ReadBackend readBackend;
    public final WriteBackend writeBackend;
    public final AggregateRegistry aggregateRegistry;

    public InternalConfig(int port, ReadBackend readBackend, WriteBackend writeBackend,
                          AggregateRegistry aggregateRegistry) {
        this.port = port;
        this.readBackend = readBackend;
        this.writeBackend = writeBackend;
        this.aggregateRegistry = aggregateRegistry;
    }
}
