package org.valz.util.protocol.messages;

import org.valz.util.aggregates.Aggregate;

public class SubmitRequest<T> {

    public String name;
    public Aggregate<T> aggregate;
    public T value;


    public void setName(String name) {
        this.name = name;
    }

    public void setAggregate(Aggregate<T> aggregate) {
        this.aggregate = aggregate;
    }

    public void setValue(T value) {
        this.value = value;
    }


    public SubmitRequest() {
    }

    public SubmitRequest(String name, Aggregate<T> aggregate, T value) {
        this.name = name;
        this.aggregate = aggregate;
        this.value = value;
    }
}
