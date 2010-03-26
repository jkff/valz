package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.Iterator;

public class IntSum implements Aggregate<Integer> {
    @NotNull
    public Integer reduce(Iterator<Integer> stream) {
        int res = 0;
        while(stream.hasNext())
            res+=stream.next();
        return res;
    }

    public void toJson(JSONObject stub) {
        // Nothing
    }

    public static String getMethod() {
        return "intSum";
    }

    public static IntSum fromJson(JSONObject json) {
        return new IntSum();
    }
}
