package org.valz.util.backends;

import org.valz.util.protocol.RemoteWriteException;
import org.valz.util.protocol.WriteBackend;
import org.valz.util.protocol.messages.SubmitRequest;

import java.util.Queue;

class NonblockingSubmitter extends PeriodicWorker {
    private final WriteBackend writeBackend;
    private final Queue<SubmitRequest> queue;

    public NonblockingSubmitter(WriteBackend writeBackend, Queue<SubmitRequest> queue) {
        super(5000);
        this.writeBackend = writeBackend;
        this.queue = queue;
    }

    @Override
    public void action() {
        SubmitRequest request;
        while ((request = queue.poll()) != null) {
            try {
                writeBackend.submit(request.getName(), request.getAggregate(), request.getValue());
            } catch (RemoteWriteException e) {
                // TODO: write log - ?
            }
        }
    }
}
