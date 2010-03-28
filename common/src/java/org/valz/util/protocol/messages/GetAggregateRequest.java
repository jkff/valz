package org.valz.util.protocol.messages;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.protocol.messages.Message;
import org.valz.util.protocol.MessageType;

import static org.valz.util.json.JSONBuilder.makeJson;

public class GetAggregateRequest extends Message<String> {
    public GetAggregateRequest(@NotNull String name) {
        super(name, MessageType.GET_AGGREGATE_REQUEST);
    }

    @NotNull
    public static GetAggregateRequest fromDataJson(@NotNull Object json) throws ParseException {
        return new GetAggregateRequest((String) ((JSONObject)json).get("name"));
    }

    public JSONObject dataToJson() {
        return makeJson("name", getData());
    }
}
