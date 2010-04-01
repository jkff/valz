package org.valz.util.protocol.messages;

import org.valz.util.protocol.RequestType;

public class RequestMessage {
    public RequestType type;
    public Object data;

    public RequestMessage() {
    }

    public RequestMessage(RequestType type, Object data) {
        this.type = type;
        this.data = data;
    }
}
