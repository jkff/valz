package org.valz.server;

import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.backends.ReadBackend;
import org.valz.util.backends.ReadChunkBackend;
import org.valz.util.backends.WriteBackend;
import org.valz.util.keytypes.KeyTypeRegistry;

public class InternalConfig {
    public final int port;
    public final ReadChunkBackend readChunkBackend;
    public final WriteBackend writeBackend;
    public final KeyTypeRegistry keyTypeRegistry;
    public final AggregateRegistry aggregateRegistry;

    public InternalConfig(int port, ReadChunkBackend readChunkBackend, WriteBackend writeBackend,
                               KeyTypeRegistry keyTypeRegistry, AggregateRegistry aggregateRegistry) {
        this.port = port;
        this.readChunkBackend = readChunkBackend;
        this.writeBackend = writeBackend;
        this.keyTypeRegistry = keyTypeRegistry;
        this.aggregateRegistry = aggregateRegistry;
    }
}
