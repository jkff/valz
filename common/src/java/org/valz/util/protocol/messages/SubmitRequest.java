package org.valz.util.protocol.messages;

import flexjson.JSON;
import org.valz.util.aggregates.Aggregate;

public class SubmitRequest {

    public String name;
    public Aggregate<?> aggregate;
    public Object value;


    public void setName(String name) {
        this.name = name;
    }

    public void setAggregate(Aggregate<?> aggregate) {
        this.aggregate = aggregate;
    }

    public void setValue(Object value) {
        this.value = value;
    }


    public SubmitRequest() {
    }

    public SubmitRequest(String name, Aggregate<?> aggregate, Object value) {
        this.name = name;
        this.aggregate = aggregate;
        this.value = value;
    }
}
