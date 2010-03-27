package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static org.valz.util.json.JSONBuilder.makeJson;

public class AggregateRegistry {
    @NotNull
    private static Method getDeserialize(@NotNull Class<? extends Aggregate<?>> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if ("deserialize".equals(method.getName()) &&
                    (0 != (method.getModifiers() & Modifier.PUBLIC)) &&
                    (0 != (method.getModifiers() & Modifier.STATIC)) &&
                    method.getParameterTypes().length == 2 &&
                    method.getParameterTypes()[0].equals(Object.class) &&
                    method.getParameterTypes()[1].equals(AggregateRegistry.class) &&
                    Aggregate.class.isAssignableFrom(method.getReturnType())) {
                return method;
            }
        }
        throw new IllegalArgumentException(String.format(
                "The class %s does not declare a public static Aggregate deserialize(Object)", clazz));
    }

    @NotNull
    public static String toAggregateString(@NotNull Aggregate aggregate) {
        return makeJson(
                "method", aggregate.getClass().getName(),
                "param", aggregate.toSerialized()
                ).toJSONString();
    }



    private final Map<String, Class<? extends Aggregate<?>>> name2class = new HashMap<String, Class<? extends Aggregate<?>>>();

    

    public String getAggregateName(@NotNull String name) {
        Class<? extends Aggregate<?>> clazz = name2class.get(name);
        return clazz.getName();
    }

    public void registerSupportedAggregate(@NotNull Class<? extends Aggregate<?>> clazz) {
        getDeserialize(clazz);
        name2class.put(clazz.getName(), clazz);
    }

    @NotNull
    public Aggregate<?> parseAggregateString(@NotNull JSONObject json) {
        String methodName = (String) json.get("deserializeMethod");
        if (methodName == null) {
            throw new IllegalArgumentException(String.format(
                    "No 'deserializeMethod' in makeJson object %s.", json.toJSONString()));
        }
        Class<? extends Aggregate<?>> clazz = name2class.get(methodName);
        if (clazz == null) {
            throw new RuntimeException(
                    String.format("Aggregate with '%s' name is not registered.", methodName));
        }
        Method deserializeMethod = getDeserialize(clazz);
        JSONObject paramObject = (JSONObject) json.get("param");
        try {
            return (Aggregate<?>) deserializeMethod.invoke(null, paramObject, this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
