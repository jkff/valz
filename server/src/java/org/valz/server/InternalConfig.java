package org.valz.server;

import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.backends.ReadBackend;
import org.valz.util.backends.WriteBackend;

public class InternalConfig {
    public final int port;
    public final ReadBackend readBackend;
    public final WriteBackend writeBackend;
    public final AggregateRegistry registry;

    public InternalConfig(int port, ReadBackend readBackend, WriteBackend writeBackend,
                               AggregateRegistry registry) {
        this.port = port;
        this.readBackend = readBackend;
        this.writeBackend = writeBackend;
        this.registry = registry;
    }
}
