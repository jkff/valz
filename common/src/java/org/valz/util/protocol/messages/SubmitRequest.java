package org.valz.util.protocol.messages;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.protocol.messages.Message;
import org.valz.util.protocol.MessageType;

import static org.valz.util.json.JSONBuilder.makeJson;

public class SubmitRequest extends Message<SubmitRequest.Submission> {
    public static SubmitRequest fromDataJson(Object json) throws ParseException {
        JSONObject dataObject = (JSONObject)json;
        return new SubmitRequest(
                (String)dataObject.get("name"),
                AggregateRegistry.INSTANCE.parseAggregateString(((JSONObject) dataObject.get("aggregate"))),
                dataObject.get("value"));
    }

    public SubmitRequest(String name, Aggregate<?> aggregate, Object value) {
        super(new Submission(name, aggregate, value), MessageType.SUBMIT_REQUEST);
    }

    public JSONObject dataToJson() {
        return makeJson(
                "name", getData().name,
                "aggregate", AggregateRegistry.toJson(getData().aggregate),
                "value", getData().value);
    }

    public static class Submission {
        public final String name;
        public final Aggregate<?> aggregate;
        public final Object value;

        public Submission(String name, Aggregate<?> aggregate, Object value) {
            this.name = name;
            this.aggregate = aggregate;
            this.value = value;
        }
    }
}
