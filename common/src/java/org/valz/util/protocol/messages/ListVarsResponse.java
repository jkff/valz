package org.valz.util.protocol.messages;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.protocol.messages.Message;
import org.valz.util.protocol.MessageType;

import java.util.Collection;

public class ListVarsResponse extends Message {
    @NotNull
    public static ListVarsResponse parseDataString(@NotNull String dataString) throws ParseException {
        JSONArray dataObject = (JSONArray)new JSONParser().parse(dataString);
        return new ListVarsResponse(dataObject);
    }

    

    private final Collection<String> vars;



    public ListVarsResponse(@NotNull Collection<String> vars) {
        this.vars = vars;
    }



    public Collection<String> getVars() {
        return vars;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.LIST_VARS_RESPONSE;
    }

    @NotNull
    @Override
    String toDataString() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(vars);
        return jsonArray.toJSONString();
    }
}