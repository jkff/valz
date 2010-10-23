package org.valz.model;

import com.sdicons.json.model.JSONArray;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.Pair;
import org.valz.util.ParserException;

import java.util.Map;

import static org.valz.util.CollectionUtils.ar;
import static org.valz.util.JsonUtils.makeJson;

public class AggregatePair<A, B> extends AbstractAggregate<Pair<A, B>> {
    public static final String NAME = "AggregatePair";

    public final Aggregate<A> first;
    public final Aggregate<B> second;

    public AggregatePair(Aggregate<A> first, Aggregate<B> second) {
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

    public static class Format extends AggregateFormat<AggregatePair<?, ?>> {

        private final AggregateRegistry aggregateRegistry;

        public Format(AggregateRegistry aggregateRegistry) {
            this.aggregateRegistry = aggregateRegistry;
        }

        public AggregatePair fromJson(JSONValue jsonValue) throws ParserException {
            JSONArray arr = (JSONArray) jsonValue;

            return new AggregatePair(
                    AggregateFormat.fromJson(aggregateRegistry, arr.get(0)),
                    AggregateFormat.fromJson(aggregateRegistry, arr.get(1)));
        }

        public JSONValue toJson(AggregatePair aggregate) {
            JSONArray arr = new JSONArray();
            arr.getValue().add(AggregateFormat.toJson(aggregateRegistry, aggregate.first));
            arr.getValue().add(AggregateFormat.toJson(aggregateRegistry, aggregate.second));
            return arr;
        }
    }
}
