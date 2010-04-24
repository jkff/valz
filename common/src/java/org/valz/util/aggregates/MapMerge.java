package org.valz.util.aggregates;

import com.sdicons.json.mapper.JSONMapper;
import com.sdicons.json.mapper.MapperException;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.jetbrains.annotations.NotNull;
import org.valz.util.AggregateRegistry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapMerge<K, V> extends AbstractAggregate<Map<K,V>> {
    public final Aggregate<? super V> mergeConflictsAggregate;

    public MapMerge(@NotNull Aggregate<? super V> mergeConflictsAggregate) {
        this.mergeConflictsAggregate = mergeConflictsAggregate;
    }

    @Override
    @NotNull
    public Map<K, V> reduce(@NotNull Iterator<Map<K,V>> stream) {
        Map<K, V> res = new HashMap<K, V>();
        while (stream.hasNext()) {
            for (Object entryObject : stream.next().entrySet()) {
                Map.Entry<K, V> entry = (Map.Entry<K, V>)entryObject;
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
    public Map<K, V> reduce(Map<K,V> item1, Map<K,V> item2) {
        return reduce(Arrays.asList(item1, item2).iterator());
    }

    public JSONValue dataToJson(Map<K, V> item) {
        try {
            return JSONMapper.toJSON(item);
        } catch (MapperException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<K, V> parseData(JSONValue json) throws ParserException {
        try {
            return (Map<K, V>)JSONMapper.toJava(json, HashMap.class);
        } catch (MapperException e) {
            throw new ParserException(e);
        }
    }

    public String getName() {
        return "MapMerge";
    }

    public JSONValue toJson() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", mergeConflictsAggregate.getName());
        map.put("aggregate", mergeConflictsAggregate.toJson());
        try {
            return JSONMapper.toJSON(map);
        } catch (MapperException e) {
            throw new RuntimeException(e);
        }
    }



    public static class Parser implements AggregateParser<Map> {

        private final AggregateRegistry registry;

        public Parser(AggregateRegistry registry) {
            this.registry = registry;
        }

        public MapMerge parse(JSONValue json) throws ParserException {
            JSONObject jsonObject = (JSONObject)json;
            String name = ((JSONString)jsonObject.get("name")).getValue();
            AggregateParser aggregateParser = registry.get(name);
            Aggregate aggregate = aggregateParser.parse(jsonObject.get("aggregate"));

            return new MapMerge(aggregate);
        }
    }
}
