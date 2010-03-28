package org.valz.util.protocol;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;

import static org.valz.util.json.JSONBuilder.makeJson;

public class MessageGetValueResponse extends Message {
    @NotNull
    public static MessageGetValueResponse parseDataString(@NotNull String dataString) throws ParseException {
        JSONObject dataObject = (JSONObject)new JSONParser().parse(dataString);
        return new MessageGetValueResponse(
                (Aggregate<?>)dataObject.get("aggregate"),
                dataObject.get("value")
        );
    }



    private final Object value;
    private final Aggregate<?> aggregate;



    public MessageGetValueResponse(@NotNull Aggregate<?> aggregate, Object value) {
        this.value = value;
        this.aggregate = aggregate;
    }



    public Object getValue() {
        return value;
    }

    public Aggregate<?> getAggregate() {
        return aggregate;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.GET_VALUE_RESPONSE;
    }

    @NotNull
    @Override
    String toDataString() {
        return makeJson(
                "aggregate", AggregateRegistry.toAggregateString(aggregate),
                "value", value
        ).toJSONString();
    }
}