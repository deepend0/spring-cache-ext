package deepend0.springcacheext.scooterapp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class RegionRepositoryTest {
    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeAll
    public void init() {
        Region region1 = new Region("1", "region 1");
        Region region2 = new Region("2", "region 2");
        List<Region> regions = List.of(region1, region2);

        Polygon polygon1_1 = new Polygon("1_1");
        Polygon polygon1_2 = new Polygon("1_2");
        region1.setPolygons(List.of(polygon1_1, polygon1_2));
        Polygon polygon2_1 = new Polygon("2_1");
        Polygon polygon2_2 = new Polygon("2_2");
        region2.setPolygons(List.of(polygon2_1, polygon2_2));

        Scooter scooter1_1_1 = new Scooter("1_1_1","S1", Scooter.Status.STEADY);
        Scooter scooter1_1_2 = new Scooter("1_1_2", "S1", Scooter.Status.BATTERY_EMPTY);
        polygon1_1.setScooters(List.of(scooter1_1_1, scooter1_1_2));
        Scooter scooter1_2_1 = new Scooter("1_2_1", "S2", Scooter.Status.STEADY);
        Scooter scooter1_2_2 = new Scooter("1_2_2", "S2", Scooter.Status.BROKEN);
        polygon1_2.setScooters(List.of(scooter1_2_1, scooter1_2_2));
        Scooter scooter2_1_1 = new Scooter("2_1_1", "S3", Scooter.Status.STEADY);
        Scooter scooter2_1_2 = new Scooter("2_1_2", "S1", Scooter.Status.BROKEN);
        polygon2_1.setScooters(List.of(scooter2_1_1, scooter2_1_2));
        Scooter scooter2_2_1 = new Scooter("2_2_1", "S4", Scooter.Status.BROKEN);
        Scooter scooter2_2_2 = new Scooter("2_2_2", "S4", Scooter.Status.BATTERY_EMPTY);
        polygon2_2.setScooters(List.of(scooter2_2_1, scooter2_2_2));

        regionRepository.saveAll(regions);
    }

    @Test
    public void findAllShouldBeCached() {
        Cache cache = cacheManager.getCache("regionById");
        cache.clear();
        regionRepository.findAll();
        Assertions.assertEquals("1", ((Region)cache.get("1").get()).getId());
        Assertions.assertEquals("2", ((Region)cache.get("2").get()).getId());
    }

    @Test
    public void findByPolygonIdsShouldBeCached() {
        Cache cache = cacheManager.getCache("regionByPolygonId");
        cache.clear();
        regionRepository.findByPolygons_IdIn(List.of("1_1", "2_2"));
        Assertions.assertEquals("1", ((Region)cache.get("1_1").get()).getId());
        Assertions.assertNull(cache.get("1_2"));
        Assertions.assertNull(cache.get("2_1"));
        Assertions.assertEquals("2", ((Region)cache.get("2_2").get()).getId());
    }

    @Test
    public void findByPolygonsShouldBeCached() {
        Cache cache = cacheManager.getCache("regionByPolygonId");
        cache.clear();
        regionRepository.findByPolygonsIn(List.of(new Polygon("1_1"), new Polygon("2_2")));
        Assertions.assertEquals("1", ((Region)cache.get("1_1").get()).getId());
        Assertions.assertNull(cache.get("1_2"));
        Assertions.assertNull(cache.get("2_1"));
        Assertions.assertEquals("2", ((Region)cache.get("2_2").get()).getId());
    }

    @Test
    public void findByScooterIdsShouldBeCached() {
        Cache cache = cacheManager.getCache("regionByScooterId");
        cache.clear();
        regionRepository.findByPolygons_Scooters_IdIn(List.of("1_1_1", "2_2_2"));
        Assertions.assertEquals("1", ((Region)cache.get("1_1_1").get()).getId());
        Assertions.assertNull(cache.get("1_1_2"));
        Assertions.assertNull(cache.get("1_2_1"));
        Assertions.assertNull(cache.get("1_2_2"));
        Assertions.assertNull(cache.get("2_1_1"));
        Assertions.assertNull(cache.get("2_1_2"));
        Assertions.assertNull(cache.get("2_2_1"));
        Assertions.assertEquals("2", ((Region)cache.get("2_2_2").get()).getId());
    }

    @Test
    public void findByScootersShouldBeCached() {
        Cache cache = cacheManager.getCache("regionByScooterId");
        cache.clear();
        regionRepository.findByPolygons_ScootersIn(List.of(new Scooter("1_1_1"), new Scooter("2_2_2")));
        Assertions.assertEquals("1", ((Region)cache.get("1_1_1").get()).getId());
        Assertions.assertNull(cache.get("1_1_2"));
        Assertions.assertNull(cache.get("1_2_1"));
        Assertions.assertNull(cache.get("1_2_2"));
        Assertions.assertNull(cache.get("2_1_1"));
        Assertions.assertNull(cache.get("2_1_2"));
        Assertions.assertNull(cache.get("2_2_1"));
        Assertions.assertEquals("2", ((Region)cache.get("2_2_2").get()).getId());
    }

    @Test
    public void deleteByIdsShoudBeEvictedFromCache() {
        Cache cache = cacheManager.getCache("regionById");
        cache.clear();
        cache.put("1", new Region("1"));
        cache.put("2", new Region("2"));
        regionRepository.deleteAllByIdIn(List.of("1", "2"));
        Assertions.assertNull(cache.get("1"));
        Assertions.assertNull(cache.get("2"));
    }
}
