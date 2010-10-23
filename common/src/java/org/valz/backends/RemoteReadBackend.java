package org.valz.backends;

import org.valz.model.AggregateRegistry;
import org.valz.model.BigMapIterator;
import org.valz.model.Sample;
import org.valz.keytypes.KeyTypeRegistry;
import org.valz.protocol.messages.InteractionType;

import java.util.*;

public class RemoteReadBackend implements ReadBackend {
    private final List<RemoteConnector> remoteConnectors = new ArrayList<RemoteConnector>();

    public RemoteReadBackend(List<String> readServerUrls, KeyTypeRegistry keyTypeRegistry,
                             AggregateRegistry aggregateRegistry)
    {
        for (String url : readServerUrls) {
            remoteConnectors.add(new RemoteConnector(url, keyTypeRegistry, aggregateRegistry));
        }
    }

    public Sample getValue(String name) throws RemoteReadException {
        Sample prevSample = null;
        for (RemoteConnector parser : remoteConnectors) {
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

    private <T> void checkEquals(T obj1, T obj2, String name, RemoteConnector parser) throws
            RemoteReadException {
        if (!obj1.equals(obj2)) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%s", remoteConnectors.get(0).getUrl()));
            for (int j = 1; j < remoteConnectors.size() && remoteConnectors.get(j) != parser; j++) {
                sb.append(String.format(", %s", remoteConnectors.get(j).getUrl()));
            }
            throw new RemoteReadException(String.format(
                    "Server %s contains aggregate %s with description, differs from servers: %s.",
                    parser.getUrl(), name, sb.toString()));
        }
    }

    public Collection<String> listVars() throws RemoteReadException {
        Set<String> set = new HashSet<String>();
        for (RemoteConnector remoteConnector : remoteConnectors) {
            Collection<String> collection =
                    remoteConnector.getReadDataResponse(InteractionType.LIST_VARS, null);
            set.addAll(collection);
        }
        return set;
    }

    public <K, T> BigMapIterator<K, T> getBigMapIterator(String name, K fromKey) throws RemoteReadException {
        return new RemoteBigMapIterator<K,T>(remoteConnectors, name, fromKey);
    }

    public Collection<String> listBigMaps() throws RemoteReadException {
        Set<String> set = new HashSet<String>();
        for (RemoteConnector remoteConnector : remoteConnectors) {
            Collection<String> collection =
                    remoteConnector.getReadDataResponse(InteractionType.LIST_BIG_MAPS, null);
            set.addAll(collection);
        }
        return set;
    }
}
