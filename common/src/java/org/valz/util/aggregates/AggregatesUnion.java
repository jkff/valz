package org.valz.util.aggregates;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.Pair;

import java.util.Map;

import static org.valz.util.JsonUtils.makeJson;

public class AggregatesUnion<A, B> extends AbstractAggregate<Pair<A,B>> {

    public static final String NAME = "AggregatesUnion";

    public final Aggregate<A> first;
    public final Aggregate<B> second;

    public AggregatesUnion(Aggregate<A> first, Aggregate<B> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Pair<A, B> reduce(Pair<A, B> item1, Pair<A, B> item2) {
        return new Pair<A,B>(first.reduce(item1.first, item2.first), second.reduce(item1.second, item2.second));
    }

    public JSONValue dataToJson(Pair<A, B> item) {
        return makeJson(
                    "first", first.dataToJson(item.first),
                    "second", second.dataToJson(item.second));
    }

    public Pair<A, B> dataFromJson(JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        Map<String, JSONValue> jsonMap = jsonObject.getValue();
        return new Pair<A,B>(first.dataFromJson(jsonMap.get("first")), second.dataFromJson(jsonMap.get("second")));
    }

    public String getName() {
        return NAME;
    }

    

    public static class ConfigFormatter implements AggregateConfigFormatter<AggregatesUnion<?,?>> {

        private final AggregateRegistry registry;

        public ConfigFormatter(AggregateRegistry registry) {
            this.registry = registry;
        }

        public AggregatesUnion fromJson(JSONValue jsonValue) throws ParserException {
            JSONObject jsonObject = (JSONObject)jsonValue;
            
            String firstName = ((JSONString)jsonObject.get("firstName")).getValue();
            AggregateConfigFormatter firstConfigFormatter = registry.get(firstName);
            Aggregate firstAggregate = firstConfigFormatter.fromJson(jsonObject.get("firstAggregate"));
            
            String secondName = ((JSONString)jsonObject.get("secondName")).getValue();
            AggregateConfigFormatter secondConfigFormatter = registry.get(secondName);
            Aggregate secondAggregate = secondConfigFormatter.fromJson(jsonObject.get("secondAggregate"));
            
            return new AggregatesUnion(firstAggregate, secondAggregate);
        }

        public JSONValue toJson(AggregatesUnion aggregate) {
            AggregateConfigFormatter firstFormatter = registry.get(aggregate.first.getName());
            AggregateConfigFormatter secondFormatter = registry.get(aggregate.second.getName());
            return makeJson(
                    "firstName", aggregate.first.getName(),
                    "firstAggregate", firstFormatter.toJson(aggregate.first),
                    "secondName", aggregate.second.getName(),
                    "secondAggregate", secondFormatter.toJson(aggregate.second));
        }

    }
}
