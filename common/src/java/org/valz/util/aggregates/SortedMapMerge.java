package org.valz.util.aggregates;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.valz.util.JsonUtils.makeJson;


public class SortedMapMerge<V> extends AbstractAggregate<SortedMap<String,V>> {
    public static final String NAME = "SortedMapMerge";

    public final Aggregate<? super V> mergeConflictsAggregate;

    public SortedMapMerge(@NotNull Aggregate<? super V> mergeConflictsAggregate) {
        this.mergeConflictsAggregate = mergeConflictsAggregate;
    }

    @Override
    public SortedMap<String,V> reduce(@NotNull Iterator<SortedMap<String,V>> stream) {
        SortedMap<String,V> res = new TreeMap<String,V>();
        while (stream.hasNext()) {
            for (Map.Entry<String, V> entry : stream.next().entrySet()) {
                V existingValue = res.get(entry.getKey());
                if (existingValue == null) {
                    res.put(entry.getKey(), entry.getValue());
                } else {
                    res.put(entry.getKey(),
                            (V)mergeConflictsAggregate.reduce(existingValue, entry.getValue()));
                }
            }
        }
        return res;
    }

    @Override
    public SortedMap<String,V> reduce(SortedMap<String,V> item1, SortedMap<String,V> item2) {
        return reduce(Arrays.asList(item1, item2).iterator());
    }

    public JSONValue dataToJson(SortedMap<String,V> item) {
        JSONObject obj = new JSONObject();
        Map<String, JSONValue> map = obj.getValue();
        for (Map.Entry<String, V> entry : item.entrySet()) {
            map.put(entry.getKey(), mergeConflictsAggregate.dataToJson(entry.getValue()));
        }
        return obj;
    }

    public SortedMap<String,V> dataFromJson(JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        TreeMap<String, V> map = new TreeMap<String, V>();
        for (Map.Entry<String, JSONValue> entry : jsonObject.getValue().entrySet()) {
            map.put(entry.getKey(), (V)mergeConflictsAggregate.dataFromJson(entry.getValue()));
        }
        return map;
    }

    public String getName() {
        return NAME;
    }


    public static class ConfigFormatter implements AggregateConfigFormatter<SortedMapMerge<?>> {

        private final AggregateRegistry registry;

        public ConfigFormatter(AggregateRegistry registry) {
            this.registry = registry;
        }

        public SortedMapMerge fromJson(JSONValue jsonValue) throws ParserException {
            JSONObject jsonObject = (JSONObject)jsonValue;
            String name = ((JSONString)jsonObject.get("name")).getValue();
            AggregateConfigFormatter configParser = registry.get(name);
            Aggregate aggregate = configParser.fromJson(jsonObject.get("aggregate"));
            return (SortedMapMerge)aggregate;
        }

        public JSONValue toJson(SortedMapMerge aggregate) {
            AggregateConfigFormatter formatter = registry.get(aggregate.mergeConflictsAggregate.getName());
            return makeJson("name",
                    aggregate.mergeConflictsAggregate.getName(),
                    "aggregate",
                    formatter.toJson(aggregate.mergeConflictsAggregate));
        }

    }
}
