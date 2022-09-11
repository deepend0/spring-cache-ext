package deepend0.springcacheext.flatcacheable;

import deepend0.springcacheext.flatcacheable.aspect.FlatCacheEvictAspect;
import deepend0.springcacheext.flatcacheable.aspect.FlatCachePutAspect;
import deepend0.springcacheext.flatcacheable.aspect.FlatCacheableAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlatCacheableContextConfig {
    @Autowired
    CacheManager cacheManager;

    @Bean
    public FlatCacheableAspect flatCacheableAspect() {
        return new FlatCacheableAspect(cacheManager);
    }

    @Bean
    public FlatCachePutAspect flatCachePutAspect() {
        return new FlatCachePutAspect(cacheManager);
    }

    @Bean
    public FlatCacheEvictAspect flatCacheEvictAspect() {
        return new FlatCacheEvictAspect(cacheManager);
    }
}
