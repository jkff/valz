package org.valz.util.protocol.messages;

import org.valz.util.protocol.RequestType;

public class RequestMessage {
    private RequestType type;
    private Object data;



    public RequestMessage() {
    }

    public RequestMessage(RequestType type, Object data) {
        this.type = type;
        this.data = data;
    }

    

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
