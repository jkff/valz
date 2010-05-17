package org.valz.util.aggregates;

public class AggregateRegistryCreator {

    public static AggregateRegistry create() {
        AggregateRegistry registry = new AggregateRegistry();
        registry.register(LongSum.NAME, new LongSum.ConfigFormatter());
        registry.register(LongMin.NAME, new LongSum.ConfigFormatter());
        registry.register(SortedMapMerge.NAME, new LongSum.ConfigFormatter());
        registry.register(AggregatesUnion.NAME, new LongSum.ConfigFormatter());
        return registry;
    }
}
