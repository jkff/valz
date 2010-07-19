package org.valz.backends;

import org.valz.model.Aggregate;
import org.valz.keytypes.KeyType;
import org.valz.protocol.messages.SubmitBigMapRequest;
import org.valz.protocol.messages.SubmitRequest;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NonBlockingWriteBackend implements WriteBackend {

    private final Queue<SubmitRequest> aggregatesQueue = new ConcurrentLinkedQueue<SubmitRequest>();
    private final Queue<SubmitBigMapRequest> bigMapsQueue = new ConcurrentLinkedQueue<SubmitBigMapRequest>();

    public NonBlockingWriteBackend(WriteBackend writeBackend, long intervalMillis) {
        new NonBlockingSubmitter(writeBackend, aggregatesQueue, bigMapsQueue, intervalMillis).start();
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException {
        aggregatesQueue.offer(new SubmitRequest<T>(name, aggregate, value));
    }

    public <K, T> void submitBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> value) throws
            RemoteWriteException {
        bigMapsQueue.offer(new SubmitBigMapRequest<K, T>(name, keyType, aggregate, value));
    }
}
