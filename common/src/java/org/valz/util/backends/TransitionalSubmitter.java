package org.valz.util.backends;

import org.apache.log4j.Logger;
import org.valz.util.PeriodicWorker;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.Value;
import org.valz.util.datastores.DataStore;
import org.valz.util.protocol.messages.BigMapChunkValue;

import java.util.Collection;

class TransitionalSubmitter extends PeriodicWorker {
    private static final Logger LOG = Logger.getLogger(TransitionalSubmitter.class);

    private final WriteBackend writeBackend;
    private final DataStore dataStore;
    private int chunkSize;

    public TransitionalSubmitter(WriteBackend writeBackend, DataStore dataStore, long intervalMillis,
                                 int chunkSize) {
        super(intervalMillis);
        this.writeBackend = writeBackend;
        this.dataStore = dataStore;
        this.chunkSize = chunkSize;
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
                Aggregate aggregate = dataStore.getBigMapAggregate(name);
                BigMapChunkValue chunk = dataStore.getBigMapChunkForSubmit(name, "", chunkSize);
                try {
                    writeBackend.submitBigMap(name, aggregate, chunk.getValue());
                } catch (ConnectionRefusedRemoteWriteException e) {
                    try {
                        dataStore.submit(name, aggregate, chunk.getValue());
                    } catch (InvalidAggregateException e1) {
                        // Ignore
                    }
                    LOG.error("Can not send data to remote backend. Reason: connection failed.", e);
                    break;
                } catch (RemoteWriteException e) {
                    try {
                        dataStore.submit(name, aggregate, chunk.getValue());
                    } catch (InvalidAggregateException e1) {
                        // Ignore
                    }
                    LOG.error("Can not send data to remote backend.", e);
                    continue;
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
                Value value = dataStore.getValue(name);
                if (value == null) {
                    continue;
                }
                dataStore.removeAggregate(name);
                try {
                    writeBackend.submit(name, value.getAggregate(), value.getValue());
                } catch (ConnectionRefusedRemoteWriteException e) {
                    try {
                        dataStore.submit(name, value.getAggregate(), value.getValue());
                    } catch (InvalidAggregateException e1) {
                        // Ignore
                    }
                    LOG.error("Can not send data to remote backend. Reason: connection failed.", e);
                    break;
                } catch (RemoteWriteException e) {
                    try {
                        dataStore.submit(name, value.getAggregate(), value.getValue());
                    } catch (InvalidAggregateException e1) {
                        // Ignore
                    }
                    LOG.error("Can not send data to remote backend.", e);
                    continue;
                }
            }
        }
    }
}