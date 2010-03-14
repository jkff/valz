package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class IntSum implements Aggregate<Integer> {
    @NotNull
    public Integer reduce(Iterator<Integer> stream) {
        int res = 0;
        while(stream.hasNext())
            res+=stream.next();
        return res;
    }
}
