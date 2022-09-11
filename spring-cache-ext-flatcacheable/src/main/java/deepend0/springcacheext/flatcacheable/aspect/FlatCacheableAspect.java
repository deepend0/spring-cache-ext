package deepend0.springcacheext.flatcacheable.aspect;

import deepend0.springcacheext.flatcacheable.FieldMapping;
import deepend0.springcacheext.flatcacheable.FlatCacheable;
import deepend0.springcacheext.flatcacheable.MappingCardinality;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
public class FlatCacheableAspect extends FlatCacheSaveOperation {
    private CacheManager cacheManager;

    public FlatCacheableAspect(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Pointcut("@annotation(deepend0.springcacheext.flatcacheable.FlatCacheable)")
    private void flatCacheablePointcut() { }

    @Around("flatCacheablePointcut()")
    private Collection<?> flatCacheableAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();

        FlatCacheSaveParams aspectParams = parseCacheAnnotation(method);

        int parameterIndex = findKeyArgument(method, aspectParams.getKeyArgument());
        Collection<?> keyArgument = (Collection<?>)pjp.getArgs()[parameterIndex];
        Class<? extends Collection> parameterType = (Class<? extends Collection>) method.getParameters()[parameterIndex].getType();
        Class<? extends Collection> returnType = (Class<? extends Collection>) method.getReturnType();

        Cache cache = cacheManager.getCache(aspectParams.getCacheName());
        Collection<Object> nonExistingKeys = new ArrayList<>();
        Collection<Object> nonExistingKeyObjects = createDefaultCollectionInstance(parameterType);
        List<Object> existingValues = new ArrayList<>();

        associateFlatWithNestedFieldSingle(keyArgument, aspectParams.getKeyField()).forEach((k, v)-> {
            Cache.ValueWrapper value = cache.get(k);
            if(value == null) {
                nonExistingKeys.add(k);
                nonExistingKeyObjects.add(v);
            } else {
                existingValues.add(value.get());
            }
        });

        Collection<Object> result = createDefaultCollectionInstance(returnType);
        if(aspectParams.getCardinality() == MappingCardinality.SINGLE) {
            result.addAll(existingValues);
        } else if(aspectParams.getCardinality() == MappingCardinality.MULTIPLE) {
            result.addAll(existingValues.stream().flatMap(val->((Collection<Object>)val).stream()).collect(Collectors.toList()));
        }
        if(!nonExistingKeys.isEmpty()) {
            Collection<Object> newValues = (Collection<Object>) pjp.proceed(new Object[]{nonExistingKeyObjects});
            Map<?, ?> keyValueMap = generateKeyValueMapByJoiningKeyValueObjects(nonExistingKeyObjects, newValues, aspectParams.getKeyField(),
                    aspectParams.getMappingKeyField(), aspectParams.getMappingValueField(), aspectParams.getCardinality());
            for (Object key : nonExistingKeys) {
                cache.put(key, keyValueMap.get(key));
            }
            result.addAll(newValues);
        }
        return result;
    }

    @Override
    public FlatCacheSaveParams parseCacheAnnotation(Method method) {
        FlatCacheable flatCacheable = method.getAnnotation(FlatCacheable.class);
        FieldMapping mapping = flatCacheable.mapping();
        FlatCacheSaveParams flatCacheSaveParams = new FlatCacheSaveParams();
        flatCacheSaveParams.setCacheName(flatCacheable.cacheName());
        String [] argFields = parseArgumentFields(flatCacheable.key());
        String argName = parseArgumentName(argFields[0]);
        String [] keyField = Arrays.copyOfRange(argFields, 1, argFields.length);
        flatCacheSaveParams.setKeyArgument(argName);
        flatCacheSaveParams.setKeyField(keyField);
        flatCacheSaveParams.setMappingKeyField(parseArgumentFields(mapping.keyField()));
        flatCacheSaveParams.setMappingValueField(parseArgumentFields(mapping.valueField()));
        flatCacheSaveParams.setCardinality(mapping.cardinality());
        return flatCacheSaveParams;
    }
}
