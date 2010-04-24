package org.valz.util;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateConfigParser;
import org.valz.util.aggregates.ParserException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.valz.util.Utils.makeJson;

public class AggregateRegistry {
    private final Map<String, AggregateConfigParser<?>> name2agg = new HashMap<String, AggregateConfigParser<?>>();

    public void register(String name, AggregateConfigParser<?> configParser) {
        if (name2agg.containsKey(name)) {
            throw new IllegalArgumentException("Aggregate with this name already registered.");
        }
        name2agg.put(name, configParser);
    }

    public void unregister(String name) {
        name2agg.remove(name);
    }

    public AggregateConfigParser<?> get(String name) {
        return name2agg.get(name);
    }

    public Collection<String> listNames() {
        return new ArrayList<String>(name2agg.keySet());
    }




    public JSONValue pickleAggregate(Aggregate<?> aggregate) {
        return makeJson(
                "name", aggregate.getName(),
                "aggregate", aggregate.configToJson());
    }

    public <T> Aggregate<T> unpickleAggregate(JSONValue jsonValue) throws ParserException {
        // TODO: lots of checks
        JSONObject jsonObject = (JSONObject)jsonValue;
        String name = ((JSONString)jsonObject.get("name")).getValue();
        AggregateConfigParser<T> configParser = (AggregateConfigParser<T>)get(name);
        Aggregate<T> aggregate = configParser.parse(jsonObject.get("aggregate"));
        return aggregate;
    }
}
