package org.valz.server;

import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.backends.ReadBackend;
import org.valz.util.backends.ReadChunkBackend;
import org.valz.util.backends.WriteBackend;

public class InternalConfig {
    public final int port;
    public final ReadChunkBackend readChunkBackend;
    public final WriteBackend writeBackend;
    public final AggregateRegistry aggregateRegistry;

    public InternalConfig(int port, ReadChunkBackend readChunkBackend, WriteBackend writeBackend,
                               AggregateRegistry aggregateRegistry) {
        this.port = port;
        this.readChunkBackend = readChunkBackend;
        this.writeBackend = writeBackend;
        this.aggregateRegistry = aggregateRegistry;
    }
}
