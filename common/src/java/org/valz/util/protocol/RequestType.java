package org.valz.util.protocol;

public enum RequestType {
    GET_VALUE(ResponseType.GET_VALUE),
    GET_AGGREGATE(ResponseType.GET_AGGREGATE),
    LIST_VARS(ResponseType.LIST_VARS),
    SUBMIT(ResponseType.SUBMIT);

    public ResponseType getResponseType() {
        return responseType;
    }

    private final ResponseType responseType;

    private RequestType(ResponseType responseType) {

        this.responseType = responseType;
    }
}
