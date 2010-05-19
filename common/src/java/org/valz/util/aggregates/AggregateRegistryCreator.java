package org.valz.util.aggregates;

public class AggregateRegistryCreator {

    public static AggregateRegistry create() {
        AggregateRegistry aggregateRegistry = new AggregateRegistry();
        aggregateRegistry.register(LongSum.NAME, new LongSum.ConfigFormatter());
        aggregateRegistry.register(LongMin.NAME, new LongMin.ConfigFormatter());
        aggregateRegistry.register(SortedMapMerge.NAME, new SortedMapMerge.ConfigFormatter(aggregateRegistry));
        aggregateRegistry.register(AggregatesUnion.NAME, new AggregatesUnion.ConfigFormatter(aggregateRegistry));
        return aggregateRegistry;
    }
}
