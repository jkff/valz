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
        JSONObject dataObject = (JSONObject) new JSONParser().parse(dataString);
        return new MessageGetValueResponse(
                (String)dataObject.get("name"),
                (Aggregate<?>)dataObject.get("aggregate"),
                dataObject.get("value")
        );
    }



    private String name;
    private Object value;
    private Aggregate<?> aggregate;



    public MessageGetValueResponse(@NotNull String name, @NotNull Aggregate<?> aggregate, Object value) {
        this.name = name;
        this.value = value;
        this.aggregate = aggregate;
    }



    @Override
    public MessageType getMessageType() {
        return MessageType.GET_VALUE_RESPONSE;
    }

    @NotNull
    @Override
    String getDataString() {
        return makeJson(
                "name", name,
                "aggregate", AggregateRegistry.toJson(aggregate),
                "value", value
        ).toJSONString();
    }
}