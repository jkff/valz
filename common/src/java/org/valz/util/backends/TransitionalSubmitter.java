package org.valz.util.backends;

import org.apache.log4j.Logger;
import org.valz.util.PeriodicWorker;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.BigMap;
import org.valz.util.datastores.DataStore;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

class TransitionalSubmitter extends PeriodicWorker {
    private static final Logger LOG = Logger.getLogger(TransitionalSubmitter.class);

    private final WriteBackend writeBackend;
    private final DataStore dataStore;

    public TransitionalSubmitter(WriteBackend writeBackend, DataStore dataStore, long intervalMillis) {
        super(intervalMillis);
        this.writeBackend = writeBackend;
        this.dataStore = dataStore;
    }

    @Override
    public void execute() {
        submitAggregates();
        submitBigMaps();
    }

    private void submitBigMaps() {
        Collection<String> bigMaps;
        synchronized (dataStore) {
            bigMaps = dataStore.listBigMaps();
        }
        for (String name : bigMaps) {
            synchronized (dataStore) {
                BigMap<?> bigMap = dataStore.getBigMap(name);
                if (bigMap == null) {
                    continue;
                }

                Iterator<? extends Map.Entry<String, ?>> iter = bigMap.iterator();
                while (iter.hasNext()) {
                    // TODO: make chunks by 100 items, not by 1
                    Map.Entry<String, ?> entry = iter.next();
                    Map chunk = Collections.singletonMap(entry.getKey(), entry.getValue());
                    iter.remove();
                    try {
                        writeBackend.submitBigMap(name, (Aggregate)bigMap.aggregate, chunk);
                    } catch (ConnectionRefusedRemoteWriteException e) {
                        bigMap.append(chunk);
                        LOG.error("Can not send data to remote backend. Reason: connection failed.", e);
                        break;
                    } catch (RemoteWriteException e) {
                        bigMap.append(chunk);
                        LOG.error("Can not send data to remote backend.", e);
                        continue;
                    }
                }
            }
        }
    }

    private void submitAggregates() {
        Collection<String> vars;
        synchronized (dataStore) {
            vars = dataStore.listVars();
        }
        for (String name : vars) {
            synchronized (dataStore) {
                Object value = dataStore.getValue(name);
                if (value == null) {
                    continue;
                }
                Aggregate aggregate = dataStore.getAggregate(name);
                dataStore.removeAggregate(name);
                try {
                    writeBackend.submit(name, aggregate, value);
                } catch (ConnectionRefusedRemoteWriteException e) {
                    dataStore.submit(name, aggregate, value);
                    LOG.error("Can not send data to remote backend. Reason: connection failed.", e);
                    break;
                } catch (RemoteWriteException e) {
                    dataStore.submit(name, aggregate, value);
                    LOG.error("Can not send data to remote backend.", e);
                    continue;
                }
            }
        }
    }
}