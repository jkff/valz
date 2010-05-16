package org.valz.util.protocol.messages;

import com.sdicons.json.model.*;
import org.jetbrains.annotations.NotNull;
import org.valz.util.Pair;
import org.valz.util.aggregates.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.valz.util.JsonUtils.makeJson;

public abstract class InteractionType<I, O> {
    private static final Map<String, InteractionType<?, ?>> ALL_TYPES =
            new HashMap<String, InteractionType<?, ?>>();

    public static Pair<InteractionType, Object> requestFromJson(JSONValue request,
                                                                AggregateRegistry registry) throws
            ParserException {
        JSONObject jsonObject = (JSONObject)request;
        Map<String, JSONValue> map = jsonObject.getValue();
        String strType = ((JSONString)map.get("type")).getValue();
        JSONValue jsonData = map.get("data");
        InteractionType<?, ?> type = InteractionType.getByCode(strType);
        return new Pair<InteractionType, Object>(type, type.requestBodyFromJson(jsonData, registry));
    }

    public static Pair<InteractionType, Object> responseFromJson(JSONValue response,
                                                                 AggregateRegistry registry) throws
            ParserException {
        JSONObject jsonObject = (JSONObject)response;
        Map<String, JSONValue> map = jsonObject.getValue();
        String strType = ((JSONString)map.get("type")).getValue();
        JSONValue jsonData = map.get("data");
        InteractionType<?, ?> type = InteractionType.getByCode(strType);
        return new Pair<InteractionType, Object>(type, type.responseBodyFromJson(jsonData, registry));
    }

    public static <I> JSONValue requestToJson(InteractionType<I, ?> type, I request,
                                              AggregateRegistry registry) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.getValue().put("type", new JSONString(type.code));
        jsonObject.getValue().put("data", type.requestBodyToJson(request, registry));
        return jsonObject;
    }

    public static <O> JSONValue responseToJson(InteractionType<?, O> type, O response,
                                               AggregateRegistry registry) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.getValue().put("type", new JSONString(type.code));
        jsonObject.getValue().put("data", type.responseBodyToJson(response, registry));
        return jsonObject;
    }


    public static final InteractionType<String, Value<?>> GET_VALUE =
            new InteractionType<String, Value<?>>("GET_VALUE") {
                public String requestBodyFromJson(JSONValue json, AggregateRegistry registry) {
                    return ((JSONString)json).getValue();
                }

                @NotNull
                public JSONValue requestBodyToJson(String request, AggregateRegistry registry) {
                    return new JSONString(request);
                }

                public Value<?> responseBodyFromJson(JSONValue json, AggregateRegistry registry) throws
                        ParserException {
                    return Value.fromJson(registry, json);
                }

                @NotNull
                public JSONValue responseBodyToJson(Value<?> response, AggregateRegistry registry) {
                    return response.toJson(registry);
                }
            };


    public static final InteractionType<String, Aggregate<?>> GET_AGGREGATE =
            new InteractionType<String, Aggregate<?>>("GET_AGGREGATE") {
                public String requestBodyFromJson(JSONValue json, AggregateRegistry registry) {
                    return ((JSONString)json).getValue();
                }

                @NotNull
                public JSONValue requestBodyToJson(String request, AggregateRegistry registry) {
                    return new JSONString(request);
                }

                public Aggregate<?> responseBodyFromJson(JSONValue json, AggregateRegistry registry) throws
                        ParserException {
                    return AggregateFormatter.fromJson(registry, json);
                }

                @NotNull
                public JSONValue responseBodyToJson(Aggregate<?> response, AggregateRegistry registry) {
                    return AggregateFormatter.toJson(registry, response);
                }
            };

    public static final InteractionType<Void, Collection<String>> LIST_VARS =
            new InteractionType<Void, Collection<String>>("LIST_VARS") {
                public Void requestBodyFromJson(JSONValue json, AggregateRegistry registry) {
                    return null;
                }

                @NotNull
                public JSONValue requestBodyToJson(Void request, AggregateRegistry registry) {
                    return new JSONNull();
                }

                public Collection<String> responseBodyFromJson(JSONValue json, AggregateRegistry registry) {
                    Collection<String> list = new ArrayList<String>();
                    for (JSONValue item : ((JSONArray)json).getValue()) {
                        list.add(((JSONString)item).getValue());
                    }
                    return list;
                }

                @NotNull
                public JSONValue responseBodyToJson(Collection<String> response, AggregateRegistry registry) {
                    JSONArray json = new JSONArray();
                    for (String item : response) {
                        json.getValue().add(new JSONString(item));
                    }
                    return json;
                }
            };

    public static final InteractionType<String, Void> REMOVE_VALUE =
            new InteractionType<String, Void>("REMOVE_VALUE") {
                public String requestBodyFromJson(JSONValue json, AggregateRegistry registry) {
                    return ((JSONString)json).getValue();
                }

                @NotNull
                public JSONValue requestBodyToJson(String request, AggregateRegistry registry) {
                    return new JSONString(request);
                }

                public Void responseBodyFromJson(JSONValue json, AggregateRegistry registry) {
                    return null;
                }

                @NotNull
                public JSONValue responseBodyToJson(Void response, AggregateRegistry registry) {
                    return null;
                }
            };

    public static final InteractionType<SubmitRequest, Void> SUBMIT =
            new InteractionType<SubmitRequest, Void>("SUBMIT") {
                public SubmitRequest requestBodyFromJson(JSONValue json, AggregateRegistry registry) throws
                        ParserException {
                    return SubmitRequest.fromJson(registry, json);
                }

                @NotNull
                public JSONValue requestBodyToJson(SubmitRequest request, AggregateRegistry registry) {
                    return request.toJson(registry);
                }

                public Void responseBodyFromJson(JSONValue json, AggregateRegistry registry) {
                    return null;
                }

                @NotNull
                public JSONValue responseBodyToJson(Void response, AggregateRegistry registry) {
                    return new JSONNull();
                }
            };



    public static final InteractionType<GetBigMapChunkRequest, BigMapChunkValue<?>> GET_BIG_MAP_CHUNK =
            new InteractionType<GetBigMapChunkRequest, BigMapChunkValue<?>>("GET_BIG_MAP_CHUNK") {
                public GetBigMapChunkRequest requestBodyFromJson(JSONValue json, AggregateRegistry registry) {
                    JSONObject jsonObject = (JSONObject)json;
                    Map<String, JSONValue> jsonMap = jsonObject.getValue();
                    return new GetBigMapChunkRequest(
                            ((JSONString)jsonMap.get("name")).getValue(),
                            ((JSONString)jsonMap.get("fromKey")).getValue(),
                            ((JSONInteger)jsonMap.get("count")).getValue().intValue());
                }

                @NotNull
                public JSONValue requestBodyToJson(GetBigMapChunkRequest request, AggregateRegistry registry) {
                    return makeJson(
                            "name", new JSONString(request.name),
                            "fromKey", new JSONString(request.fromKey),
                            "count", new JSONInteger(new BigInteger(request.count+"")));
                }

                public BigMapChunkValue<?> responseBodyFromJson(JSONValue json, AggregateRegistry registry) throws
                        ParserException {
                    return BigMapChunkValue.fromJson(registry, json);
                }

                @NotNull
                public JSONValue responseBodyToJson(BigMapChunkValue<?> response, AggregateRegistry registry) {
                    return response.toJson(registry);
                }
            };

    public static final InteractionType<RemoveBigMapChunkRequest, Void> REMOVE_BIG_MAP_CHUNK =
            new InteractionType<RemoveBigMapChunkRequest, Void>("REMOVE_BIG_MAP_CHUNK") {
                public RemoveBigMapChunkRequest requestBodyFromJson(JSONValue json, AggregateRegistry registry) {
                    JSONObject jsonObject = (JSONObject)json;
                    Map<String, JSONValue> jsonMap = jsonObject.getValue();
                    Collection<String> list = new ArrayList<String>();
                    for (JSONValue item : ((JSONArray)jsonMap.get("keys")).getValue()) {
                        list.add(((JSONString)item).getValue());
                    }

                    return new RemoveBigMapChunkRequest(
                            ((JSONString)jsonMap.get("name")).getValue(),
                            list);
                }

                @NotNull
                public JSONValue requestBodyToJson(RemoveBigMapChunkRequest request, AggregateRegistry registry) {
                    JSONArray jsonArray = new JSONArray();
                    for (String item : request.keys) {
                        jsonArray.getValue().add(new JSONString(item));
                    }
                    return makeJson(
                            "name", new JSONString(request.name),
                            "keys", jsonArray);
                }

                public Void responseBodyFromJson(JSONValue json, AggregateRegistry registry) throws
                        ParserException {
                    return null;
                }

                @NotNull
                public JSONValue responseBodyToJson(Void response, AggregateRegistry registry) {
                    return new JSONNull();
                }
            };

    public static final InteractionType<Void, Collection<String>> LIST_BIG_MAPS =
            new InteractionType<Void, Collection<String>>("LIST_BIG_MAPS") {
                public Void requestBodyFromJson(JSONValue json, AggregateRegistry registry) {
                    return null;
                }

                @NotNull
                public JSONValue requestBodyToJson(Void request, AggregateRegistry registry) {
                    return new JSONNull();
                }

                public Collection<String> responseBodyFromJson(JSONValue json, AggregateRegistry registry) {
                    Collection<String> list = new ArrayList<String>();
                    for (JSONValue item : ((JSONArray)json).getValue()) {
                        list.add(((JSONString)item).getValue());
                    }
                    return list;
                }

                @NotNull
                public JSONValue responseBodyToJson(Collection<String> response, AggregateRegistry registry) {
                    JSONArray json = new JSONArray();
                    for (String item : response) {
                        json.getValue().add(new JSONString(item));
                    }
                    return json;
                }
            };

    public static final InteractionType<String, Void> REMOVE_BIG_MAP =
            new InteractionType<String, Void>("REMOVE_BIG_MAP") {
                public String requestBodyFromJson(JSONValue json, AggregateRegistry registry) {
                    return ((JSONString)json).getValue();
                }

                @NotNull
                public JSONValue requestBodyToJson(String request, AggregateRegistry registry) {
                    return new JSONString(request);
                }

                public Void responseBodyFromJson(JSONValue json, AggregateRegistry registry) {
                    return null;
                }

                @NotNull
                public JSONValue responseBodyToJson(Void response, AggregateRegistry registry) {
                    return null;
                }
            };

    public static final InteractionType<SubmitBigMapRequest, Void> SUBMIT_BIG_MAP =
            new InteractionType<SubmitBigMapRequest, Void>("SUBMIT_BIG_MAP") {
                public SubmitBigMapRequest requestBodyFromJson(JSONValue json,
                                                               AggregateRegistry registry) throws
                        ParserException {
                    return SubmitBigMapRequest.fromJson(registry, json);
                }

                @NotNull
                public JSONValue requestBodyToJson(SubmitBigMapRequest request, AggregateRegistry registry) {
                    return request.toJson(registry);
                }

                public Void responseBodyFromJson(JSONValue json, AggregateRegistry registry) {
                    return null;
                }

                @NotNull
                public JSONValue responseBodyToJson(Void response, AggregateRegistry registry) {
                    return new JSONNull();
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

    public abstract I requestBodyFromJson(JSONValue json, AggregateRegistry registry) throws ParserException;

    @NotNull
    public abstract JSONValue requestBodyToJson(I request, AggregateRegistry registry);

    public abstract O responseBodyFromJson(JSONValue json, AggregateRegistry registry) throws ParserException;

    @NotNull
    public abstract JSONValue responseBodyToJson(O response, AggregateRegistry registry);

    public String toString() {
        return code;
    }
}