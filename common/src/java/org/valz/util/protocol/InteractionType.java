package org.valz.util.protocol;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.messages.SubmitRequest;

import java.util.Collection;

public class InteractionType<I,O> {
    public static final InteractionType<String, Object> GET_VALUE = create("GET_VALUE");
    public static final InteractionType<String, Aggregate<?>> GET_AGGREGATE = create("GET_AGGREGATE");
    public static final InteractionType<Void, Collection<String>> LIST_VARS = create("LIST_VARS");
    public static final InteractionType<SubmitRequest, Void> SUBMIT = create("SUBMIT");


    
    private String code;



    private InteractionType() { }

    private InteractionType(String code) { this.code = code; }



    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InteractionType that = (InteractionType) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    private static <I,O> InteractionType<I,O> create(String code) {
        return new InteractionType<I,O>(code);
    }
}