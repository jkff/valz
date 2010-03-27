package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class AggregateRegistry {
    private Map<String, Class<? extends Aggregate<?>>> method2class = new HashMap<String, Class<? extends Aggregate<?>>>();

    private static Method getDeserialize(Class<? extends Aggregate<?>> clazz) {
        for (Method m : clazz.getDeclaredMethods()) {
            if ("deserialize".equals(m.getName()) &&
                    (0 != (m.getModifiers() & Modifier.PUBLIC)) &&
                    (0 != (m.getModifiers() & Modifier.STATIC)) &&
                    m.getParameterTypes().length == 2 &&
                    m.getParameterTypes()[0].equals(Object.class) &&
                    m.getParameterTypes()[1].equals(AggregateRegistry.class) &&
                    Aggregate.class.isAssignableFrom(m.getReturnType())) {
                return m;
            }
        }
        throw new IllegalArgumentException(String.format(
                "The class %s does not declare a public static Aggregate deserialize(Object)", clazz));
    }

    public void registerSupportedAggregate(Class<? extends Aggregate<?>> clazz) {
        Method method = getDeserialize(clazz);
        method2class.put(clazz.getName(), clazz);
    }

    @Nullable
    public Aggregate<?> parseJson(JSONObject json) {
        String methodName = (String) json.get("methodName");
        if (methodName == null) {
            throw new IllegalArgumentException("No 'methodName' in makeJson object " + json.toJSONString());
        }
        Class<? extends Aggregate<?>> clazz = method2class.get(methodName);
        if (clazz == null) {
            throw new RuntimeException(
                    String.format("Aggregate with '%s' name is not registered.", methodName));
        }
        Method method = getDeserialize(clazz);
        JSONObject jsonAggregate = (JSONObject) json.get("aggregate");
        try {
            return (Aggregate<?>) method.invoke(null, jsonAggregate, this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject toJson(@NotNull Aggregate aggregate) {
        JSONObject json = new JSONObject();
        json.put("method", aggregate.getClass().getName());
        json.put("aggregate", aggregate.toSerialized());
        return json;
    }
}
