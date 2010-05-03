package org.valz.util.protocol.backends;

import org.valz.util.PeriodicWorker;
import org.valz.util.protocol.messages.SubmitRequest;

import java.util.Queue;

class NonBlockingSubmitter extends PeriodicWorker {
    private final WriteBackend writeBackend;
    private final Queue<SubmitRequest> queue;

    public NonBlockingSubmitter(WriteBackend writeBackend, Queue<SubmitRequest> queue, long intervalMillis) {
        super(intervalMillis);
        this.writeBackend = writeBackend;
        this.queue = queue;
    }

    @Override
    public void action() {
        SubmitRequest request;
        while ((request = queue.poll()) != null) {
            try {
                writeBackend.submit(request.getName(), request.getAggregate(), request.getValue());
            } catch (ConnectionRemoteWriteException e) {
                // TODO: write log - ?
                queue.add(request);
                break;
            } catch (RemoteWriteException e) {
                // TODO: write log - ?
                continue;
            }
        }
    }
}
