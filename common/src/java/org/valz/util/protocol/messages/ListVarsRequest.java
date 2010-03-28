package org.valz.util.protocol.messages;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.valz.util.protocol.MessageType;

public class ListVarsRequest extends Message<Void, Object> {
    public ListVarsRequest() {
        super(null, MessageType.LIST_VARS_REQUEST);
    }

    public static ListVarsRequest fromDataJson(@NotNull Object json) throws ParseException {
        return new ListVarsRequest();
    }

    public JSONObject dataToJson() {
        return null;
    }
}
