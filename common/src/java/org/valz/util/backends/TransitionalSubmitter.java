package org.valz.util.backends;

import org.valz.util.PeriodicWorker;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.datastores.DataStore;

import java.util.Collection;

class TransitionalSubmitter extends PeriodicWorker {

    private final WriteBackend writeBackend;
    private final DataStore dataStore;

    public TransitionalSubmitter(WriteBackend writeBackend, DataStore dataStore, long intervalMillis) {
        super(intervalMillis);
        this.writeBackend = writeBackend;
        this.dataStore = dataStore;
    }

    @Override
    public void execute() {
        Collection<String> vars = null;
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
                try {
                    writeBackend.submit(name, aggregate, value);
                } catch (ConnectionRefusedRemoteWriteException e) {
                    // TODO: write log - ?
                    break;
                } catch (RemoteWriteException e) {
                    continue;
                }
                dataStore.removeAggregate(name);
            }
        }
    }
}