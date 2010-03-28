package org.valz.util.protocol.messages;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.protocol.messages.Message;
import org.valz.util.protocol.MessageType;

import static org.valz.util.json.JSONBuilder.makeJson;

public class GetAggregateRequest extends Message<String, JSONObject> {
    public GetAggregateRequest(String name) {
        super(name, MessageType.GET_AGGREGATE_REQUEST);
    }

    public static GetAggregateRequest fromDataJson(JSONObject json) throws ParseException {
        return new GetAggregateRequest((String)json.get("name"));
    }

    public JSONObject dataToJson() {
        return makeJson("name", getData());
    }
}
