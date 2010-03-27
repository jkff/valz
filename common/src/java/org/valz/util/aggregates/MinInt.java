package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.Iterator;

public class MinInt implements Aggregate<Integer> {
    @NotNull
    public Integer reduce(Iterator<Integer> stream) {
        int res = Integer.MAX_VALUE;
        while (stream.hasNext()) {
            int value = stream.next();
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
        return "minInt";
    }

    public static MinInt fromJson(JSONObject json) {
        return new MinInt();
    }
}