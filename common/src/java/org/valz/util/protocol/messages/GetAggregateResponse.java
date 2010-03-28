package org.valz.util.protocol.messages;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.protocol.MessageType;

import static org.valz.util.json.JSONBuilder.makeJson;

public class GetAggregateResponse extends Message<Aggregate<?>, JSONObject> {
    public GetAggregateResponse(Aggregate<?> aggregate) {
        super(aggregate, MessageType.GET_AGGREGATE_RESPONSE);
    }

    public static GetAggregateResponse fromDataJson(JSONObject json) throws ParseException {
        Aggregate<?> agg = AggregateRegistry.INSTANCE.parseAggregateString((JSONObject)json.get("aggregate"));
        return new GetAggregateResponse(agg);
    }

    public JSONObject dataToJson() {
        return makeJson("aggregate", AggregateRegistry.toJson(getData()));
    }
}