package org.valz.util.protocol;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;

import static org.valz.util.json.JSONBuilder.makeJson;

public class MessageListVarsResponse extends Message {
    @NotNull
    public static MessageListVarsResponse parseDataString(@NotNull String dataString) throws ParseException {
        JSONArray dataObject = (JSONArray) new JSONParser().parse(dataString);
        return new MessageListVarsResponse(dataObject);
    }


    private final List<String> vars;



    public MessageListVarsResponse(@NotNull List<String> vars) {
        this.vars = vars;
    }


    
    public List<String> getVars() {
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