package org.valz.server;

import org.valz.util.AggregateRegistry;
import org.valz.util.backends.ReadBackend;
import org.valz.util.backends.WriteBackend;

public class ValzServerConfiguration {
    public final int port;
    public final ReadBackend readBackend;
    public final WriteBackend writeBackend;
    public final AggregateRegistry registry;

    public ValzServerConfiguration(int port, ReadBackend readBackend, WriteBackend writeBackend,
                               AggregateRegistry registry) {
        this.port = port;
        this.readBackend = readBackend;
        this.writeBackend = writeBackend;
        this.registry = registry;
    }
}
