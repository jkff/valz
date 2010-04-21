package flexjson.forInterface;

public class MyClass implements MyInterface {
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MyClass other = (MyClass)o;
        return true;
    }

    public int hashCode() {
        return 0;
    }
}
