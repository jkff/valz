package org.valz.util.protocol.messages;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.protocol.messages.Message;
import org.valz.util.protocol.MessageType;

import static org.valz.util.json.JSONBuilder.makeJson;

/**
 * Created on: 28.03.2010 12:26:12
 */
public class GetAggregateRequest extends Message {
    @NotNull
    public static GetAggregateRequest parseDataString(@NotNull String dataString) throws ParseException {
        JSONObject dataObject = (JSONObject)new JSONParser().parse(dataString);
        return new GetAggregateRequest((String) dataObject.get("name")
        );
    }

    private final String name;

    public GetAggregateRequest(@NotNull String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.GET_AGGREGATE_REQUEST;
    }

    @NotNull
    @Override
    public String toDataString() {
        return makeJson("name", name).toJSONString();
    }
}
