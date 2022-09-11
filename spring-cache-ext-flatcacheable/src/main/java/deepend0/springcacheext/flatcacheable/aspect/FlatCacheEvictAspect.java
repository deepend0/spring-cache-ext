package deepend0.springcacheext.flatcacheable.aspect;

import deepend0.springcacheext.flatcacheable.FlatCacheEvict;
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
import java.util.List;

@Aspect
public class FlatCacheEvictAspect extends FlatCacheOperation {
    private CacheManager cacheManager;

    public FlatCacheEvictAspect(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Pointcut("@annotation(deepend0.springcacheext.flatcacheable.FlatCacheEvict)")
    private void flatCacheEvictPointcut() { }

    @Around("flatCacheEvictPointcut()")
    public Collection<?> flatCachePutAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        FlatCacheParams aspectParams = parseCacheAnnotation(method);
        Collection<Object> newValues = (Collection<Object>) pjp.proceed();

        List<?> keys;
        if("result".equals(aspectParams.getKeyArgument())) {
            keys = getNestedFieldValues(newValues, aspectParams.getKeyField());
        } else {
            int parameterIndex = findKeyArgument(method, aspectParams.getKeyArgument());
            Collection<?> keyArgument = (Collection<?>)pjp.getArgs()[parameterIndex];
            keys = getNestedFieldValues(keyArgument, aspectParams.getKeyField());
        }

        Cache cache = cacheManager.getCache(aspectParams.getCacheName());
        for (Object key : keys) {
            cache.evictIfPresent(key);
        }
        return newValues;
    }

    @Override
    public FlatCacheParams parseCacheAnnotation(Method method) {
        FlatCacheEvict flatCacheEvict = method.getAnnotation(FlatCacheEvict.class);
        FlatCacheParams flatCacheParams = new FlatCacheParams();
        flatCacheParams.setCacheName(flatCacheEvict.cacheName());
        String [] argFields = parseArgumentFields(flatCacheEvict.key());
        String argName = parseArgumentName(argFields[0]);
        String [] keyField = Arrays.copyOfRange(argFields, 1, argFields.length);
        flatCacheParams.setKeyArgument(argName);
        flatCacheParams.setKeyField(keyField);
        return flatCacheParams;
    }
}
