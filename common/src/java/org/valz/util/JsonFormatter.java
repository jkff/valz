package org.valz.util;

import com.sdicons.json.model.JSONValue;

public interface JsonFormatter<T> {
    JSONValue toJson(T item);
}
