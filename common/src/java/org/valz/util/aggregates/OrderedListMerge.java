package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.valz.util.Pair;

import java.util.*;

import static org.valz.util.json.JSONBuilder.makeJson;

public class OrderedListMerge extends AbstractAggregate<JSONArray> {

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

    public static OrderedListMerge deserialize(Object object, AggregateRegistry registry) {
        return new OrderedListMerge((String) ((JSONObject) object).get("key"));
    }



    private final String key;



    public OrderedListMerge(final String key) {
        this.key = key;
    }



    @Override
    public Object toSerialized() {
        return makeJson("key", key);
    }

    @Override
    public JSONArray reduce(@NotNull Iterator<JSONArray> stream) {
        List<Iterator<JSONObject>> iters = new ArrayList<Iterator<JSONObject>>();

        while (stream.hasNext()) {
            iters.add(stream.next().iterator());
        }

        Iterator<JSONObject> resIter = reduce(new Comparator<JSONObject>() {
            public int compare(JSONObject o1, JSONObject o2) {
                Comparable v1 = (Comparable) o1.get(key);
                Comparable v2 = (Comparable) o2.get(key);
                return v1.compareTo(v2);
            }
        }, iters.iterator());

        JSONArray res = new JSONArray();
        while (resIter.hasNext()) {
            res.add(resIter.next());
        }
        return res;
    }

    @Override
    public JSONArray reduce(JSONArray item1, JSONArray item2) {
        List<JSONArray> iters = new ArrayList<JSONArray>();
        iters.add(item1);
        iters.add(item2);
        return reduce(iters.iterator());
    }
}
