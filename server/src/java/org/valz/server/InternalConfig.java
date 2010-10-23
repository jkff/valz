package org.valz.server;

import org.valz.model.AggregateRegistry;
import org.valz.backends.ReadBackend;
import org.valz.backends.WriteBackend;
import org.valz.keytypes.KeyTypeRegistry;

public class InternalConfig {
    public final int port;
    public final ReadBackend readBackend;
    public final WriteBackend writeBackend;
    public final KeyTypeRegistry keyTypeRegistry;
    public final AggregateRegistry aggregateRegistry;

    public InternalConfig(int port, ReadBackend readBackend, WriteBackend writeBackend,
                               KeyTypeRegistry keyTypeRegistry, AggregateRegistry aggregateRegistry) {
        this.port = port;
        this.readBackend = readBackend;
        this.writeBackend = writeBackend;
        this.keyTypeRegistry = keyTypeRegistry;
        this.aggregateRegistry = aggregateRegistry;
    }
}
