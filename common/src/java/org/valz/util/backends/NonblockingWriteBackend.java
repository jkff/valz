package org.valz.util.backends;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.RemoteWriteException;
import org.valz.util.protocol.WriteBackend;
import org.valz.util.protocol.messages.SubmitRequest;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NonblockingWriteBackend implements WriteBackend {

    private final Queue<SubmitRequest> queue = new ConcurrentLinkedQueue<SubmitRequest>();

    public NonblockingWriteBackend(WriteBackend writeBackend) {
        new Thread(new NonblockingSubmitter(writeBackend, queue)).start();
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException {
        queue.offer(new SubmitRequest<T>(name, aggregate, value));
    }
}
