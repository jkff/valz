package org.valz.protocol.messages;

import com.sdicons.json.model.*;
import org.jetbrains.annotations.NotNull;
import org.valz.util.Pair;
import org.valz.model.AggregateRegistry;
import org.valz.util.ParserException;
import org.valz.model.Sample;
import org.valz.keytypes.KeyTypeRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class InteractionType<I, O> {
    private static final Map<String, InteractionType<?, ?>> ALL_TYPES =
            new HashMap<String, InteractionType<?, ?>>();

    public static Pair<InteractionType, Object> requestFromJson(JSONValue request,
                                                                KeyTypeRegistry keyTypeRegistry,
                                                                AggregateRegistry aggregateRegistry) throws
            ParserException {
        JSONObject jsonObject = (JSONObject)request;
        Map<String, JSONValue> map = jsonObject.getValue();
        String strType = ((JSONString)map.get("type")).getValue();
        JSONValue jsonData = map.get("data");
        InteractionType<?, ?> type = InteractionType.getByCode(strType);
        return new Pair<InteractionType, Object>(type,
                type.requestBodyFromJson(jsonData, keyTypeRegistry, aggregateRegistry));
    }

    public static Pair<InteractionType, Object> responseFromJson(JSONValue response,
                                                                 KeyTypeRegistry keyTypeRegistry,
                                                                 AggregateRegistry aggregateRegistry) throws
            ParserException {
        JSONObject jsonObject = (JSONObject)response;
        Map<String, JSONValue> map = jsonObject.getValue();
        String strType = ((JSONString)map.get("type")).getValue();
        JSONValue jsonData = map.get("data");
        InteractionType<?, ?> type = InteractionType.getByCode(strType);
        return new Pair<InteractionType, Object>(type,
                type.responseBodyFromJson(jsonData, keyTypeRegistry, aggregateRegistry));
    }

    public static <I> JSONValue requestToJson(InteractionType<I, ?> type, I request,
                                              KeyTypeRegistry keyTypeRegistry,
                                              AggregateRegistry aggregateRegistry) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.getValue().put("type", new JSONString(type.code));
        jsonObject.getValue()
                .put("data", type.requestBodyToJson(request, keyTypeRegistry, aggregateRegistry));
        return jsonObject;
    }

    public static <O> JSONValue responseToJson(InteractionType<?, O> type, O response,
                                               KeyTypeRegistry keyTypeRegistry,
                                               AggregateRegistry aggregateRegistry) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.getValue().put("type", new JSONString(type.code));
        jsonObject.getValue()
                .put("data", type.responseBodyToJson(response, keyTypeRegistry, aggregateRegistry));
        return jsonObject;
    }



    public static final InteractionType<SubmitRequest, Void> SUBMIT =
            new InteractionType<SubmitRequest, Void>("SUBMIT") {
                public SubmitRequest requestBodyFromJson(JSONValue json, KeyTypeRegistry keyTypeRegistry,
                                                         AggregateRegistry aggregateRegistry) throws
                        ParserException {
                    return SubmitRequest.fromJson(aggregateRegistry, json);
                }

                @NotNull
                public JSONValue requestBodyToJson(SubmitRequest request, KeyTypeRegistry keyTypeRegistry,
                                                   AggregateRegistry aggregateRegistry) {
                    return request.toJson(aggregateRegistry);
                }

                public Void responseBodyFromJson(JSONValue json, KeyTypeRegistry keyTypeRegistry,
                                                 AggregateRegistry aggregateRegistry) {
                    return null;
                }

                @NotNull
                public JSONValue responseBodyToJson(Void response, KeyTypeRegistry keyTypeRegistry,
                                                    AggregateRegistry aggregateRegistry) {
                    return new JSONNull();
                }
            };

    public static final InteractionType<String, Sample<?>> GET_VALUE =
            new InteractionType<String, Sample<?>>("GET_VALUE") {
                public String requestBodyFromJson(JSONValue json, KeyTypeRegistry keyTypeRegistry,
                                                  AggregateRegistry aggregateRegistry) {
                    return ((JSONString)json).getValue();
                }

                @NotNull
                public JSONValue requestBodyToJson(String request, KeyTypeRegistry keyTypeRegistry,
                                                   AggregateRegistry aggregateRegistry) {
                    return new JSONString(request);
                }

                public Sample<?> responseBodyFromJson(JSONValue json, KeyTypeRegistry keyTypeRegistry,
                                                     AggregateRegistry aggregateRegistry) throws
                        ParserException {
                    return Sample.fromJson(aggregateRegistry, json);
                }

                @NotNull
                public JSONValue responseBodyToJson(Sample<?> response, KeyTypeRegistry keyTypeRegistry,
                                                    AggregateRegistry aggregateRegistry) {
                    return response.toJson(aggregateRegistry);
                }
            };

    public static final InteractionType<Void, Collection<String>> LIST_VARS =
            new InteractionType<Void, Collection<String>>("LIST_VARS") {
                public Void requestBodyFromJson(JSONValue json, KeyTypeRegistry keyTypeRegistry,
                                                AggregateRegistry aggregateRegistry) {
                    return null;
                }

                @NotNull
                public JSONValue requestBodyToJson(Void request, KeyTypeRegistry keyTypeRegistry,
                                                   AggregateRegistry aggregateRegistry) {
                    return new JSONNull();
                }

                public Collection<String> responseBodyFromJson(JSONValue json,
                                                               KeyTypeRegistry keyTypeRegistry,
                                                               AggregateRegistry aggregateRegistry) {
                    Collection<String> list = new ArrayList<String>();
                    for (JSONValue item : ((JSONArray)json).getValue()) {
                        list.add(((JSONString)item).getValue());
                    }
                    return list;
                }

                @NotNull
                public JSONValue responseBodyToJson(Collection<String> response,
                                                    KeyTypeRegistry keyTypeRegistry,
                                                    AggregateRegistry aggregateRegistry) {
                    JSONArray json = new JSONArray();
                    for (String item : response) {
                        json.getValue().add(new JSONString(item));
                    }
                    return json;
                }
            };

    public static final InteractionType<String, Void> REMOVE_VALUE =
            new InteractionType<String, Void>("REMOVE_VALUE") {
                public String requestBodyFromJson(JSONValue json, KeyTypeRegistry keyTypeRegistry,
                                                  AggregateRegistry aggregateRegistry) {
                    return ((JSONString)json).getValue();
                }

                @NotNull
                public JSONValue requestBodyToJson(String request, KeyTypeRegistry keyTypeRegistry,
                                                   AggregateRegistry aggregateRegistry) {
                    return new JSONString(request);
                }

                public Void responseBodyFromJson(JSONValue json, KeyTypeRegistry keyTypeRegistry,
                                                 AggregateRegistry aggregateRegistry) {
                    return null;
                }

                @NotNull
                public JSONValue responseBodyToJson(Void response, KeyTypeRegistry keyTypeRegistry,
                                                    AggregateRegistry aggregateRegistry) {
                    return null;
                }
            };



    public static final InteractionType<SubmitBigMapRequest, Void> SUBMIT_BIG_MAP =
            new InteractionType<SubmitBigMapRequest, Void>("SUBMIT_BIG_MAP") {
                public SubmitBigMapRequest requestBodyFromJson(JSONValue json,
                                                               KeyTypeRegistry keyTypeRegistry,
                                                               AggregateRegistry aggregateRegistry) throws
                        ParserException {
                    return SubmitBigMapRequest.fromJson(keyTypeRegistry, aggregateRegistry, json);
                }

                @NotNull
                public JSONValue requestBodyToJson(SubmitBigMapRequest request,
                                                   KeyTypeRegistry keyTypeRegistry,
                                                   AggregateRegistry aggregateRegistry) {
                    return request.toJson(keyTypeRegistry, aggregateRegistry);
                }

                public Void responseBodyFromJson(JSONValue json, KeyTypeRegistry keyTypeRegistry,
                                                 AggregateRegistry aggregateRegistry) {
                    return null;
                }

                @NotNull
                public JSONValue responseBodyToJson(Void response, KeyTypeRegistry keyTypeRegistry,
                                                    AggregateRegistry aggregateRegistry) {
                    return new JSONNull();
                }
            };

    public static final InteractionType<GetBigMapChunkRequest, BigMapChunkValue> GET_BIG_MAP_CHUNK =
            new InteractionType<GetBigMapChunkRequest, BigMapChunkValue>("GET_BIG_MAP_CHUNK") {
                public GetBigMapChunkRequest requestBodyFromJson(
                        JSONValue json, KeyTypeRegistry keyTypeRegistry,
                        AggregateRegistry aggregateRegistry)
                        throws ParserException
                {
                    return GetBigMapChunkRequest.fromJson(keyTypeRegistry, json);
                }

                @NotNull
                public JSONValue requestBodyToJson(
                        GetBigMapChunkRequest request, KeyTypeRegistry keyTypeRegistry,
                        AggregateRegistry aggregateRegistry)
                {
                    return request.toJson();
                }

                public BigMapChunkValue responseBodyFromJson(
                        JSONValue json, KeyTypeRegistry keyTypeRegistry,
                        AggregateRegistry aggregateRegistry)
                        throws ParserException
                {
                    return BigMapChunkValue.fromJson(keyTypeRegistry, aggregateRegistry, json);
                }

                @NotNull
                public JSONValue responseBodyToJson(
                        BigMapChunkValue response, KeyTypeRegistry keyTypeRegistry,
                        AggregateRegistry aggregateRegistry)
                {
                    return response.toJson(keyTypeRegistry, aggregateRegistry);
                }
            };

    public static final InteractionType<Void, Collection<String>> LIST_BIG_MAPS =
            new InteractionType<Void, Collection<String>>("LIST_BIG_MAPS") {
                public Void requestBodyFromJson(JSONValue json, KeyTypeRegistry keyTypeRegistry,
                                                AggregateRegistry aggregateRegistry) {
                    return null;
                }

                @NotNull
                public JSONValue requestBodyToJson(Void request, KeyTypeRegistry keyTypeRegistry,
                                                   AggregateRegistry aggregateRegistry) {
                    return new JSONNull();
                }

                public Collection<String> responseBodyFromJson(JSONValue json,
                                                               KeyTypeRegistry keyTypeRegistry,
                                                               AggregateRegistry aggregateRegistry) {
                    Collection<String> list = new ArrayList<String>();
                    for (JSONValue item : ((JSONArray)json).getValue()) {
                        list.add(((JSONString)item).getValue());
                    }
                    return list;
                }

                @NotNull
                public JSONValue responseBodyToJson(Collection<String> response,
                                                    KeyTypeRegistry keyTypeRegistry,
                                                    AggregateRegistry aggregateRegistry) {
                    JSONArray json = new JSONArray();
                    for (String item : response) {
                        json.getValue().add(new JSONString(item));
                    }
                    return json;
                }
            };

    public static final InteractionType<String, Void> REMOVE_BIG_MAP =
            new InteractionType<String, Void>("REMOVE_BIG_MAP") {
                public String requestBodyFromJson(JSONValue json, KeyTypeRegistry keyTypeRegistry,
                                                  AggregateRegistry aggregateRegistry) {
                    return ((JSONString)json).getValue();
                }

                @NotNull
                public JSONValue requestBodyToJson(String request, KeyTypeRegistry keyTypeRegistry,
                                                   AggregateRegistry aggregateRegistry) {
                    return new JSONString(request);
                }

                public Void responseBodyFromJson(JSONValue json, KeyTypeRegistry keyTypeRegistry,
                                                 AggregateRegistry aggregateRegistry) {
                    return null;
                }

                @NotNull
                public JSONValue responseBodyToJson(Void response, KeyTypeRegistry keyTypeRegistry,
                                                    AggregateRegistry aggregateRegistry) {
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

    public abstract I requestBodyFromJson(JSONValue json, KeyTypeRegistry keyTypeRegistry,
                                          AggregateRegistry aggregateRegistry) throws ParserException;

    @NotNull
    public abstract JSONValue requestBodyToJson(I request, KeyTypeRegistry keyTypeRegistry,
                                                AggregateRegistry aggregateRegistry);

    public abstract O responseBodyFromJson(JSONValue json, KeyTypeRegistry keyTypeRegistry,
                                           AggregateRegistry aggregateRegistry) throws ParserException;

    @NotNull
    public abstract JSONValue responseBodyToJson(O response, KeyTypeRegistry keyTypeRegistry,
                                                 AggregateRegistry aggregateRegistry);

    public String toString() {
        return code;
    }
}