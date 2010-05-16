package org.valz.viewer;

import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.AggregateRegistryCreator;
import org.valz.util.backends.ReadBackend;
import org.valz.util.backends.RemoteReadBackend;

import java.util.List;

public class ViewerInternalConfig {
    public final ReadBackend readBackend;
    public final AggregateRegistry registry;

    public ViewerInternalConfig(ReadBackend readBackend, AggregateRegistry registry) {
        this.readBackend = readBackend;
        this.registry = registry;
    }

    public static ViewerInternalConfig getConfig(List<String> urls) {
        AggregateRegistry registry = AggregateRegistryCreator.create();
        ReadBackend readBackend = new RemoteReadBackend(urls, registry, chunkSize);
        return new ViewerInternalConfig(readBackend, registry);
    }
}