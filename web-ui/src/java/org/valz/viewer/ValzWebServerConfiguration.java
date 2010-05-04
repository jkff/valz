package org.valz.viewer;

import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.backends.ReadBackend;

public class ValzWebServerConfiguration {
    public final int port;
    public final ReadBackend readBackend;
    public final AggregateRegistry registry;

    public ValzWebServerConfiguration(int port, ReadBackend readBackend, AggregateRegistry registry) {
        this.port = port;
        this.readBackend = readBackend;
        this.registry = registry;
    }
}