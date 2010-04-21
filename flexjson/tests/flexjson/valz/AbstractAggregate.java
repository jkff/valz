package flexjson.valz;

import java.util.Iterator;

public abstract class AbstractAggregate<T> implements Aggregate<T> {
    public T reduce(Iterator<T> stream) {
        T res = stream.next();
        while (stream.hasNext()) {
            res = reduce(res, stream.next());
        }
        return res;
    }

    public abstract T reduce(T item1, T item2);


    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return 0;
    }
}
