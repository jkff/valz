package flexjson.valz;

import java.util.Iterator;

public interface Aggregate<T> {
    /**
     * The result of this method must be independent on the order of items
     * in 'stream', that is, it must represent a fold in a commutative semigroup.
     * (equivalence of results for different permutations of the same stream
     * may be defined in the application's terms, t.i., it does not have to be
     * equivalence on .equals())
     * <p/>
     * It is guaranteed that the stream will not contain nulls and will not be empty.
     * <p/>
     * Note that we use 'Iterator' instead of 'Iterable', because the stream
     * may fail to provide the ability to traverse it several times.
     */
    T reduce(Iterator<T> stream);

    T reduce(T item1, T item2);
}