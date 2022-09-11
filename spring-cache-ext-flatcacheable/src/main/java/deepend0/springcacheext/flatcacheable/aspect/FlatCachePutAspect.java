package deepend0.springcacheext.flatcacheable.aspect;

import deepend0.springcacheext.flatcacheable.FieldMapping;
import deepend0.springcacheext.flatcacheable.FlatCachePut;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Aspect
public class FlatCachePutAspect extends FlatCacheSaveOperation {
    private CacheManager cacheManager;

    public FlatCachePutAspect(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Pointcut("@annotation(deepend0.springcacheext.flatcacheable.FlatCachePut)")
    private void flatCachePutPointcut() { }

    @Around("flatCachePutPointcut()")
    public Collection<?> flatCachePutAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        FlatCacheSaveParams aspectParams = parseCacheAnnotation(method);

        Collection<Object> newValues = (Collection<Object>) pjp.proceed();

        Map<?, ?> keyValueMap;
        if("result".equals(aspectParams.getKeyArgument())) {
            keyValueMap = generateKeyValueMapUsingValues(newValues, aspectParams.getKeyField());
        } else {
            int parameterIndex = findKeyArgument(method, aspectParams.getKeyArgument());
            Collection<?> keyArgument = (Collection<?>)pjp.getArgs()[parameterIndex];
            keyValueMap = generateKeyValueMapByJoiningKeyValueObjects(keyArgument, newValues, aspectParams.getKeyField(),
                    aspectParams.getMappingKeyField(), aspectParams.getMappingValueField(), aspectParams.getCardinality());
        }

        Cache cache = cacheManager.getCache(aspectParams.getCacheName());
        for (Map.Entry entry : keyValueMap.entrySet()) {
            cache.put(entry.getKey(), entry.getValue());
        }
        return newValues;
    }

    @Override
    public FlatCacheSaveParams parseCacheAnnotation(Method method) {
        FlatCachePut flatCachePut = method.getAnnotation(FlatCachePut.class);
        FieldMapping mapping = flatCachePut.mapping();
        FlatCacheSaveParams flatCacheSaveParams = new FlatCacheSaveParams();
        flatCacheSaveParams.setCacheName(flatCachePut.cacheName());
        String [] argFields = parseArgumentFields(flatCachePut.key());
        String argName = parseArgumentName(argFields[0]);
        String [] keyField = Arrays.copyOfRange(argFields, 1, argFields.length);
        flatCacheSaveParams.setKeyArgument(argName);
        flatCacheSaveParams.setKeyField(keyField);
        flatCacheSaveParams.setMappingKeyField(parseArgumentFields(mapping.keyField()));
        flatCacheSaveParams.setMappingValueField(parseArgumentFields(mapping.valueField()));
        flatCacheSaveParams.setCardinality(mapping.cardinality());
        return flatCacheSaveParams;
    }

    private static Map<?, ?> generateKeyValueMapUsingValues(Collection<Object> newValues, String [] keyFields) {
        return associateFlatWithNestedFieldSingle(newValues, keyFields);
    }
}
