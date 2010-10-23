package org.valz.backends;

import org.apache.log4j.Logger;
import org.valz.protocol.messages.SubmitBigMapRequest;
import org.valz.protocol.messages.SubmitRequest;

import java.util.concurrent.BlockingQueue;

class NonBlockingBigMapsSubmitter extends Thread {
    private static final Logger LOG = Logger.getLogger(NonBlockingBigMapsSubmitter.class);

    private final WriteBackend writeBackend;
    private final BlockingQueue<SubmitBigMapRequest> bigMapsQueue;

    public NonBlockingBigMapsSubmitter(WriteBackend writeBackend, BlockingQueue<SubmitBigMapRequest> bigMapsQueue) {
        this.writeBackend = writeBackend;
        this.bigMapsQueue = bigMapsQueue;
    }

    @Override
    public void run() {
        while (true) {
            SubmitBigMapRequest request = null;
            try {
                request = bigMapsQueue.take();
                writeBackend.submitBigMap(request.getName(), request.getKeyType(), request.getAggregate(), request.getValue());
            } catch (ConnectionRefusedRemoteWriteException e) {
                bigMapsQueue.add(request);
                LOG.info("Invalid submit. Reason: connection failed.", e);
            } catch (RemoteWriteException e) {
                LOG.info("Invalid submit. Reason: unknown.", e);
            } catch (InterruptedException e) {
                bigMapsQueue.add(request);
                LOG.info("Invalid submit. Reason: thread is interrupted.", e);
                break;
            }
        }
    }
}