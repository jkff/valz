package org.valz.util.protocol.messages;

import com.sdicons.json.mapper.JSONMapper;
import com.sdicons.json.mapper.MapperException;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateParser;
import org.valz.util.aggregates.ParserException;
import org.valz.util.protocol.InteractionType;

import static org.valz.util.Utils.makeJson;

public class RequestMessage<T> {
    private InteractionType<T,?> type = null;
    private T data = null;



    public static RequestMessage parse(AggregateRegistry registry, JSONValue json) throws ParserException {
        JSONObject jsonObject = (JSONObject)json;
        String strType = ((JSONString)jsonObject.get("type")).getValue();
        InteractionType<?, ?> type = InteractionType.ALL_TYPES.get(strType);
        Object data = null;

        if (InteractionType.GET_VALUE == type) {
            data = ((JSONString)jsonObject.get("data")).getValue();
        } else if (InteractionType.GET_AGGREGATE == type) {
            data = ((JSONString)jsonObject.get("data")).getValue();
        } else if (InteractionType.SUBMIT == type) {
            data = SubmitRequest.parse(registry, jsonObject.get("data"));
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

    public JSONValue toJson() {
        JSONValue jsonData = null;
        if (InteractionType.GET_VALUE == type) {
            jsonData = new JSONString((String)data);
        } else if (InteractionType.GET_AGGREGATE == type) {
            jsonData = new JSONString((String)data);
        } else if (InteractionType.SUBMIT == type) {
            jsonData = ((SubmitRequest)data).toJson();
        }

        return makeJson(
                "type", type,
                "data", jsonData);
    }
}
