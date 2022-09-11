package deepend0.springcacheext.flatcacheable.aspect;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class FlatCacheOperation {
    public abstract FlatCacheParams parseCacheAnnotation(Method method);

    public static String[] parseArgumentFields(String fieldExpr) {
        if (fieldExpr != null && !fieldExpr.trim().equals("")) {
            return fieldExpr.split("\\.");
        }
        return new String[0];
    }

    public static String parseArgumentName(String argumentMarker) {
        if (argumentMarker.startsWith("#")) {
            return argumentMarker.substring(1);
        }
        throw new IllegalArgumentException("Not a valid argument marker");
    }

    public static int findKeyArgument(Method method, String keyArgument) {
        int parameterIndex = -1;
        for (int i = 0; i < method.getParameters().length; i++) {
            if (method.getParameters()[i].getName().equals(keyArgument)) {
                parameterIndex = i;
                break;
            }
        }
        return parameterIndex;
    }

    public static List<?> getNestedFieldValues(Object arg, String[] fields) {
        Stream<?> stream;

        if (arg instanceof Collection) {
            stream = ((Collection) arg).stream();
        } else {
            stream = Stream.of(arg);
        }

        for (String fieldName : fields) {
            stream = stream.flatMap(o -> {
                Object val = getFieldValue(o, fieldName);

                if (val instanceof Collection) {
                    return ((Collection<?>) val).stream();
                } else {
                    return Stream.of(val);
                }
            });
        }
        return stream.collect(Collectors.toList());
    }

    public static Object getFieldValue(Object target, String fieldName) {
        Class<?> clazz = target.getClass();
        Field field = ReflectionUtils.findField(clazz, fieldName);
        field.setAccessible(true);
        return ReflectionUtils.getField(field, target);
    }
}
