package org.valz.server;

import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class AggregateParser {
    private Map<String, Class<? extends Aggregate<?>>> method2class = new HashMap<String, Class<? extends Aggregate<?>>>();

    void registerSupportedAggregate(Class<? extends Aggregate<?>> clazz) {
        Method getMethod = AggregateUtils.findGetMethod(clazz);
        try {
            String method = (String) getMethod.invoke(null);
            method2class.put(method, clazz);
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public Aggregate<?> parse(JSONObject json) {
        String method = (String)json.get("method");
        if(method == null) {
            throw new IllegalArgumentException("No 'method' in makeJson object " + json.toJSONString());
        }
        Class<? extends Aggregate<?>> clazz = method2class.get(method);
        if(clazz == null) {
            return null;
        }
        for(Method m : clazz.getDeclaredMethods()) {
            if(m.getName().equals("fromJson") &&
                    (0 != (m.getModifiers() & Modifier.PUBLIC)) &&
                    (0 != (m.getModifiers() & Modifier.STATIC)) &&
                    m.getParameterTypes().length == 1 &&
                    m.getParameterTypes()[0].equals(JSONObject.class) &&
                    Aggregate.class.isAssignableFrom(m.getReturnType()))
            {
                try {
                    return (Aggregate<?>) m.invoke(null, json);
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new IllegalArgumentException(
                "The class " + clazz + " does not declare a public static Aggregate fromJson(JSONObject)");
    }
}
