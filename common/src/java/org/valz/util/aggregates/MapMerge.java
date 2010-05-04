//package org.valz.util.aggregates;
//
//import com.sdicons.json.mapper.JSONMapper;
//import com.sdicons.json.mapper.MapperException;
//import com.sdicons.json.model.JSONObject;
//import com.sdicons.json.model.JSONString;
//import com.sdicons.json.model.JSONValue;
//import org.jetbrains.annotations.NotNull;
//import org.valz.util.aggregates.AggregateRegistry;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//import static org.valz.util.JsonUtils.makeJson;
//
//
//public class MapMerge<K, V> extends AbstractAggregate<Map<K,V>> {
//    public final Aggregate<? super V> mergeConflictsAggregate;
//
//    public MapMerge(@NotNull Aggregate<? super V> mergeConflictsAggregate) {
//        this.mergeConflictsAggregate = mergeConflictsAggregate;
//    }
//
//    @Override
//    @NotNull
//    public Map<K, V> reduce(@NotNull Iterator<Map<K,V>> stream) {
//        Map<K, V> res = new HashMap<K, V>();
//        while (stream.hasNext()) {
//            for (Object entryObject : stream.next().entrySet()) {
//                Map.Entry<K, V> entry = (Map.Entry<K, V>)entryObject;
//                V existingValue = res.get(entry.getKey());
//                if (existingValue == null) {
//                    res.put(entry.getKey(), entry.getValue());
//                } else {
//                    res.put(entry.getKey(),
//                            (V)mergeConflictsAggregate.reduce(existingValue, entry.getValue()));
//                }
//            }
//        }
//        return res;
//    }
//
//    @Override
//    public Map<K, V> reduce(Map<K,V> item1, Map<K,V> item2) {
//        return reduce(Arrays.asList(item1, item2).iterator());
//    }
//
//    public Object dataToJson(Map<K, V> item) {
//        return item;
//    }
//
//    public Map<K, V> dataFromJson(JSONValue jsonValue) throws ParserException {
//
//    }
//
//    public String getName() {
//        return "MapMerge";
//    }
//
//    public Object toJson() {
//        return makeJson(
//                "name", mergeConflictsAggregate.getName(),
//                "aggregate", mergeConflictsAggregate.toJson());
//    }
//
//
//
//    public static class ConfigFormatter implements AggregateConfigFormatter<Map> {
//
//        private final AggregateRegistry registry;
//
//        public ConfigFormatter(AggregateRegistry registry) {
//            this.registry = registry;
//        }
//
//        public MapMerge fromJson(JSONValue jsonValue) throws ParserException {
//            JSONObject jsonObject = (JSONObject)jsonValue;
//            String name = ((JSONString)jsonObject.get("name")).getValue();
//            AggregateConfigFormatter configParser = registry.get(name);
//            Aggregate aggregate = configParser.fromJson(jsonObject.get("aggregate"));
//
//            return new MapMerge(aggregate);
//        }
//    }
//}
