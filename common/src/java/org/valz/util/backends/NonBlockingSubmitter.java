package org.valz.util.backends;

import org.apache.log4j.Logger;
import org.valz.util.PeriodicWorker;
import org.valz.util.protocol.messages.SubmitRequest;

import java.util.Queue;

class NonBlockingSubmitter extends PeriodicWorker {
    private static final Logger log = Logger.getLogger(NonBlockingSubmitter.class);

    private final WriteBackend writeBackend;
    private final Queue<SubmitRequest> queue;

    public NonBlockingSubmitter(WriteBackend writeBackend, Queue<SubmitRequest> queue, long intervalMillis) {
        super(intervalMillis);
        this.writeBackend = writeBackend;
        this.queue = queue;
    }

    @Override
    public void execute() {
        SubmitRequest request;
        while ((request = queue.poll()) != null) {
            try {
                writeBackend.submit(request.getName(), request.getAggregate(), request.getValue());
            } catch (ConnectionRefusedRemoteWriteException e) {
                queue.add(request);
                log.info("Invalid submit.", e);
                break;
            } catch (RemoteWriteException e) {
                log.info("Invalid submit.", e);
                continue;
            }
        }
    }
}
