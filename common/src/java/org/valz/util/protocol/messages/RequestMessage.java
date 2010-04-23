package org.valz.util.protocol.messages;

import org.valz.util.protocol.InteractionType;

public class RequestMessage<T> {
    private InteractionType<T,?> type = null;
    private T data = null;

    public RequestMessage() {
    }

    public RequestMessage(InteractionType<T, ?> type, T data) {
        this.type = type;
        this.data = data;
    }

    public InteractionType getType() {
        return type;
    }

    public T getData() {
        return data;
    }
}
