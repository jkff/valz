package org.valz.util.protocol;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static org.valz.util.json.JSONBuilder.makeJson;

public class MessageGetValueRequest extends Message {
    @NotNull
    public static MessageGetValueRequest parseDataString(@NotNull String dataString) throws ParseException {
        JSONObject dataObject = (JSONObject) new JSONParser().parse(dataString);
        return new MessageGetValueRequest(
                (String) dataObject.get("name")
        );
    }



    private String name;


    
    public MessageGetValueRequest(@NotNull String name) {
        this.name = name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.GET_VALUE_REQUEST;
    }

    @NotNull
    @Override
    String getDataString() {
        return makeJson(
                "name", name
        ).toJSONString();
    }
}
