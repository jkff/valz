package org.valz.util.backends;

import org.apache.log4j.Logger;
import org.valz.util.PeriodicWorker;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.datastores.DataStore;

import java.util.Collection;

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
                    dataStore.createAggregate(name, aggregate, value);
                    LOG.error("Can not send data to remote backend. Reason: connection failed.", e);
                    break;
                } catch (RemoteWriteException e) {
                    dataStore.createAggregate(name, aggregate, value);
                    LOG.error("Can not send data to remote backend.", e);
                    continue;
                }
            }
        }
    }
}