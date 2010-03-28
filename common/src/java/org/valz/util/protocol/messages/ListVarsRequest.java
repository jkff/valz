package org.valz.util.protocol.messages;

import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;
import org.valz.util.protocol.messages.Message;
import org.valz.util.protocol.MessageType;

public class ListVarsRequest extends Message {
    @NotNull
    public static ListVarsRequest parseDataString(@NotNull String dataString) throws ParseException {
        return new ListVarsRequest();
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.LIST_VARS_REQUEST;
    }

    @NotNull
    @Override
    public String toDataString() {
        return "";
    }
}
