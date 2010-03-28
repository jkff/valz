package org.valz.util.protocol.messages;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.protocol.messages.Message;
import org.valz.util.protocol.MessageType;

import static org.valz.util.json.JSONBuilder.makeJson;

public class GetValueRequest extends Message<String> {
    public GetValueRequest(@NotNull String name) {
        super(name, MessageType.GET_VALUE_REQUEST);
    }

    public static GetValueRequest fromDataJson(@NotNull Object json) throws ParseException {
        return new GetValueRequest((String) ((JSONObject)json).get("name"));
    }

    public JSONObject dataToJson() {
        return makeJson("name", getData());
    }
}
