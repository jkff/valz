package org.valz.viewer;

import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.AggregateRegistryCreator;
import org.valz.util.backends.ReadBackend;
import org.valz.util.backends.RemoteReadBackend;
import org.valz.util.keytypes.KeyTypeRegistry;
import org.valz.util.keytypes.KeyTypeRegistryCreator;

import java.util.List;

public class ViewerInternalConfig {
    public final ReadBackend readBackend;
    public final KeyTypeRegistry keyTypeRegistry;
    public final AggregateRegistry aggregateRegistry;

    public ViewerInternalConfig(ReadBackend readBackend, KeyTypeRegistry keyTypeRegistry, AggregateRegistry aggregateRegistry) {
        this.readBackend = readBackend;
        this.keyTypeRegistry = keyTypeRegistry;
        this.aggregateRegistry = aggregateRegistry;
    }

    public static ViewerInternalConfig getConfig(List<String> urls, int chunkSize) {
        KeyTypeRegistry keyTypeRegistry = KeyTypeRegistryCreator.create();
        AggregateRegistry aggregateRegistry = AggregateRegistryCreator.create();
        ReadBackend readBackend = new RemoteReadBackend(urls, keyTypeRegistry, aggregateRegistry, chunkSize);
        return new ViewerInternalConfig(readBackend, keyTypeRegistry, aggregateRegistry);
    }
}