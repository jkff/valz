package org.valz.api;

import org.jetbrains.annotations.NotNull;

public interface ValueParser<T> {
    T parse(@NotNull String str);
}
