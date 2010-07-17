package org.valz.viewer;

import org.valz.aggregates.AggregateRegistry;
import org.valz.backends.ReadBackend;
import org.valz.backends.RemoteReadBackend;
import org.valz.keytypes.KeyTypeRegistry;

import java.util.List;

public class ViewerInternalConfig {
    public final ReadBackend readBackend;
    public final AggregateRegistry aggregateRegistry;

    public ViewerInternalConfig(ReadBackend readBackend, AggregateRegistry aggregateRegistry) {
        this.readBackend = readBackend;
        this.aggregateRegistry = aggregateRegistry;
    }

    public static ViewerInternalConfig getConfig(List<String> urls, int chunkSize) {
        KeyTypeRegistry keyTypeRegistry = KeyTypeRegistry.create();
        AggregateRegistry aggregateRegistry = AggregateRegistry.create();
        ReadBackend readBackend = new RemoteReadBackend(urls, keyTypeRegistry, aggregateRegistry);
        return new ViewerInternalConfig(readBackend, aggregateRegistry);
    }
}