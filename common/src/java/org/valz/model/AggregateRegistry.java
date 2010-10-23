package org.valz.model;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;

import java.util.HashMap;
import java.util.Map;

public class AggregateRegistry {
    private final Map<String, AggregateFormat<?>> name2agg =
            new HashMap<String, AggregateFormat<?>>();

    public AggregateRegistry() {
    }

    public void register(String name, AggregateFormat<?> format) {
        if(null != name2agg.put(name, format)) {
            throw new IllegalArgumentException("Aggregate with this name already registered.");
        }
    }

    public AggregateFormat<?> get(String name) {
        return name2agg.get(name);
    }

    public static AggregateRegistry create() {
        AggregateRegistry aggregateRegistry = new AggregateRegistry();
        aggregateRegistry.register(LongSum.NAME, new LongSum.Format());
        aggregateRegistry.register(LongMin.NAME, new LongMin.Format());
        aggregateRegistry.register(SortedMapMerge.NAME, new SortedMapMerge.Format(aggregateRegistry));
        aggregateRegistry.register(AggregatePair.NAME, new AggregatePair.Format(aggregateRegistry));
        return aggregateRegistry;
    }
}
