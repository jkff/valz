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

    public static GetValueRequest parseDataString(@NotNull String dataString) throws ParseException {
        JSONObject dataObject = (JSONObject)new JSONParser().parse(dataString);
        return new GetValueRequest(
                (String)dataObject.get("name")
        );
    }

    public String toDataString() {
        return makeJson("name", getData()).toJSONString();
    }
}
