package org.valz.util.protocol.messages;

import com.sdicons.json.mapper.JSONMapper;
import com.sdicons.json.mapper.MapperException;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateParser;
import org.valz.util.protocol.InteractionType;

public class ResponseMessage<T> {

    public static <T> ResponseMessage<T> parse(AggregateRegistry registry, JSONValue json) throws MapperException {

        // TODO: write it correctly

        return (ResponseMessage<T>)JSONMapper.toJava(json, ResponseMessage.class);
    }



    private final InteractionType<?,T> type;
    private final T data;



    public ResponseMessage(InteractionType<?,T> type, T data) {
        this.type = type;
        this.data = data;
    }

    public JSONValue toJson() {
        // TODO: write it correctly

        try {
            return JSONMapper.toJSON(this);
        } catch (MapperException e) {
            throw new RuntimeException(e);
        }
    }

    public T getData() {
        return data;
    }

    public InteractionType<?, T> getType() {
        return type;
    }
}