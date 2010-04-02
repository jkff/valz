package flexjson.forInterface;

public class WithObjectField {
    private Object obj;



    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WithObjectField other = (WithObjectField)o;
        return obj == null ? other.obj == null : obj.equals(other.obj);
    }

    public int hashCode() {
        return obj == null ? 0 : obj.hashCode();
    }
}
