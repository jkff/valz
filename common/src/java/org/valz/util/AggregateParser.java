package org.valz.util;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateConfigParser;
import org.valz.util.aggregates.ParserException;

import static org.valz.util.Utils.makeJson;

public class AggregateParser {

    public static JSONValue toJson(Aggregate<?> aggregate) {
        return makeJson(
                "name", aggregate.getName(),
                "config", aggregate.configToJson());
    }

    public static Aggregate parse(AggregateRegistry registry, JSONValue json) throws ParserException {
        JSONObject jsonObject = (JSONObject)json;
        String name = ((JSONString)jsonObject.get("name")).getValue();
        AggregateConfigParser configParser = registry.get(name);
        return configParser.parse(jsonObject.get("config"));
    }

    private AggregateParser() {}
}
