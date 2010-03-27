package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListConcat implements Aggregate<JSONArray> {
    @NotNull
    public JSONArray reduce(Iterator<JSONArray> stream) {
        JSONArray res = new JSONArray();
        while (stream.hasNext()) {
            res.addAll(stream.next());
        }
        return res;
    }

    public void toJson(JSONObject stub) {
        // Nothing
    }

    public static String getMethod() {
        return "listConcat";
    }

    public static ListConcat fromJson(JSONObject json) {
        return new ListConcat();
    }
}