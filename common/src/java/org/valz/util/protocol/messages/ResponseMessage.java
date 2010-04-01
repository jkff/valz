package org.valz.util.protocol.messages;

import org.valz.util.protocol.ResponseType;

public class ResponseMessage {
    public ResponseType type;
    public Object data;

    public ResponseMessage() {
    }

    public ResponseMessage(ResponseType type, Object data) {
        this.type = type;
        this.data = data;
    }
}