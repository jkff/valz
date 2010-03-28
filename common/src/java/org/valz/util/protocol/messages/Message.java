package org.valz.util.protocol.messages;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.protocol.MessageType;

import java.io.IOException;

import static org.valz.util.json.JSONBuilder.makeJson;

public abstract class Message {
    @NotNull
    public static Message parseMessageString(@NotNull String messageString) throws IOException {
        try {
            JSONObject messageObject = (JSONObject)new JSONParser().parse(messageString);
            MessageType messageType = MessageType.valueOf((String)messageObject.get("messageType"));
            String dataString = (String)messageObject.get("data");

            switch (messageType) {
                case SUBMIT_REQUEST:
                    return SubmitRequest.parseDataString(dataString);
                case LIST_VARS_REQUEST:
                    return ListVarsRequest.parseDataString(dataString);
                case GET_VALUE_REQUEST:
                    return GetValueRequest.parseDataString(dataString);
                case GET_AGGREGATE_REQUEST:
                    return GetAggregateRequest.parseDataString(dataString);
                case LIST_VARS_RESPONSE:
                    return ListVarsResponse.parseDataString(dataString);
                case GET_VALUE_RESPONSE:
                    return GetValueResponse.parseDataString(dataString);
                case GET_AGGREGATE_RESPONSE:
                    return GetAggregateResponse.parseDataString(dataString);
            }
            throw new IllegalArgumentException(String.format("Can not serve request '%s'.", messageType));
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    Message() {
    }

    public abstract MessageType getMessageType();

    @NotNull
    abstract String toDataString();

    @NotNull
    public final String toMessageString() {
        return makeJson(
                "messageType", getMessageType().name(),
                "data", toDataString()
        ).toJSONString();
    }
}
