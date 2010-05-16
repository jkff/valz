package org.valz.util.protocol.messages;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;
import org.valz.util.JsonFormatter;
import org.valz.util.JsonParser;
import org.valz.util.JsonUtils;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateFormatter;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.ParserException;

import java.util.Map;

import static org.valz.util.JsonUtils.makeJson;

public class BigMapChunkValue<T> {
    public static <T> BigMapChunkValue<T> fromJson(AggregateRegistry registry, JSONValue json) throws
            ParserException {
        JSONObject jsonObject = (JSONObject)json;
        Map<String, JSONValue> map = jsonObject.getValue();
        final Aggregate<T> aggregate = AggregateFormatter.fromJson(registry, map.get("aggregate"));

        Object value = aggregate.dataFromJson(map.get("value"));
        return new BigMapChunkValue<T>(aggregate, JsonUtils.fromJson(map.get("value"), new JsonParser<T>() {
            public T fromJson(JSONValue jsonValue) throws ParserException {
                return aggregate.dataFromJson(jsonValue);
            }
        }));
    }

    private final Aggregate<T> aggregate;
    private final Map<String, T> value;

    public BigMapChunkValue(Aggregate<T> aggregate, Map<String, T> value) {
        this.aggregate = aggregate;
        this.value = value;
    }

    public Map<String, T> getValue() {
        return value;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }

    public JSONValue toJson(AggregateRegistry registry) {
        return makeJson("aggregate", AggregateFormatter.toJson(registry, aggregate), "value",
                makeJson(value, new JsonFormatter<T>() {
                    public JSONValue toJson(T item) {
                        return aggregate.dataToJson(item);
                    }
                }));
    }
}