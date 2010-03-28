package org.valz.util.protocol.messages;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.protocol.messages.Message;
import org.valz.util.protocol.MessageType;

import static org.valz.util.json.JSONBuilder.makeJson;

public class GetValueResponse extends Message<Object> {
    public static GetValueResponse parseDataString(@NotNull String dataString) throws ParseException {
        JSONObject dataObject = (JSONObject)new JSONParser().parse(dataString);
        return new GetValueResponse(dataObject.get("value"));
    }

    public GetValueResponse(Object value) {
        super(value, MessageType.GET_VALUE_RESPONSE);
    }

    String toDataString() {
        return makeJson("value", getData()).toJSONString();
    }
}