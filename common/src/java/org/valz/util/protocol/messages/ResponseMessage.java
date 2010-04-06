package org.valz.util.protocol.messages;

import org.valz.util.protocol.InteractionType;

public class ResponseMessage<T> {
    public InteractionType<?,T> type;
    public T data;

    public ResponseMessage() {
    }

    public ResponseMessage(InteractionType type, T data) {
        this.type = type;
        this.data = data;
    }
}