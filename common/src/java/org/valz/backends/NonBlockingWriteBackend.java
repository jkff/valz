package org.valz.backends;

import org.valz.model.Aggregate;
import org.valz.protocol.messages.SubmitBigMapRequest;
import org.valz.protocol.messages.SubmitRequest;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NonBlockingWriteBackend implements WriteBackend {

    private final BlockingQueue<SubmitRequest> aggregatesQueue = new LinkedBlockingQueue<SubmitRequest>();
    private final BlockingQueue<SubmitBigMapRequest> bigMapsQueue = new LinkedBlockingQueue<SubmitBigMapRequest>();

    public NonBlockingWriteBackend(WriteBackend writeBackend) {
        new NonBlockingSubmitter(writeBackend, aggregatesQueue).start();
        new NonBlockingBigMapsSubmitter(writeBackend, bigMapsQueue).start();
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException {
        aggregatesQueue.offer(new SubmitRequest<T>(name, aggregate, value));
    }

    public <T> void submitBigMap(String name, Aggregate<T> aggregate, Map<String, T> value) throws
            RemoteWriteException {
        bigMapsQueue.offer(new SubmitBigMapRequest<T>(name, aggregate, value));
    }
}
