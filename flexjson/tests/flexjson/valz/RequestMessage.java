package flexjson.valz;

import flexjson.valz.RequestType;

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


    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestMessage other = (RequestMessage)o;
        return type == null ? other.type == null : type.equals(other.type) &&
                data == null ? other.data == null : data.equals(other.data);
    }

    public int hashCode() {
        return (type == null ? 0 : type.hashCode()) ^ 
                (data == null ? 0 : data.hashCode());
    }
}