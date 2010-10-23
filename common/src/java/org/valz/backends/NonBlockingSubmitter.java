package org.valz.backends;

import org.apache.log4j.Logger;
import org.valz.util.PeriodicWorker;
import org.valz.protocol.messages.SubmitBigMapRequest;
import org.valz.protocol.messages.SubmitRequest;

import java.util.Queue;

class NonBlockingSubmitter extends PeriodicWorker {
    private static final Logger LOG = Logger.getLogger(NonBlockingSubmitter.class);

    private final WriteBackend writeBackend;
    private final Queue<SubmitRequest> aggregatesQueue;
    private final Queue<SubmitBigMapRequest> bigMapsQueue;

    public NonBlockingSubmitter(WriteBackend writeBackend, Queue<SubmitRequest> aggregatesQueue,
                                Queue<SubmitBigMapRequest> bigMapsQueue, long intervalMillis) {
        super("submit queue", intervalMillis);
        this.writeBackend = writeBackend;
        this.aggregatesQueue = aggregatesQueue;
        this.bigMapsQueue = bigMapsQueue;
    }

    @Override
    public void tick() {
        sumbitAggregates();
        sumbitBigMaps();
    }

    private void sumbitBigMaps() {
        SubmitBigMapRequest request;
        while ((request = bigMapsQueue.poll()) != null) {
            try {
                writeBackend.submitBigMap(request.getName(), request.getKeyType(), request.getAggregate(), request.getValue());
            } catch (ConnectionRefusedRemoteWriteException e) {
                bigMapsQueue.add(request);
                LOG.info("Invalid submit. Reason: connection failed.", e);
                break;
            } catch (RemoteWriteException e) {
                LOG.info("Invalid submit.", e);
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
            }
        }
    }
}
