package org.valz.util.aggregates;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.valz.util.CollectionUtils.ar;
import static org.valz.util.JsonUtils.makeJson;


public class SortedMapMerge<T> extends AbstractAggregate<SortedMap<String, T>> {
    public static final String NAME = "SortedMapMerge";

    public final Aggregate<? super T> mergeConflictsAggregate;

    public SortedMapMerge(@NotNull Aggregate<? super T> mergeConflictsAggregate) {
        this.mergeConflictsAggregate = mergeConflictsAggregate;
    }

    @Override
    public SortedMap<String, T> reduce(@NotNull Iterator<SortedMap<String, T>> stream) {
        SortedMap<String, T> res = new TreeMap<String, T>();
        while (stream.hasNext()) {
            for (Map.Entry<String, T> entry : stream.next().entrySet()) {
                T existingValue = res.get(entry.getKey());
                if (existingValue == null) {
                    res.put(entry.getKey(), entry.getValue());
                } else {
                    res.put(entry.getKey(),
                            (T)mergeConflictsAggregate.reduce(existingValue, entry.getValue()));
                }
            }
        }
        return res;
    }

    @Override
    public SortedMap<String, T> reduce(SortedMap<String, T> item1, SortedMap<String, T> item2) {
        return reduce(Arrays.asList(item1, item2).iterator());
    }

    public JSONValue dataToJson(SortedMap<String, T> item) {
        JSONObject obj = new JSONObject();
        Map<String, JSONValue> map = obj.getValue();
        for (Map.Entry<String, T> entry : item.entrySet()) {
            map.put(entry.getKey(), mergeConflictsAggregate.dataToJson(entry.getValue()));
        }
        return obj;
    }

    public SortedMap<String, T> dataFromJson(JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        TreeMap<String, T> map = new TreeMap<String, T>();
        for (Map.Entry<String, JSONValue> entry : jsonObject.getValue().entrySet()) {
            map.put(entry.getKey(), (T)mergeConflictsAggregate.dataFromJson(entry.getValue()));
        }
        return map;
    }

    public String getName() {
        return NAME;
    }


    public static class ConfigFormatter implements AggregateConfigFormatter<SortedMapMerge<?>> {

        private final AggregateRegistry aggregateRegistry;

        public ConfigFormatter(AggregateRegistry aggregateRegistry) {
            this.aggregateRegistry = aggregateRegistry;
        }

        public SortedMapMerge fromJson(JSONValue jsonValue) throws ParserException {
            JSONObject jsonObject = (JSONObject)jsonValue;
            String name = ((JSONString)jsonObject.get("name")).getValue();
            AggregateConfigFormatter configFormatter = aggregateRegistry.get(name);
            Aggregate aggregate = configFormatter.fromJson(jsonObject.get("aggregate"));
            return (SortedMapMerge)aggregate;
        }

        public JSONValue toJson(SortedMapMerge aggregate) {
            AggregateConfigFormatter formatter =
                    aggregateRegistry.get(aggregate.mergeConflictsAggregate.getName());
            return makeJson(ar("name", "aggregate"), ar(aggregate.mergeConflictsAggregate.getName(),
                    formatter.toJson(aggregate.mergeConflictsAggregate)));
        }

    }
}
