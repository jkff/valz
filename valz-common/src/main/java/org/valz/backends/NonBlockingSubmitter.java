package org.valz.backends;

import org.apache.log4j.Logger;
import org.valz.util.PeriodicWorker;
import org.valz.protocol.messages.SubmitBigMapRequest;
import org.valz.protocol.messages.SubmitRequest;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

class NonBlockingSubmitter extends Thread {
    private static final Logger LOG = Logger.getLogger(NonBlockingSubmitter.class);

    private final WriteBackend writeBackend;
    private final BlockingQueue<SubmitRequest> aggregatesQueue;

    public NonBlockingSubmitter(WriteBackend writeBackend, BlockingQueue<SubmitRequest> aggregatesQueue) {
        this.writeBackend = writeBackend;
        this.aggregatesQueue = aggregatesQueue;
    }

    @Override
    public void run() {
        while (true) {
            SubmitRequest request = null;
            try {
                request = aggregatesQueue.take();
                writeBackend.submit(request.getName(), request.getAggregate(), request.getValue());
            } catch (ConnectionRefusedRemoteWriteException e) {
                aggregatesQueue.add(request);
                LOG.warn("Invalid submit. Reason: connection failed.", e);
            } catch (RemoteWriteException e) {
                LOG.warn("Invalid submit. Reason: unknown.", e);
            } catch (InterruptedException e) {
                aggregatesQueue.add(request);
                LOG.warn("Invalid submit. Reason: thread is interrupted.", e);
                break;
            }
        }
    }
}
