package org.valz.util.protocol.messages;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.protocol.MessageType;

import java.io.IOException;

import static org.valz.util.json.JSONBuilder.makeJson;

public abstract class Message<T> {
    private MessageType type;
    private T data;

    @NotNull
    public static Message parseMessageString(@NotNull String messageString) throws IOException {
        try {
            JSONObject messageObject = (JSONObject)new JSONParser().parse(messageString);
            MessageType messageType = MessageType.valueOf((String)messageObject.get("messageType"));
            Object data = messageObject.get("data");

            switch (messageType) {
                case SUBMIT_REQUEST:
                    return SubmitRequest.fromDataJson(data);
                case LIST_VARS_REQUEST:
                    return ListVarsRequest.fromDataJson(data);
                case GET_VALUE_REQUEST:
                    return GetValueRequest.fromDataJson(data);
                case GET_AGGREGATE_REQUEST:
                    return GetAggregateRequest.fromDataJson(data);
                case LIST_VARS_RESPONSE:
                    return ListVarsResponse.fromDataJson(data);
                case GET_VALUE_RESPONSE:
                    return GetValueResponse.fromDataJson(data);
                case GET_AGGREGATE_RESPONSE:
                    return GetAggregateResponse.fromDataJson(data);
            }
            throw new IllegalArgumentException(String.format("Can not serve request '%s'.", messageType));
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    protected Message(T data, MessageType type) {
        this.data = data;
        this.type = type;
    }

    public MessageType getMessageType() {
        return type;
    }

    abstract Object dataToJson();

    @NotNull
    public final String toMessageString() {
        return makeJson(
                "messageType", getMessageType().name(),
                "data", dataToJson()
        ).toJSONString();
    }

    public T getData() {
        return data;
    }
}
