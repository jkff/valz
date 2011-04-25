package org.valz.protocol.messages;

import com.sdicons.json.model.*;
import org.jetbrains.annotations.NotNull;
import org.valz.util.Pair;
import org.valz.model.AggregateRegistry;
import org.valz.util.ParserException;
import org.valz.model.Sample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class InteractionType<I, O> {
    private static final Map<String, InteractionType<?, ?>> ALL_TYPES =
            new HashMap<String, InteractionType<?, ?>>();

    public static Pair<InteractionType, Object> requestFromJson(
            JSONValue request, AggregateRegistry aggregateRegistry)
            throws ParserException
    {
        JSONObject jsonObject = (JSONObject)request;
        Map<String, JSONValue> map = jsonObject.getValue();
        String strType = ((JSONString)map.get("type")).getValue();
        JSONValue jsonData = map.get("data");
        InteractionType<?, ?> type = InteractionType.getByCode(strType);
        return new Pair<InteractionType, Object>(type,
                type.requestBodyFromJson(jsonData, aggregateRegistry));
    }

    public static Pair<InteractionType, Object> responseFromJson(
            JSONValue response, AggregateRegistry aggregateRegistry)
            throws ParserException
    {
        JSONObject jsonObject = (JSONObject)response;
        Map<String, JSONValue> map = jsonObject.getValue();
        String strType = ((JSONString)map.get("type")).getValue();
        JSONValue jsonData = map.get("data");
        InteractionType<?, ?> type = InteractionType.getByCode(strType);
        return new Pair<InteractionType, Object>(type,
                type.responseBodyFromJson(jsonData, aggregateRegistry));
    }

    public static <I> JSONValue requestToJson(InteractionType<I, ?> type, I request,
                                              AggregateRegistry aggregateRegistry) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.getValue().put("type", new JSONString(type.code));
        jsonObject.getValue()
                .put("data", type.requestBodyToJson(request, aggregateRegistry));
        return jsonObject;
    }

    public static <O> JSONValue responseToJson(InteractionType<?, O> type, O response,
                                               AggregateRegistry aggregateRegistry) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.getValue().put("type", new JSONString(type.code));
        jsonObject.getValue()
                .put("data", type.responseBodyToJson(response, aggregateRegistry));
        return jsonObject;
    }

    // {type: "SUBMIT", data: {name: "hitCount", aggregate: {type: "LongSum", config: null}, value: 5}}

    public static final InteractionType<SubmitRequest, Void> SUBMIT =
            new InteractionType<SubmitRequest, Void>("SUBMIT") {
                public SubmitRequest requestBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry)
                    throws ParserException
                {
                    return SubmitRequest.fromJson(aggregateRegistry, json);
                }

                @NotNull
                public JSONValue requestBodyToJson(SubmitRequest request, AggregateRegistry aggregateRegistry) {
                    return request.toJson(aggregateRegistry);
                }

                public Void responseBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry) {
                    return null;
                }

                @NotNull
                public JSONValue responseBodyToJson(Void response, AggregateRegistry aggregateRegistry) {
                    return new JSONNull();
                }
            };

    // {type: "GET_VALUE", data: "hitCount"}

    public static final InteractionType<String, Sample<?>> GET_VALUE =
            new InteractionType<String, Sample<?>>("GET_VALUE") {
                public String requestBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry) {
                    return ((JSONString)json).getValue();
                }

                @NotNull
                public JSONValue requestBodyToJson(String request, AggregateRegistry aggregateRegistry) {
                    return new JSONString(request);
                }

                public Sample<?> responseBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry) throws
                ParserException {
                    return Sample.fromJson(aggregateRegistry, json);
                }

                @NotNull
                public JSONValue responseBodyToJson(Sample<?> response, AggregateRegistry aggregateRegistry) {
                    return response.toJson(aggregateRegistry);
                }
            };


    // {type: "GET_VALUE", data: null}

    public static final InteractionType<Void, Collection<String>> LIST_VALS =
            new InteractionType<Void, Collection<String>>("LIST_VALS") {
                public Void requestBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry) {
                    return null;
                }

                @NotNull
                public JSONValue requestBodyToJson(Void request, AggregateRegistry aggregateRegistry) {
                    return new JSONNull();
                }

                public Collection<String> responseBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry) {
                    Collection<String> list = new ArrayList<String>();
                    for (JSONValue item : ((JSONArray)json).getValue()) {
                        list.add(((JSONString)item).getValue());
                    }
                    return list;
                }

                @NotNull
                public JSONValue responseBodyToJson(Collection<String> response, AggregateRegistry aggregateRegistry) {
                    JSONArray json = new JSONArray();
                    for (String item : response) {
                        json.getValue().add(new JSONString(item));
                    }
                    return json;
                }
            };

    // {type: "REMOVE_VALUE", data: "hitCount"}

    public static final InteractionType<String, Void> REMOVE_VALUE =
            new InteractionType<String, Void>("REMOVE_VALUE") {
                public String requestBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry) {
                    return ((JSONString)json).getValue();
                }

                @NotNull
                public JSONValue requestBodyToJson(String request, AggregateRegistry aggregateRegistry) {
                    return new JSONString(request);
                }

                public Void responseBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry) {
                    return null;
                }

                @NotNull
                public JSONValue responseBodyToJson(Void response, AggregateRegistry aggregateRegistry) {
                    return null;
                }
            };

    // {type: "SUBMIT_BIG_MAP", data: {name: "hitsByIp", aggregate: {type: "LongSum"}, value: {"192.168.1.1": 10, ...}}}

    public static final InteractionType<SubmitBigMapRequest, Void> SUBMIT_BIG_MAP =
            new InteractionType<SubmitBigMapRequest, Void>("SUBMIT_BIG_MAP") {
                public SubmitBigMapRequest requestBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry)
                        throws ParserException
                {
                    return SubmitBigMapRequest.fromJson(aggregateRegistry, json);
                }

                @NotNull
                public JSONValue requestBodyToJson(SubmitBigMapRequest request, AggregateRegistry aggregateRegistry) {
                    return request.toJson(aggregateRegistry);
                }

                public Void responseBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry) {
                    return null;
                }

                @NotNull
                public JSONValue responseBodyToJson(Void response, AggregateRegistry aggregateRegistry) {
                    return new JSONNull();
                }
            };

    public static final InteractionType<GetBigMapChunkRequest, BigMapChunkValue> GET_BIG_MAP_CHUNK =
            new InteractionType<GetBigMapChunkRequest, BigMapChunkValue>("GET_BIG_MAP_CHUNK") {
                public GetBigMapChunkRequest requestBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry)
                        throws ParserException
                {
                    return GetBigMapChunkRequest.fromJson(json);
                }

                @NotNull
                public JSONValue requestBodyToJson(GetBigMapChunkRequest request, AggregateRegistry aggregateRegistry) {
                    return request.toJson();
                }

                public BigMapChunkValue responseBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry)
                    throws ParserException
                {
                    return BigMapChunkValue.fromJson(aggregateRegistry, json);
                }

                @NotNull
                public JSONValue responseBodyToJson(BigMapChunkValue response, AggregateRegistry aggregateRegistry) {
                    return response.toJson(aggregateRegistry);
                }
            };

    public static final InteractionType<Void, Collection<String>> LIST_BIG_MAPS =
            new InteractionType<Void, Collection<String>>("LIST_BIG_MAPS") {
                public Void requestBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry) {
                    return null;
                }

                @NotNull
                public JSONValue requestBodyToJson(Void request, AggregateRegistry aggregateRegistry) {
                    return new JSONNull();
                }

                public Collection<String> responseBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry) {
                    Collection<String> list = new ArrayList<String>();
                    for (JSONValue item : ((JSONArray)json).getValue()) {
                        list.add(((JSONString)item).getValue());
                    }
                    return list;
                }

                @NotNull
                public JSONValue responseBodyToJson(Collection<String> response, AggregateRegistry aggregateRegistry) {
                    JSONArray json = new JSONArray();
                    for (String item : response) {
                        json.getValue().add(new JSONString(item));
                    }
                    return json;
                }
            };

    public static final InteractionType<String, Void> REMOVE_BIG_MAP =
            new InteractionType<String, Void>("REMOVE_BIG_MAP") {
                public String requestBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry) {
                    return ((JSONString)json).getValue();
                }

                @NotNull
                public JSONValue requestBodyToJson(String request, AggregateRegistry aggregateRegistry) {
                    return new JSONString(request);
                }

                public Void responseBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry) {
                    return null;
                }

                @NotNull
                public JSONValue responseBodyToJson(Void response, AggregateRegistry aggregateRegistry) {
                    return null;
                }
            };



    private final String code;

    private InteractionType(String code) {
        this.code = code;
        ALL_TYPES.put(code, this);
    }

    public static InteractionType<?, ?> getByCode(String code) {
        return ALL_TYPES.get(code);
    }

    public abstract I requestBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry) throws ParserException;

    @NotNull
    public abstract JSONValue requestBodyToJson(I request, AggregateRegistry aggregateRegistry);

    public abstract O responseBodyFromJson(JSONValue json, AggregateRegistry aggregateRegistry) throws ParserException;

    @NotNull
    public abstract JSONValue responseBodyToJson(O response, AggregateRegistry aggregateRegistry);

    public String toString() {
        return code;
    }
}