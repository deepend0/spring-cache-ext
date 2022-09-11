package deepend0.springcacheext.flatcacheable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

@SpringBootTest(classes = {TestConfig.class, FlatCacheableContextConfig.class, FlatCacheEvictDataService.class, DataPopulatorService.class})
public class FlatCacheEvictAspectTest {
    @Autowired
    CacheManager cacheManager;

    @Autowired
    FlatCacheEvictDataService flatCacheEvictDataService;

    @Test
    public void should_evict_by_argument_key() {
        Cache cache = cacheManager.getCache("lowById");
        cache.put("3_1_1", new LowClass("3_1_1"));
        cache.put("1_1_2", new LowClass("1_1_2"));
        flatCacheEvictDataService.findLowClassesByIds(List.of("1_1_1", "1_1_2"));
        Assertions.assertEquals("3_1_1", ((LowClass) cache.get("3_1_1").get()).id);
        Assertions.assertNull(cache.get("1_1_2"));
    }

    @Test
    public void should_evict_by_result_key() {
        Cache cache = cacheManager.getCache("lowById2");
        cache.put("3_1_1", new LowClass("3_1_1"));
        cache.put("1_1_2", new LowClass("1_1_2"));
        flatCacheEvictDataService.findAllLowClasses();
        Assertions.assertEquals("3_1_1", ((LowClass) cache.get("3_1_1").get()).id);
        Assertions.assertNull(cache.get("1_1_2"));
    }
}

@Service
class FlatCacheEvictDataService {
    @Autowired
    DataPopulatorService dataPopulatorService;

    @FlatCacheEvict(cacheName = "lowById", key = "#lowClassIds")
    public List<LowClass> findLowClassesByIds(List<String> lowClassIds) {
        return dataPopulatorService.findLowClassesByIds(lowClassIds);
    }

    @FlatCacheEvict(cacheName = "lowById2", key = "#result.id")
    public List<LowClass> findAllLowClasses() {
        return dataPopulatorService.findAllLowClasses();
    }
}