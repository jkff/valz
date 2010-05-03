package org.valz.util;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateConfigFormatter;
import org.valz.util.aggregates.ParserException;

import java.util.Map;

import static org.valz.util.JsonUtils.makeJson;

public class AggregateFormatter {
    public static JSONValue toJson(AggregateRegistry registry, Aggregate<?> aggregate) {
        AggregateConfigFormatter formatter = registry.get(aggregate.getName());
        return makeJson("name", aggregate.getName(), "config", formatter.configToJson(aggregate));
    }

    public static Aggregate parse(AggregateRegistry registry, JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        Map<String, JSONValue> map = jsonObject.getValue();
        String name = ((JSONString)map.get("name")).getValue();
        AggregateConfigFormatter configFormatter = registry.get(name);
        return configFormatter.parse(map.get("config"));
    }

    private AggregateFormatter() {
    }
}
