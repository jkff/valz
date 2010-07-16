package org.valz.aggregates;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.Pair;

import java.util.Map;

import static org.valz.util.CollectionUtils.ar;
import static org.valz.util.JsonUtils.makeJson;

public class AggregateProduct<A, B> extends AbstractAggregate<Pair<A, B>> {
    public static final String NAME = "AggregateProduct";

    public final Aggregate<A> first;
    public final Aggregate<B> second;

    public AggregateProduct(Aggregate<A> first, Aggregate<B> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Pair<A, B> reduce(Pair<A, B> item1, Pair<A, B> item2) {
        return new Pair<A, B>(
                first.reduce(item1.first, item2.first),
                second.reduce(item1.second, item2.second));
    }

    public JSONValue dataToJson(Pair<A, B> item) {
        return makeJson(
                ar("first", "second"),
                ar(first.dataToJson(item.first), second.dataToJson(item.second)));
    }

    public Pair<A, B> dataFromJson(JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        Map<String, JSONValue> jsonMap = jsonObject.getValue();
        return new Pair<A, B>(
                first.dataFromJson(jsonMap.get("first")),
                second.dataFromJson(jsonMap.get("second")));
    }

    public String getName() {
        return NAME;
    }

    public static class Format extends AggregateFormat<AggregateProduct<?, ?>> {

        private final AggregateRegistry aggregateRegistry;

        public Format(AggregateRegistry aggregateRegistry) {
            this.aggregateRegistry = aggregateRegistry;
        }

        public AggregateProduct fromJson(JSONValue jsonValue) throws ParserException {
            JSONObject jsonObject = (JSONObject)jsonValue;

            String firstName = ((JSONString)jsonObject.get("firstName")).getValue();
            AggregateFormat firstFormat = aggregateRegistry.get(firstName);
            Aggregate firstAggregate = firstFormat.fromJson(jsonObject.get("firstAggregate"));

            String secondName = ((JSONString)jsonObject.get("secondName")).getValue();
            AggregateFormat secondFormat = aggregateRegistry.get(secondName);
            Aggregate secondAggregate = secondFormat.fromJson(jsonObject.get("secondAggregate"));

            return new AggregateProduct(firstAggregate, secondAggregate);
        }

        public JSONValue toJson(AggregateProduct aggregate) {
            AggregateFormat firstFormatter = aggregateRegistry.get(aggregate.first.getName());
            AggregateFormat secondFormatter = aggregateRegistry.get(aggregate.second.getName());
            return makeJson(ar("firstName", "firstAggregate", "secondName", "secondAggregate"),
                    ar(aggregate.first.getName(), firstFormatter.toJson(aggregate.first),
                            aggregate.second.getName(), secondFormatter.toJson(aggregate.second)));
        }

    }
}
