package deepend0.springcacheext.flatcacheable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = {TestConfig.class, FlatCacheableContextConfig.class, FlatCachePutDataService.class, DataPopulatorService.class})
public class FlatCachePutAspectTest {
    @Autowired
    CacheManager cacheManager;

    @Autowired
    DataPopulatorService dataPopulatorService;

    @Autowired
    FlatCachePutDataService flatCachePutDataService;

    @Test
    public void should_cache_all_new_values() {
        Cache cache = cacheManager.getCache("topByLowId");
        cache.put("1_1_1", new TopClass("3"));
        flatCachePutDataService.findTopClassesByLowClassIds(List.of("1_1_1", "2_2_2"));
        Assertions.assertEquals("1", ((TopClass) cache.get("1_1_1").get()).id);
        Assertions.assertNull(cache.get("1_1_2"));
        Assertions.assertNull(cache.get("1_2_1"));
        Assertions.assertNull(cache.get("1_2_2"));
        Assertions.assertNull(cache.get("2_1_1"));
        Assertions.assertNull(cache.get("2_1_2"));
        Assertions.assertNull(cache.get("2_2_1"));
        Assertions.assertEquals("2", ((TopClass) cache.get("2_2_2").get()).id);
    }

    @Test
    public void should_cache_many_to_one_relationship_from_value() {
        flatCachePutDataService.findTopClassesByLowClassIds2(List.of("1_1_1", "1_1_2", "1_2_1", "1_2_2", "2_1_1",
                "2_1_2", "2_2_1", "2_2_2"));
        Cache cache = cacheManager.getCache("topByLowId2");
        Assertions.assertEquals("1", ((TopClass) cache.get("1_1_1").get()).id);
        Assertions.assertEquals("1", ((TopClass) cache.get("1_1_2").get()).id);
        Assertions.assertEquals("1", ((TopClass) cache.get("1_2_1").get()).id);
        Assertions.assertEquals("1", ((TopClass) cache.get("1_2_2").get()).id);
        Assertions.assertEquals("2", ((TopClass) cache.get("2_1_1").get()).id);
        Assertions.assertEquals("2", ((TopClass) cache.get("2_1_2").get()).id);
        Assertions.assertEquals("2", ((TopClass) cache.get("2_2_1").get()).id);
        Assertions.assertEquals("2", ((TopClass) cache.get("2_2_2").get()).id);
    }

    @Test
    public void should_cache_many_to_one_relationship_from_key() {
        List<LowClass> lowClasses = dataPopulatorService.findAllLowClasses();
        flatCachePutDataService.findTopClassesByLowClasses(lowClasses);
        Cache cache = cacheManager.getCache("topByLowId3");
        Assertions.assertEquals("1", ((TopClass) cache.get("1_1_1").get()).id);
        Assertions.assertEquals("1", ((TopClass) cache.get("1_1_2").get()).id);
        Assertions.assertEquals("1", ((TopClass) cache.get("1_2_1").get()).id);
        Assertions.assertEquals("1", ((TopClass) cache.get("1_2_2").get()).id);
        Assertions.assertEquals("2", ((TopClass) cache.get("2_1_1").get()).id);
        Assertions.assertEquals("2", ((TopClass) cache.get("2_1_2").get()).id);
        Assertions.assertEquals("2", ((TopClass) cache.get("2_2_1").get()).id);
        Assertions.assertEquals("2", ((TopClass) cache.get("2_2_2").get()).id);
    }

    @Test
    public void should_cache_one_to_many_relationship_from_value() {
        flatCachePutDataService.findLowClassesByTopClassIds(List.of("1", "2"));
        Cache cache = cacheManager.getCache("lowsByTopId");
        List<LowClass> lows1 = (List<LowClass>) cache.get("1").get();
        Assertions.assertEquals(lows1.size(), 4);
        List<LowClass> lows2 = (List<LowClass>) cache.get("2").get();
        Assertions.assertEquals(lows2.size(), 4);
    }

    @Test
    public void should_cache_one_to_many_relationship_from_key() {
        List<TopClass> topClasses = dataPopulatorService.findAllTopClasses();
        flatCachePutDataService.findLowClassesByTopClasses(topClasses);
        Cache cache = cacheManager.getCache("lowsByTopId2");
        List<LowClass> lows1 = (List<LowClass>) cache.get("1").get();
        Assertions.assertEquals(lows1.size(), 4);
        List<LowClass> lows2 = (List<LowClass>) cache.get("2").get();
        Assertions.assertEquals(lows2.size(), 4);
    }

    @Test
    public void should_cache_only_from_value_object() {
        flatCachePutDataService.findAllLowClasses();
        Cache cache = cacheManager.getCache("lowById");
        Assertions.assertEquals("1_1_1", ((LowClass) cache.get("1_1_1").get()).id);
        Assertions.assertEquals("1_1_2", ((LowClass) cache.get("1_1_2").get()).id);
        Assertions.assertEquals("1_2_1", ((LowClass) cache.get("1_2_1").get()).id);
        Assertions.assertEquals("1_2_2", ((LowClass) cache.get("1_2_2").get()).id);
        Assertions.assertEquals("2_1_1", ((LowClass) cache.get("2_1_1").get()).id);
        Assertions.assertEquals("2_1_2", ((LowClass) cache.get("2_1_2").get()).id);
        Assertions.assertEquals("2_2_1", ((LowClass) cache.get("2_2_1").get()).id);
        Assertions.assertEquals("2_2_2", ((LowClass) cache.get("2_2_2").get()).id);
    }
}


@Service
class FlatCachePutDataService {
    @Autowired
    DataPopulatorService dataPopulatorService;

    @FlatCachePut(cacheName = "topByLowId", key = "#lowClassIds",
            mapping = @FieldMapping(keyField = "", valueField = "midClasses.lowClasses.id",
                    cardinality = MappingCardinality.SINGLE))
    public List<TopClass> findTopClassesByLowClassIds(List<String> lowClassIds) {
        return dataPopulatorService.findTopClassesByLowClassIds(lowClassIds);
    }

    @FlatCachePut(cacheName = "topByLowId2", key = "#lowClassIds",
            mapping = @FieldMapping(keyField = "", valueField = "midClasses.lowClasses.id",
                    cardinality = MappingCardinality.SINGLE))
    public List<TopClass> findTopClassesByLowClassIds2(List<String> lowClassIds) {
        return dataPopulatorService.findTopClassesByLowClassIds(lowClassIds);
    }

    @FlatCachePut(cacheName = "topByLowId3", key = "#lowClasses.id",
            mapping = @FieldMapping(keyField = "midClass.topClass.id", valueField = "id",
                    cardinality = MappingCardinality.SINGLE))
    public List<TopClass> findTopClassesByLowClasses(List<LowClass> lowClasses) {
        return lowClasses.stream()
                .map(lc -> lc.midClass.topClass)
                .distinct()
                .collect(Collectors.toList());
    }

    @FlatCachePut(cacheName = "lowsByTopId", key = "#topClassIds",
            mapping = @FieldMapping(keyField = "", valueField = "midClass.topClass.id",
                    cardinality = MappingCardinality.MULTIPLE))
    public List<LowClass> findLowClassesByTopClassIds(List<String> topClassIds) {
        return dataPopulatorService.findLowClassesByTopClassIds(topClassIds);
    }

    @FlatCachePut(cacheName = "lowsByTopId2", key = "#topClasses.id",
            mapping = @FieldMapping(keyField = "midClasses.lowClasses.id", valueField = "id",
                    cardinality = MappingCardinality.MULTIPLE))
    public List<LowClass> findLowClassesByTopClasses(List<TopClass> topClasses) {
        return topClasses.stream()
                .flatMap(t -> t.midClasses.stream())
                .flatMap(m -> m.lowClasses.stream())
                .collect(Collectors.toList());
    }

    @FlatCachePut(cacheName = "lowById", key = "#result.id")
    public List<LowClass> findAllLowClasses() {
        return dataPopulatorService.findAllLowClasses();
    }
}
