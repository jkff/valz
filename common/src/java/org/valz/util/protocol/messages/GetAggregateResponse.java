package org.valz.util.protocol.messages;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.protocol.MessageType;

import static org.valz.util.json.JSONBuilder.makeJson;

public class GetAggregateResponse extends Message<Aggregate<?>> {
    public GetAggregateResponse(@NotNull Aggregate<?> aggregate) {
        super(aggregate, MessageType.GET_AGGREGATE_RESPONSE);
    }

    public static GetAggregateResponse parseDataString(@NotNull String dataString) throws ParseException {
        JSONObject dataObject = (JSONObject)new JSONParser().parse(dataString);
        Aggregate<?> agg = AggregateRegistry.INSTANCE.parseAggregateString(
                (JSONObject) dataObject.get("aggregate"));
        return new GetAggregateResponse(agg);
    }

    String toDataString() {
        return makeJson("aggregate", AggregateRegistry.toJson(getData())).toJSONString();
    }
}