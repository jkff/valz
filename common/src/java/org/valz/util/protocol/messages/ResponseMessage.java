package org.valz.util.protocol.messages;

import com.sdicons.json.mapper.JSONMapper;
import com.sdicons.json.mapper.MapperException;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.AggregateParser;
import org.valz.util.Value;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.ParserException;

import static org.valz.util.Utils.makeJson;

public class ResponseMessage<T> {

    public static ResponseMessage parse(AggregateRegistry registry, JSONValue json) throws ParserException {
        JSONObject jsonObject = (JSONObject)json;
        String strType = ((JSONString)jsonObject.get("type")).getValue();
        JSONValue jsonData = jsonObject.get("data");
        InteractionType<?, ?> type = InteractionType.ALL_TYPES.get(strType);
        Object data = null;

        if (InteractionType.GET_VALUE == type) {
            data = Value.parse(registry, jsonData);
        } else if (InteractionType.GET_AGGREGATE == type) {
            data = AggregateParser.parse(registry, jsonData);
        } else if (InteractionType.SUBMIT == type) {
            data = null;
        } else if (InteractionType.LIST_VARS == type) {
            try {
                data = JSONMapper.toJava(jsonData);
            } catch (MapperException e) {
                throw new ParserException(e);
            }
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

    public JSONValue toJson() {
        JSONValue jsonData = null;
        if (InteractionType.GET_VALUE == type) {
            jsonData = ((Value)data).toJson();
        } else if (InteractionType.GET_AGGREGATE == type) {
            jsonData = AggregateParser.toJson((Aggregate<?>)data);
        } else if (InteractionType.SUBMIT == type) {
            jsonData = null;
        } else if (InteractionType.LIST_VARS == type) {
            try {
                jsonData = JSONMapper.toJSON(data);
            } catch (MapperException e) {
                throw new RuntimeException(e);
            }
        }

        return makeJson(
                "type", type,
                "data", jsonData);
    }
}