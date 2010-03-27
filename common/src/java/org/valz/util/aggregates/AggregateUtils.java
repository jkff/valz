package org.valz.util.aggregates;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


public class AggregateUtils {
    public static Method findGetMethod(Class<? extends Aggregate> clazz) {
        Method getMethod = null;
        for(Method m : clazz.getDeclaredMethods()) {
            if(m.getName().equals("getMethod") &&
                    (0 != (m.getModifiers() & Modifier.PUBLIC)) &&
                    (0 != (m.getModifiers() & Modifier.STATIC)) &&
                    m.getParameterTypes().length == 0 &&
                    m.getReturnType().equals(String.class))
            {
                getMethod = m;
                break;
            }
        }
        if(getMethod == null)
        throw new IllegalArgumentException(
                "The class " + clazz + " does not declare a public static String getMethod()");
        return getMethod;
    }
}
