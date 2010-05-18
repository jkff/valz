package org.valz.viewer;

import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.AggregateRegistryCreator;
import org.valz.util.backends.ReadBackend;
import org.valz.util.backends.RemoteReadBackend;

import java.util.List;

public class ViewerInternalConfig {
    public final ReadBackend readBackend;
    public final AggregateRegistry aggregateRegistry;

    public ViewerInternalConfig(ReadBackend readBackend, AggregateRegistry aggregateRegistry) {
        this.readBackend = readBackend;
        this.aggregateRegistry = aggregateRegistry;
    }

    public static ViewerInternalConfig getConfig(List<String> urls, int chunkSize) {
        AggregateRegistry aggregateRegistry = AggregateRegistryCreator.create();
        ReadBackend readBackend = new RemoteReadBackend(urls, aggregateRegistry, chunkSize);
        return new ViewerInternalConfig(readBackend, aggregateRegistry);
    }
}