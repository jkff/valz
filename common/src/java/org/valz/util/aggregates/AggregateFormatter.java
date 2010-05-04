package org.valz.util.aggregates;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;

import java.util.Map;

import static org.valz.util.JsonUtils.makeJson;

public class AggregateFormatter {
    public static JSONValue toJson(AggregateRegistry registry, Aggregate<?> aggregate) {
        AggregateConfigFormatter formatter = registry.get(aggregate.getName());
        return makeJson("name", aggregate.getName(), "config", formatter.toJson(aggregate));
    }

    public static Aggregate fromJson(AggregateRegistry registry, JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        Map<String, JSONValue> map = jsonObject.getValue();
        String name = ((JSONString)map.get("name")).getValue();
        AggregateConfigFormatter configFormatter = registry.get(name);
        return configFormatter.fromJson(map.get("config"));
    }

    private AggregateFormatter() {
    }
}
