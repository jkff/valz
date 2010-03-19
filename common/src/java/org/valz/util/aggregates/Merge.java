package org.valz.util.aggregates;

import org.valz.util.Pair;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class Merge<T> implements Aggregate<Iterator<T>> {
    private Comparator<? super T> comparator;

    public Merge(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    public Iterator<T> reduce(Iterator<Iterator<T>> stream) {
        final PriorityQueue<Pair<T,Iterator<T>>> q = new PriorityQueue<Pair<T, Iterator<T>>>(
                0, new Comparator<Pair<T, Iterator<T>>>() {
                    public int compare(Pair<T, Iterator<T>> p1, Pair<T, Iterator<T>> p2) {
                        return comparator.compare(p1.first, p2.first);
                    }
                }
        );

        while(stream.hasNext()) {
            Iterator<T> i = stream.next();
            if(i.hasNext()) q.offer(new Pair<T, Iterator<T>>(i.next(), i));
        }

        return new Iterator<T>() {
            public boolean hasNext() {
                return !q.isEmpty();
            }

            public T next() {
                Pair<T, Iterator<T>> p = q.remove();
                T res = p.first;
                if(p.second.hasNext()) {
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
}
