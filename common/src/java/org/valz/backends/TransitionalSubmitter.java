package org.valz.backends;

import org.apache.log4j.Logger;
import org.valz.util.PeriodicWorker;
import org.valz.aggregates.Aggregate;
import org.valz.aggregates.Sample;
import org.valz.datastores.DataStore;
import org.valz.keytypes.KeyType;
import org.valz.protocol.messages.BigMapChunkValue;

import java.util.Collection;

class TransitionalSubmitter extends PeriodicWorker {
    private static final Logger LOG = Logger.getLogger(TransitionalSubmitter.class);

    private final WriteBackend writeBackend;
    private final DataStore dataStore;
    private int chunkSize;

    public TransitionalSubmitter(WriteBackend writeBackend, DataStore dataStore, long intervalMillis,
                                 int chunkSize) {
        super("transitional submitter", intervalMillis);
        this.writeBackend = writeBackend;
        this.dataStore = dataStore;
        this.chunkSize = chunkSize;
    }

    @Override
    public void tick() {
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
                KeyType keyType = dataStore.getBigMapKeyType(name);
                Aggregate aggregate = dataStore.getBigMapAggregate(name);
                BigMapChunkValue chunk = dataStore.popBigMapChunk(name, keyType.getMinValue(), chunkSize);
                try {
                    writeBackend.submitBigMap(name, keyType, aggregate, chunk.getValue());
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
                Sample sample = dataStore.getValue(name);
                if (sample == null) {
                    continue;
                }
                dataStore.removeAggregate(name);
                try {
                    writeBackend.submit(name, sample.getAggregate(), sample.getValue());
                } catch (ConnectionRefusedRemoteWriteException e) {
                    try {
                        dataStore.submit(name, sample.getAggregate(), sample.getValue());
                    } catch (InvalidAggregateException e1) {
                        // Ignore
                    }
                    LOG.error("Can not send data to remote backend. Reason: connection failed.", e);
                    break;
                } catch (RemoteWriteException e) {
                    try {
                        dataStore.submit(name, sample.getAggregate(), sample.getValue());
                    } catch (InvalidAggregateException e1) {
                        // Ignore
                    }
                    LOG.error("Can not send data to remote backend.", e);
                }
            }
        }
    }
}