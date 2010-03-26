package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapMerge implements Aggregate<JSONObject> {

    private boolean keepFirst = false;

    public MapMerge(boolean keepFirst) {
        this.keepFirst = keepFirst;
    }

    @NotNull
    public JSONObject reduce(Iterator<JSONObject> stream) {
        JSONObject res = new JSONObject();
        while (stream.hasNext()) {
            if (!keepFirst) {
                res.putAll(stream.next());
            } else {
                for (Object objectEntry : stream.next().entrySet()) {
                    Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>)objectEntry;
                    if (!res.containsKey(entry.getKey())) {
                        res.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return res;
    }

    public void toJson(JSONObject stub) {
        stub.put("keepFirst", keepFirst);
    }

    public static String getMethod() {
        return "mapMerge";
    }

    public static MapMerge fromJson(JSONObject json) {
        boolean keepFirst = (Boolean) json.get("keepFirst");
        return new MapMerge(keepFirst);
    }
}