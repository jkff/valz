package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class MapMerge extends AbstractAggregate<JSONObject> {

    private Aggregate<Object> mergeConflictsAggregate;

    public MapMerge(@NotNull Aggregate<Object> mergeConflictsAggregate) {
        this.mergeConflictsAggregate = mergeConflictsAggregate;
    }

    @Override
    @NotNull
    public JSONObject reduce(Iterator<JSONObject> stream) {
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


    @Override
    public Object toSerialized() {
        JSONObject json = new JSONObject();
        json.put("mergeConflictsAggregate", AggregateRegistry.toJson(mergeConflictsAggregate));
        return json;
    }

    public static MapMerge deserialize(Object object, AggregateRegistry registry) {
        Object jsonMergeConflictsAggregate = ((JSONObject) object).get("mergeConflictsAggregate");
        Aggregate mergeConflictsAggregate = registry.parseJson((JSONObject) jsonMergeConflictsAggregate);
        return new MapMerge(mergeConflictsAggregate);
    }
}
