package flexjson.forInterface;

public class MyContainer {
    public void set_interface(MyInterface _interface) {
        this._interface = _interface;
    }

    public MyInterface _interface = null;

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MyContainer other = (MyContainer)o;
        return _interface == null ? other._interface == null : _interface.equals(other._interface);
    }

    public int hashCode() {
        return _interface == null ? 0 : _interface.hashCode();
    }
}
