package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.Iterator;

public class MinLong implements Aggregate<Long> {
    @NotNull
    public Long reduce(Iterator<Long> stream) {
        long res = Long.MAX_VALUE;
        while (stream.hasNext()) {
            long value = stream.next();
            if (value < res) {
                res = value;
            }
        }
        return res;
    }

    public void toJson(JSONObject stub) {
        // Nothing
    }

    public static String getMethod() {
        return "minLong";
    }

    public static MinLong fromJson(JSONObject json) {
        return new MinLong();
    }
}