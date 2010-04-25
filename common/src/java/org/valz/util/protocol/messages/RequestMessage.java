package org.valz.util.protocol.messages;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.ParserException;

import java.util.Map;

import static org.valz.util.Utils.makeJson;

public class RequestMessage<T> {
    private InteractionType<T, ?> type = null;
    private T data = null;



    public static RequestMessage parse(AggregateRegistry registry, JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        Map<String, JSONValue> map = jsonObject.getValue();
        String strType = ((JSONString)map.get("type")).getValue();
        JSONValue jsonData = map.get("data");
        InteractionType<?, ?> type = InteractionType.ALL_TYPES.get(strType);
        Object data = null;

        if (InteractionType.GET_VALUE == type) {
            data = ((JSONString)jsonData).getValue();
        } else if (InteractionType.GET_AGGREGATE == type) {
            data = ((JSONString)jsonData).getValue();
        } else if (InteractionType.SUBMIT == type) {
            data = SubmitRequest.parse(registry, jsonData);
        } else if (InteractionType.LIST_VARS == type) {
            data = null;
        }

        return new RequestMessage(type, data);
    }



    public RequestMessage(InteractionType<T, ?> type, T data) {
        this.type = type;
        this.data = data;
    }

    public InteractionType getType() {
        return type;
    }

    public T getData() {
        return data;
    }

    public Object toJson() {
        Object jsonData = null;
        if (InteractionType.GET_VALUE == type) {
            jsonData = data;
        } else if (InteractionType.GET_AGGREGATE == type) {
            jsonData = data;
        } else if (InteractionType.SUBMIT == type) {
            jsonData = ((SubmitRequest)data).toJson();
        } else if (InteractionType.LIST_VARS == type) {
            jsonData = null;
        }

        return makeJson("type", type.getCode(), "data", jsonData);
    }
}
