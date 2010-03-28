package org.valz.server;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateParser;
import org.valz.util.aggregates.AggregateUtils;
import org.valz.util.protocol.Backend;
import org.valz.util.protocol.RemoteReadException;

import java.util.*;

public class ValzBackend implements Backend {
    private static final Logger log = Logger.getLogger(ValzBackend.class);

    private AggregateParser parser = new AggregateParser();
    private Map<String, Object> name2val = new HashMap<String, Object>();
    private Map<String, Aggregate> name2agg = new HashMap<String, Aggregate>();

    public synchronized void submit(String name, JSONObject aggregateSpec, Object value) {
        Aggregate aggregate;
        try {
            aggregate = parser.parse(aggregateSpec);
        } catch (Exception e) {
            log.error("Malformed aggregate spec: " + aggregateSpec.toJSONString(), e);
            return;
        }
        if (!name2val.containsKey(name)) {
            name2val.put(name, value);
            name2agg.put(name, aggregate);
        } else {
            Object oldValue = name2val.get(name);
            List<Object> list = Arrays.asList(oldValue, value);
            Object newValue = aggregate.reduce(list.iterator());
            name2val.put(name, newValue);
        }
    }

    public synchronized Collection<String> listVars() {
        return new ArrayList<String>(name2val.keySet());
    }

    public JSONObject getAggregateDescription(String name) throws RemoteReadException {
        return AggregateUtils.toJson(name2agg.get(name));
    }

    public synchronized Object getValue(String name) {
        return name2val.get(name);
    }

    public synchronized void registerSupportedAggregate(Class<? extends Aggregate<?>> clazz) {
        parser.registerSupportedAggregate(clazz);
    }
}
