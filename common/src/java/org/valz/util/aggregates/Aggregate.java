package org.valz.util.aggregates;

import com.sdicons.json.model.JSONValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @Nullable
    T reduce(@NotNull Iterator<T> stream);

    @Nullable
    T reduce(T item1, T item2);

    JSONValue dataToJson(T item);

    T parseData(JSONValue json) throws ParserException;

    String getName();

    JSONValue configToJson();
}
