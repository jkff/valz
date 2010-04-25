package org.valz.util.protocol.messages;

import org.valz.util.Value;
import org.valz.util.aggregates.Aggregate;

import java.util.*;

public class InteractionType<I,O> {
    public static final InteractionType<String, Value<?>> GET_VALUE = create("GET_VALUE");
    public static final InteractionType<String, Aggregate<?>> GET_AGGREGATE = create("GET_VALUE");
    public static final InteractionType<Void, Collection<String>> LIST_VARS = create("LIST_VARS");
    public static final InteractionType<SubmitRequest, Void> SUBMIT = create("SUBMIT");

    public static final Map<String, InteractionType<?, ?>> ALL_TYPES = new HashMap<String, InteractionType<?, ?>>() {{
        put(InteractionType.GET_VALUE.getCode(), InteractionType.GET_VALUE);
        put(InteractionType.GET_AGGREGATE.getCode(), InteractionType.GET_AGGREGATE);
        put(InteractionType.LIST_VARS.getCode(), InteractionType.LIST_VARS);
        put(InteractionType.SUBMIT.getCode(), InteractionType.SUBMIT);
    }};



    private static <I,O> InteractionType<I,O> create(String code) {
        return new InteractionType<I,O>(code);
    }



    private final String code;
    private InteractionType(String code) { this.code = code; }



    public String getCode() {
        return code;
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

    @Override
    public String toString() {
        return code;
    }
}