package org.valz.util.protocol;

import org.apache.log4j.Logger;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.RemoteWriteException;
import org.valz.util.protocol.WriteBackend;
import org.valz.util.protocol.messages.SubmitRequest;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

class BatchSubmitter implements Runnable {
    private static final Logger log = Logger.getLogger(BatchSubmitter.class);

    private final WriteBackend writeBackend;
    private final ConcurrentLinkedQueue<SubmitRequest> queue;

    public BatchSubmitter(WriteBackend writeBackend, ConcurrentLinkedQueue<SubmitRequest> queue) {
        this.writeBackend = writeBackend;
        this.queue = queue;
    }

    public void run() {
        Thread.currentThread().setDaemon(true);

        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO: invent something
                break;
            }

            Map<String, List<SubmitRequest>> map = new HashMap<String, List<SubmitRequest>>();
            {
                SubmitRequest request;
                while ((request = queue.poll()) != null) {
                    List<SubmitRequest> list = map.get(request.getName());
                    if (list == null) {
                        map.put(request.getName(), Arrays.asList(request));
                    } else {
                        list.add(request);
                    }
                }
            }

            for (List<SubmitRequest> requestsList : map.values()) {
                String name = requestsList.get(0).getName();
                Aggregate aggregate = requestsList.get(0).getAggregate();

                List<Object> valuesList = new ArrayList<Object>();
                for (SubmitRequest request : requestsList) {
                    if (!request.getAggregate().equals(aggregate)) {
                        // TODO: invent something for different aggregates with same val name
                    }
                    valuesList.add(request.getValue());
                }
                Object value = aggregate.reduce(valuesList.iterator());
                try {
                    writeBackend.submit(name, aggregate, value);
                } catch (RemoteWriteException e) {
                    // TODO: invent something
                }
            }
        }
    }
}
