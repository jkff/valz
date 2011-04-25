package org.valz.viewer;

import org.valz.model.AggregateRegistry;
import org.valz.backends.ReadBackend;
import org.valz.backends.RemoteReadBackend;

import java.util.List;

public class ViewerInternalConfig {
    public final ReadBackend readBackend;
    public final AggregateRegistry aggregateRegistry;

    public ViewerInternalConfig(ReadBackend readBackend, AggregateRegistry aggregateRegistry) {
        this.readBackend = readBackend;
        this.aggregateRegistry = aggregateRegistry;
    }

    public static ViewerInternalConfig getConfig(List<String> urls, int chunkSize) {
        AggregateRegistry aggregateRegistry = AggregateRegistry.create();
        ReadBackend readBackend = new RemoteReadBackend(urls, aggregateRegistry);
        return new ViewerInternalConfig(readBackend, aggregateRegistry);
    }
}