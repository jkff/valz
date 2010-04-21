package flexjson.forPrivateFields;

public class Bar<T> {
    private T x;
    public T y;


    private Bar<? extends T> bar;

    public Bar() {

    }

    public Bar(T x) {
        this.x = x;
    }


    public void setBar(Bar<? extends T> bar) {
        this.bar = bar;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        Bar other = (Bar)o;
        return (x == null ? other.x == null : x.equals(other.x)) &&
                (y == null ? other.y == null : y.equals(other.y)) &&
                (bar == null ? other.bar == null : bar.equals(other.bar));
    }
}