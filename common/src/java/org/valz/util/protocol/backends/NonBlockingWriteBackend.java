package org.valz.util.protocol.backends;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.messages.SubmitRequest;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NonBlockingWriteBackend implements WriteBackend {

    private final Queue<SubmitRequest> queue = new ConcurrentLinkedQueue<SubmitRequest>();

    public NonBlockingWriteBackend(WriteBackend writeBackend, long intervalMillis) {
        new Thread(new NonBlockingSubmitter(writeBackend, queue, intervalMillis)).start();
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException {
        queue.offer(new SubmitRequest<T>(name, aggregate, value));
    }
}
