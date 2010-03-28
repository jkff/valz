package org.valz.util.protocol.messages;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.valz.util.protocol.MessageType;

import static org.valz.util.json.JSONBuilder.makeJson;

public class GetValueResponse extends Message<Object, JSONObject> {
    public GetValueResponse(Object value) {
        super(value, MessageType.GET_VALUE_RESPONSE);
    }

    public static GetValueResponse fromDataJson(JSONObject json) throws ParseException {
        return new GetValueResponse(json.get("value"));
    }

    public JSONObject dataToJson() {
        return makeJson("value", getData());
    }
}