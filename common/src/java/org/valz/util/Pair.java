package org.valz.util;

public class Pair<A, B> {
    public final A first;
    public final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode()) ^
                (second == null ? 0 : second.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair<A, B> that = (Pair<A, B>)o;
        if (this.first == null ?  that.first != null : !this.first.equals(that.first)) return false;
        if (this.second == null ?  that.second != null : !this.second.equals(that.second)) return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("[%s, %s]", first, second);
    }
}
