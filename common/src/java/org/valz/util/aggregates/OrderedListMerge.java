package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.valz.util.Pair;

import java.util.*;

public class OrderedListMerge<T> extends AbstractAggregate<List<T>> {

    private static <T> Iterator<T> reduce(final Comparator<T> comparator, Iterator<Iterator<T>> stream) {
        final PriorityQueue<Pair<T, Iterator<T>>> q = new PriorityQueue<Pair<T, Iterator<T>>>(
                0, new Comparator<Pair<T, Iterator<T>>>() {
                    public int compare(Pair<T, Iterator<T>> p1, Pair<T, Iterator<T>> p2) {
                        return comparator.compare(p1.first, p2.first);
                    }
                }
        );

        while (stream.hasNext()) {
            Iterator<T> iter = stream.next();
            if (iter.hasNext()) {
                q.offer(new Pair<T, Iterator<T>>(iter.next(), iter));
            }
        }

        return new Iterator<T>() {
            public boolean hasNext() {
                return !q.isEmpty();
            }

            public T next() {
                Pair<T, Iterator<T>> p = q.remove();
                T res = p.first;
                if (p.second.hasNext()) {
                    T next = p.second.next();
                    q.offer(new Pair<T, Iterator<T>>(next, p.second));
                }
                return res;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    // TODO: make private final
    public Comparator<T> comparator;



    public OrderedListMerge(final Comparator<T> comparator) {
        this.comparator = comparator;
    }




    @Override
    public List<T> reduce(@NotNull Iterator<List<T>> stream) {
        List<Iterator<T>> iters = new ArrayList<Iterator<T>>();
        while (stream.hasNext()) {
            iters.add(stream.next().iterator());
        }

        Iterator<T> resIter = reduce(comparator, iters.iterator());

        ArrayList<T> res = new ArrayList<T>();
        while (resIter.hasNext()) {
            res.add(resIter.next());
        }
        return res;
    }

    @Override
    public List<T> reduce(List<T> item1, List<T> item2) {
        List<List<T>> iters = new ArrayList<List<T>>();
        iters.add(item1);
        iters.add(item2);
        return reduce(iters.iterator());
    }
}
