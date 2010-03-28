package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.Iterator;

public class LongSum implements Aggregate<Long> {
    @NotNull
    public Long reduce(Iterator<Long> stream) {
        long res = 0;
        while(stream.hasNext())
            res+=stream.next();
        return res;
    }

    public void toJson(JSONObject stub) {
        // Nothing
    }

    public static String getMethod() {
        return "longSum";
    }

    public static LongSum fromJson(JSONObject json) {
        return new LongSum();
    }
}
