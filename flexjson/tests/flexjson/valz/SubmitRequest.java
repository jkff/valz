package flexjson.valz;

public class SubmitRequest<T> {

    public String name;
    public Aggregate<T> aggregate;
    public T value;


    public void setName(String name) {
        this.name = name;
    }

    public void setAggregate(Aggregate<T> aggregate) {
        this.aggregate = aggregate;
    }

    public void setValue(T value) {
        this.value = value;
    }


    public SubmitRequest() {
    }

    public SubmitRequest(String name, Aggregate<T> aggregate, T value) {
        this.name = name;
        this.aggregate = aggregate;
        this.value = value;
    }


    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubmitRequest<T> other = (SubmitRequest<T>)o;
        return name == null ? other.name == null : name.equals(other.name) &&
                aggregate == null ? other.aggregate == null : aggregate.equals(other.aggregate) &&
                value == null ? other.value == null : value.equals(other.value);
    }

    public int hashCode() {
        return (name == null ? 0 : name.hashCode()) ^
                (aggregate == null ? 0 : aggregate.hashCode()) ^
                (value == null ? 0 : value.hashCode());
    }
}