package org.valz.util.aggregates;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.keytypes.KeyType;

import java.util.Map;

import static org.valz.util.JsonUtils.makeJson;
import static org.valz.util.CollectionUtils.*;

public class AggregateFormatter {
    public static JSONValue toJson(AggregateRegistry aggregateRegistry, Aggregate<?> aggregate) {
        AggregateConfigFormatter formatter = aggregateRegistry.get(aggregate.getName());
        return makeJson(ar("name", "config"), ar(aggregate.getName(), formatter.toJson(aggregate)));
    }

    public static Aggregate fromJson(AggregateRegistry aggregateRegistry, JSONValue json) throws ParserException {
        JSONObject jsonObject = (JSONObject) json;
        Map<String, JSONValue> jsonMap = jsonObject.getValue();
        String name = ((JSONString)jsonMap.get("name")).getValue();
        AggregateConfigFormatter configFormatter = aggregateRegistry.get(name);
        return configFormatter.fromJson(jsonMap.get("config"));
    }

    private AggregateFormatter() {
    }
}
