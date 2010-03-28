package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import static org.valz.util.json.JSONBuilder.makeJson;
public class MapMerge extends AbstractAggregate<JSONObject> {
    public static MapMerge deserialize(Object object, AggregateRegistry registry) {
        Object aggregateObject = ((JSONObject) object).get("mergeConflictsAggregate");
        Aggregate<Object> mergeConflictsAggregate =
                (Aggregate<Object>)registry.parseAggregateString((JSONObject) aggregateObject);
        return new MapMerge(mergeConflictsAggregate);
    }

    private final Aggregate<Object> mergeConflictsAggregate;

    public MapMerge(@NotNull Aggregate<Object> mergeConflictsAggregate) {
        this.mergeConflictsAggregate = mergeConflictsAggregate;
    }



    @Override
    public Object toSerialized() {
        return makeJson(
                "mergeConflictsAggregate", AggregateRegistry.toJson(mergeConflictsAggregate));
    }
    
    @Override
    @NotNull
    public JSONObject reduce(@NotNull Iterator<JSONObject> stream) {
        JSONObject res = new JSONObject();
        while (stream.hasNext()) {
            for (Object objectEntry : stream.next().entrySet()) {
                Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) objectEntry;
                Object existingValue = res.get(entry.getKey());
                if (existingValue == null) {
                    res.put(entry.getKey(), entry.getValue());
                } else {
                    res.put(entry.getKey(),
                            mergeConflictsAggregate.reduce(existingValue, entry.getValue()));
                }
            }
        }
        return res;
    }

    @Override
    public JSONObject reduce(JSONObject item1, JSONObject item2) {
        return reduce(Arrays.asList(item1, item2).iterator());
    }
}
