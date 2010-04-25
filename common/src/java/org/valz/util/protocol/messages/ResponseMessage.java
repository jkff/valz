package org.valz.util.protocol.messages;

import com.sdicons.json.model.JSONArray;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.AggregateParser;
import org.valz.util.AggregateRegistry;
import org.valz.util.Value;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.ParserException;

import java.util.ArrayList;
import java.util.Collection;

import static org.valz.util.Utils.makeJson;

public class ResponseMessage<T> {

    public static ResponseMessage parse(AggregateRegistry registry, JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        String strType = ((JSONString)jsonObject.get("type")).getValue();
        JSONValue jsonData = jsonObject.get("data");
        InteractionType<?, ?> type = InteractionType.ALL_TYPES.get(strType);
        Object data = null;

        if (InteractionType.GET_VALUE.equals(type)) {
            data = Value.parse(registry, jsonData);
        } else if (InteractionType.GET_AGGREGATE.equals(type)) {
            data = AggregateParser.parse(registry, jsonData);
        } else if (InteractionType.SUBMIT.equals(type)) {
            data = null;
        } else if (InteractionType.LIST_VARS.equals(type)) {
            Collection<String> list = new ArrayList<String>();
            for (JSONValue item : ((JSONArray)jsonData).getValue()) {
                list.add(((JSONString)item).getValue());
            }
            data = list;
        }

        return new ResponseMessage(type, data);
    }



    private final InteractionType<?, T> type;
    private final T data;



    public ResponseMessage(InteractionType<?, T> type, T data) {
        this.type = type;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public InteractionType<?, T> getType() {
        return type;
    }

    public Object toJson() {
        Object jsonData = null;
        if (InteractionType.GET_VALUE == type) {
            jsonData = ((Value)data).toJson();
        } else if (InteractionType.GET_AGGREGATE == type) {
            jsonData = AggregateParser.toJson((Aggregate<?>)data);
        } else if (InteractionType.SUBMIT == type) {
            jsonData = null;
        } else if (InteractionType.LIST_VARS == type) {
            jsonData = data;
        }

        return makeJson("type", type.getCode(), "data", jsonData);
    }
}