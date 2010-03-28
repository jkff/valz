package org.valz.util.protocol.messages;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.protocol.messages.Message;
import org.valz.util.protocol.MessageType;

import java.util.Collection;

public class ListVarsResponse extends Message<Collection<String>> {
    public static ListVarsResponse parseDataString(@NotNull String dataString) throws ParseException {
        JSONArray dataObject = (JSONArray)new JSONParser().parse(dataString);
        return new ListVarsResponse(dataObject);
    }

    public ListVarsResponse(@NotNull Collection<String> vars) {
        super(vars, MessageType.LIST_VARS_RESPONSE);
    }

    String toDataString() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(getData());
        return jsonArray.toJSONString();
    }
}