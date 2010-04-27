package org.valz.util.protocol;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.RemoteWriteException;
import org.valz.util.protocol.WriteBackend;
import org.valz.util.protocol.messages.SubmitRequest;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BatchingWriteBackend implements WriteBackend {

    private final ConcurrentLinkedQueue<SubmitRequest> queue = new ConcurrentLinkedQueue<SubmitRequest>();

    public BatchingWriteBackend(WriteBackend writeBackend) {
        new Thread(new BatchSubmitter(writeBackend, queue)).start();
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException {
        queue.offer(new SubmitRequest<T>(name, aggregate, value));
    }
}
