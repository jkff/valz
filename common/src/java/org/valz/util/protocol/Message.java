package org.valz.util.protocol;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static org.valz.util.json.JSONBuilder.makeJson;

public abstract class Message {
    @NotNull
    public static Message parseMessageString(@NotNull String messageString) throws ParseException {
        JSONObject messageObject = (JSONObject) new JSONParser().parse(messageString);
        MessageType messageType = MessageType.valueOf((String) messageObject.get("messageType"));
        String dataString = (String) messageObject.get("data");

        switch (messageType) {
            case SUBMIT_REQUEST:
                return MessageSubmitRequest.parseDataString(dataString);
            case LIST_VARS_REQUEST:
                return MessageListVarsRequest.parseDataString(dataString);
            case GET_VALUE_REQUEST:
                return MessageGetValueRequest.parseDataString(dataString);
            case LIST_VARS_RESPONSE:
                return MessageListVarsResponse.parseDataString(dataString);
            case GET_VALUE_RESPONSE:
                return MessageGetValueResponse.parseDataString(dataString);
        }
        throw new IllegalArgumentException(String.format("Can not serve request '%s'.", messageType));
    }


    Message() {
    }


    public abstract MessageType getMessageType();

    @NotNull
    abstract String getDataString();

    @NotNull
    public final String toMessageString() {
        return makeJson(
                "messageType", MessageType.SUBMIT_REQUEST.name(),
                "data", getDataString()
        ).toJSONString();
    }
}
