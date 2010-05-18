package org.valz.util.aggregates;

public class AggregateRegistryCreator {

    public static AggregateRegistry create() {
        AggregateRegistry aggregateRegistry = new AggregateRegistry();
        aggregateRegistry.register(LongSum.NAME, new LongSum.ConfigFormatter());
        aggregateRegistry.register(LongMin.NAME, new LongSum.ConfigFormatter());
        aggregateRegistry.register(SortedMapMerge.NAME, new LongSum.ConfigFormatter());
        aggregateRegistry.register(AggregatesUnion.NAME, new LongSum.ConfigFormatter());
        return aggregateRegistry;
    }
}
