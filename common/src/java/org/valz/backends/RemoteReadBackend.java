package org.valz.backends;

import org.valz.aggregates.AggregateRegistry;
import org.valz.aggregates.Sample;
import org.valz.keytypes.KeyTypeRegistry;
import org.valz.protocol.ResponseParser;
import org.valz.protocol.messages.BigMapChunkValue;
import org.valz.protocol.messages.InteractionType;

import java.util.*;

public class RemoteReadBackend implements ReadBackend {
    private final List<ResponseParser> responseParsers = new ArrayList<ResponseParser>();

    public RemoteReadBackend(List<String> readServerUrls, KeyTypeRegistry keyTypeRegistry,
                             AggregateRegistry aggregateRegistry) {
        for (String url : readServerUrls) {
            responseParsers.add(new ResponseParser(url, keyTypeRegistry, aggregateRegistry));
        }
    }

    public Sample getValue(String name) throws RemoteReadException {
        Sample prevSample = null;
        for (ResponseParser parser : responseParsers) {
            Sample<?> sample = parser.getReadDataResponse(InteractionType.GET_VALUE, name);
            if (prevSample == null) {
                prevSample = sample;
            } else {
                checkEquals(sample.getAggregate(), prevSample.getAggregate(), name, parser);
                prevSample = new Sample(prevSample.getAggregate(),
                        prevSample.getAggregate().reduce(prevSample.getValue(), sample.getValue()));
            }
        }
        return prevSample;
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

    public void removeAggregate(String name) throws RemoteWriteException {
        for (ResponseParser responseParser : responseParsers) {
            responseParser.getWriteDataResponse(InteractionType.REMOVE_VALUE, name);
        }
    }

    public <K, T> BigMapChunkValue<K, T> getBigMapChunk(String name, K fromKey, int count) throws RemoteReadException {
        // Naive implementation. TODO: Provide some caching or refactor to "getBigMapIterator(name,fromKey)"
        // where BigMapIterator {Chunk next(count);}

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
}
