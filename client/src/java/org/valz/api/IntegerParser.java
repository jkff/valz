package org.valz.api;

import org.jetbrains.annotations.NotNull;

public class IntegerParser implements ValueParser<Integer> {
    public Integer parse(@NotNull String str) {
        return Integer.parseInt(str);
    }
}
