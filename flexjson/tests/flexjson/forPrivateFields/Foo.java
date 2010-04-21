package flexjson.forPrivateFields;

public class Foo {
    private int x;
    public int y;

    public Foo() {

    }

    public Foo(int x) {
        this.x = x;
    }

    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        Foo other = (Foo)o;
        return this.x == other.x &&
                this.y == other.y;
    }
}
