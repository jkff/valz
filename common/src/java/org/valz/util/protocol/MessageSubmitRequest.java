package org.valz.util.protocol;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;

import static org.valz.util.json.JSONBuilder.makeJson;

public class MessageSubmitRequest extends Message {
    @NotNull
    public static MessageSubmitRequest parseDataString(@NotNull String dataString) throws ParseException {
        JSONObject dataObject = (JSONObject)new JSONParser().parse(dataString);
        return new MessageSubmitRequest(
                (String)dataObject.get("name"),
                (Aggregate<?>)dataObject.get("aggregate"),
                dataObject.get("value")
        );
    }

    

    private final String name;
    private final Object value;
    private final Aggregate<?> aggregate;



    public MessageSubmitRequest(@NotNull String name, @NotNull Aggregate<?> aggregate, Object value) {
        this.name = name;
        this.value = value;
        this.aggregate = aggregate;
    }



    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public Aggregate<?> getAggregate() {
        return aggregate;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.SUBMIT_REQUEST;
    }

    @NotNull
    @Override
    String toDataString() {
        return makeJson(
                "name", name,
                "aggregate", AggregateRegistry.toAggregateString(aggregate),
                "value", value
        ).toJSONString();
    }
}
