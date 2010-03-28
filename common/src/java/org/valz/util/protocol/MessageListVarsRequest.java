package org.valz.util.protocol;

import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

public class MessageListVarsRequest extends Message {
    @NotNull
    public static MessageListVarsRequest parseDataString(@NotNull String dataString) throws ParseException {
        return new MessageListVarsRequest();
    }



    @Override
    public MessageType getMessageType() {
        return MessageType.LIST_VARS_REQUEST;
    }

    @NotNull
    @Override
    String toDataString() {
        return "";
    }
}
