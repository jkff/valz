package org.valz.aggregates;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static org.valz.util.CollectionUtils.ar;
import static org.valz.util.JsonUtils.makeJson;

public abstract class AggregateFormat<T extends Aggregate<?>> {
    public abstract T fromJson(JSONValue jsonValue) throws ParserException;

    @Nullable
    public abstract JSONValue toJson(T aggregate);

    public static JSONValue toJson(AggregateRegistry aggregateRegistry, Aggregate<?> aggregate) {
        AggregateFormat format = aggregateRegistry.get(aggregate.getName());
        return makeJson(ar("name", "config"), ar(aggregate.getName(), format.toJson(aggregate)));
    }

    public static Aggregate fromJson(AggregateRegistry aggregateRegistry, JSONValue json) throws ParserException {
        JSONObject jsonObject = (JSONObject) json;
        Map<String, JSONValue> jsonMap = jsonObject.getValue();
        String name = ((JSONString)jsonMap.get("name")).getValue();
        AggregateFormat format = aggregateRegistry.get(name);
        return format.fromJson(jsonMap.get("config"));
    }
}
