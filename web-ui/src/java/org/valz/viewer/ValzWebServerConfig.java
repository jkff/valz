package org.valz.viewer;

import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.backends.ReadBackend;

public class ValzWebServerConfig {
    public final int port;
    public final ReadBackend readBackend;
    public final AggregateRegistry registry;

    public ValzWebServerConfig(int port, ReadBackend readBackend, AggregateRegistry registry) {
        this.port = port;
        this.readBackend = readBackend;
        this.registry = registry;
    }
}