package org.valz.util.backends;

import org.apache.log4j.Logger;
import org.valz.util.PeriodicWorker;
import org.valz.util.protocol.messages.SubmitBigMapRequest;
import org.valz.util.protocol.messages.SubmitRequest;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class NonBlockingSubmitter extends PeriodicWorker {
    private static final Logger LOG = Logger.getLogger(NonBlockingSubmitter.class);

    private final WriteBackend writeBackend;
    private final Queue<SubmitRequest> aggregatesQueue;
    private final Queue<SubmitBigMapRequest> bigMapsQueue;

    public NonBlockingSubmitter(WriteBackend writeBackend, Queue<SubmitRequest> aggregatesQueue, Queue<SubmitBigMapRequest> bigMapsQueue, long intervalMillis) {
        super(intervalMillis);
        this.writeBackend = writeBackend;
        this.aggregatesQueue = aggregatesQueue;
        this.bigMapsQueue = bigMapsQueue;
    }

    @Override
    public void execute() {
        sumbitAggregates();
        sumbitBigMaps();
    }

    private void sumbitBigMaps() {
        SubmitRequest request;
        while ((request = aggregatesQueue.poll()) != null) {
            try {
                writeBackend.submit(request.getName(), request.getAggregate(), request.getValue());
            } catch (ConnectionRefusedRemoteWriteException e) {
                aggregatesQueue.add(request);
                LOG.info("Invalid submit. Reason: connection failed.", e);
                break;
            } catch (RemoteWriteException e) {
                LOG.info("Invalid submit.", e);
                continue;
            }
        }
    }

    private void sumbitAggregates() {
        SubmitRequest request;
        while ((request = aggregatesQueue.poll()) != null) {
            try {
                writeBackend.submit(request.getName(), request.getAggregate(), request.getValue());
            } catch (ConnectionRefusedRemoteWriteException e) {
                aggregatesQueue.add(request);
                LOG.info("Invalid submit. Reason: connection failed.", e);
                break;
            } catch (RemoteWriteException e) {
                LOG.info("Invalid submit.", e);
                continue;
            }
        }
    }
}
