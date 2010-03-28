package org.valz.util.json;

import org.json.simple.JSONObject;

public class JSONBuilder {
    public static JSONObject makeJson(Object... kvp) {
        JSONObject res = new JSONObject();
        for(int i = 0; i < kvp.length; i+=2) {
            res.put(kvp[i], kvp[i+1]);
        }
        return res;
    }


    
    private JSONBuilder() {
    }
}
