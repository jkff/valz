package org.valz.util.backends;

import org.valz.util.Pair;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.BigMapIterator;
import org.valz.util.aggregates.Value;
import org.valz.util.keytypes.KeyType;
import org.valz.util.keytypes.KeyTypeRegistry;
import org.valz.util.protocol.messages.InteractionType;
import org.valz.util.protocol.messages.ResponseParser;

import java.util.*;

public class RemoteReadBackend implements ReadBackend {
    private final List<ResponseParser> responseParsers = new ArrayList<ResponseParser>();
    private final int chunkSize;

    public RemoteReadBackend(List<String> readServerUrls, KeyTypeRegistry keyTypeRegistry,
                             AggregateRegistry aggregateRegistry, int chunkSize) {
        this.chunkSize = chunkSize;
        for (String url : readServerUrls) {
            responseParsers.add(new ResponseParser(url, keyTypeRegistry, aggregateRegistry));
        }
    }

    public Value getValue(String name) throws RemoteReadException {
        Value prevValue = null;
        for (ResponseParser parser : responseParsers) {
            Value<?> value = parser.getReadDataResponse(InteractionType.GET_VALUE, name);
            if (prevValue == null) {
                prevValue = value;
            } else {
                checkEquals(value.getAggregate(), prevValue.getAggregate(), name, parser);
                prevValue = new Value(prevValue.getAggregate(),
                        prevValue.getAggregate().reduce(prevValue.getValue(), value.getValue()));
            }
        }
        return prevValue;
    }

    private <T> void checkEquals(T obj1, T obj2, String name, ResponseParser parser) throws
            RemoteReadException {
        if (!obj1.equals(obj2)) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%s", responseParsers.get(0).getUrl()));
            for (int j = 1; j < responseParsers.size() && responseParsers.get(j) != parser; j++) {
                sb.append(String.format(", %s", responseParsers.get(j).getUrl()));
            }
            throw new RemoteReadException(String.format(
                    "Server %s contains aggregate %s with description, differs from servers: %s.",
                    parser.getUrl(), name, sb.toString()));
        }
    }

    public Collection<String> listVars() throws RemoteReadException {
        Set<String> set = new HashSet<String>();
        for (ResponseParser responseParser : responseParsers) {
            Collection<String> collection =
                    responseParser.getReadDataResponse(InteractionType.LIST_VARS, null);
            set.addAll(collection);
        }
        return set;
    }

    public void removeAggregate(String name) throws RemoteReadException {
        for (ResponseParser responseParser : responseParsers) {
            responseParser.getReadDataResponse(InteractionType.REMOVE_VALUE, name);
        }
    }

    public <K, T> BigMapIterator<K, T> getBigMapIterator(String name) throws RemoteReadException {

        class Container {
            public KeyType<K> keyType = null;
        }

        final Container container = new Container();
        Aggregate<T> aggregate = null;

        final Comparator<Pair<Map.Entry<K, T>, BigMapIterator<K, T>>> comparator =
                new Comparator<Pair<Map.Entry<K, T>, BigMapIterator<K, T>>>() {
                    public int compare(Pair<Map.Entry<K, T>, BigMapIterator<K, T>> p1,
                                       Pair<Map.Entry<K, T>, BigMapIterator<K, T>> p2) {
                        return container.keyType.compare(p1 == null ? null : p1.first.getKey(),
                                p2 == null ? null : p2.first.getKey());
                    }
                };

        final PriorityQueue<Pair<Map.Entry<K, T>, BigMapIterator<K, T>>> queue =
                new PriorityQueue<Pair<Map.Entry<K, T>, BigMapIterator<K, T>>>(1, comparator);

        for (ResponseParser parser : responseParsers) {
            BigMapIterator<K, T> iter = new RemoteBigMapIterator<K, T>(parser, name, chunkSize);
            if (iter.hasNext()) {
                queue.offer(new Pair<Map.Entry<K, T>, BigMapIterator<K, T>>(iter.next(), iter));
            }
            if (aggregate == null) {
                container.keyType = iter.getKeyType();
                aggregate = iter.getAggregate();
            } else {
                checkEquals(aggregate, iter.getAggregate(), name, parser);
                checkEquals(container.keyType, iter.getKeyType(), name, parser);
            }
        }
        final Aggregate<T> finalAggregate = aggregate;

        return new BigMapIterator<K, T>() {
            public boolean hasNext() {
                return !queue.isEmpty();
            }

            public Map.Entry<K, T> next() {
                Pair<Map.Entry<K, T>, BigMapIterator<K, T>> curPair = null;
                Pair<Map.Entry<K, T>, BigMapIterator<K, T>> pair = null;
                Map.Entry<K, T> res = null;

                while (!queue.isEmpty()) {
                    curPair = queue.peek();
                    if (pair == null) {
                        pair = queue.remove();
                        res = curPair.first;
                    } else {
                        if (0 != comparator.compare(pair, curPair)) {
                            break;
                        }
                        curPair = queue.remove();
                        res.setValue(finalAggregate.reduce(res.getValue(), curPair.first.getValue()));
                    }
                    if (curPair.second.hasNext()) {
                        Map.Entry<K, T> next = curPair.second.next();
                        queue.offer(new Pair<Map.Entry<K, T>, BigMapIterator<K, T>>(next, curPair.second));
                    }
                }
                return res;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public KeyType<K> getKeyType() {
                return container.keyType;
            }

            public Aggregate<T> getAggregate() {
                return finalAggregate;
            }
        };
    }

    public Collection<String> listBigMaps() throws RemoteReadException {
        Set<String> set = new HashSet<String>();
        for (ResponseParser responseParser : responseParsers) {
            Collection<String> collection =
                    responseParser.getReadDataResponse(InteractionType.LIST_BIG_MAPS, null);
            set.addAll(collection);
        }
        return set;
    }

    public void removeBigMap(String name) throws RemoteReadException {
        for (ResponseParser responseParser : responseParsers) {
            responseParser.getReadDataResponse(InteractionType.REMOVE_BIG_MAP, name);
        }
    }


}
