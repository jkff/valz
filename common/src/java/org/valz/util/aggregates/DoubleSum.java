package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.Iterator;

public class DoubleSum implements Aggregate<Double> {
    @NotNull
    public Double reduce(Iterator<Double> stream) {
        double res = 0;
        while(stream.hasNext())
            res+=stream.next();
        return res;
    }

    public void toJson(JSONObject stub) {
        // Nothing
    }

    public static String getMethod() {
        return "doubleSum";
    }

    public static DoubleSum fromJson(JSONObject json) {
        return new DoubleSum();
    }
}