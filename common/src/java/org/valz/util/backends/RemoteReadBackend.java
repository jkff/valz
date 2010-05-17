package org.valz.util.backends;

import org.valz.util.Pair;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.BigMapIterator;
import org.valz.util.aggregates.Value;
import org.valz.util.protocol.messages.BigMapChunkValue;
import org.valz.util.protocol.messages.InteractionType;
import org.valz.util.protocol.messages.ResponseParser;

import java.util.*;

public class RemoteReadBackend implements ReadBackend {
    private final List<ResponseParser> responseParsers = new ArrayList<ResponseParser>();
    private final int chunkSize;

    public RemoteReadBackend(List<String> readServerUrls, AggregateRegistry registry, int chunkSize) {
        this.chunkSize = chunkSize;
        for (String url : readServerUrls) {
            responseParsers.add(new ResponseParser(url, registry));
        }
    }

    public Value getValue(String name) throws RemoteReadException {
        Value prevValue = null;
        for (ResponseParser parser : responseParsers) {
            Value<?> value = parser.getReadDataResponse(InteractionType.GET_VALUE, name);
            if (prevValue == null) {
                prevValue = value;
            } else {
                checkAggregates(value.getAggregate(), prevValue.getAggregate(), name, parser);
                prevValue = new Value(prevValue.getAggregate(),
                    prevValue.getAggregate().reduce(prevValue.getValue(), value.getValue()));
            }
        }
        return prevValue;
    }

    private void checkAggregates(Aggregate agg1, Aggregate agg2, String name, ResponseParser parser) throws
            RemoteReadException {
        if (!agg1.equals(agg2)) {
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

    public <T> BigMapIterator<T> getBigMapIterator(String name) throws RemoteReadException {
        final PriorityQueue<Pair<Map.Entry<String, T>, BigMapIterator<T>>> queue =
                new PriorityQueue<Pair<Map.Entry<String, T>, BigMapIterator<T>>>(0,
                        new Comparator<Pair<Map.Entry<String, T>, BigMapIterator<T>>>() {
                            public int compare(Pair<Map.Entry<String, T>, BigMapIterator<T>> p1,
                                               Pair<Map.Entry<String, T>, BigMapIterator<T>> p2) {
                                return p1.first.getKey().compareTo(p2.first.getKey());
                            }
                        });

        Aggregate<T> aggregate = null;
        for (ResponseParser parser : responseParsers) {
            BigMapIterator<T> iter = new RemoteBigMapIterator<T>(parser, name, chunkSize);
            if (iter.hasNext()) {
                queue.offer(new Pair<Map.Entry<String, T>, BigMapIterator<T>>(iter.next(), iter));
            }
            if (aggregate == null) {
                aggregate = iter.getAggregate();
            } else {
                checkAggregates(aggregate, iter.getAggregate(), name, parser);
            }
        }
        final Aggregate<T> finalAggregate = aggregate;

        return new BigMapIterator<T>() {
            public boolean hasNext() {
                return !queue.isEmpty();
            }

            public Map.Entry<String, T> next() {
                Pair<Map.Entry<String, T>, BigMapIterator<T>> p = queue.remove();
                Map.Entry<String, T> res = p.first;
                if (p.second.hasNext()) {
                    Map.Entry<String, T> next = p.second.next();
                    queue.offer(new Pair<Map.Entry<String, T>, BigMapIterator<T>>(next, p.second));
                }
                return res;
            }

            public void remove() {
                throw new UnsupportedOperationException();
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
