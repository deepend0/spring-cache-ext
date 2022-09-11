package deepend0.springcacheext.scooterapp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
public class ScooterRepositoryTest {
    @Autowired
    private ScooterRepository scooterRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void init() {
        Region region1 = new Region("1", "region 1");
        Region region2 = new Region("2", "region 2");

        Polygon polygon1_1 = new Polygon("1_1");
        polygon1_1.setRegion(region1);
        Polygon polygon1_2 = new Polygon("1_2");
        polygon1_2.setRegion(region1);
        Polygon polygon2_1 = new Polygon("2_1");
        polygon2_1.setRegion(region2);
        Polygon polygon2_2 = new Polygon("2_2");
        polygon2_2.setRegion(region2);

        Scooter scooter1_1_1 = new Scooter("1_1_1", "S1", Scooter.Status.STEADY);
        scooter1_1_1.setPolygon(polygon1_1);
        Scooter scooter1_1_2 = new Scooter("1_1_2", "S1", Scooter.Status.BATTERY_EMPTY);
        scooter1_1_2.setPolygon(polygon1_1);
        Scooter scooter1_2_1 = new Scooter("1_2_1", "S2", Scooter.Status.STEADY);
        scooter1_2_1.setPolygon(polygon1_2);
        Scooter scooter1_2_2 = new Scooter("1_2_2", "S2", Scooter.Status.BROKEN);
        scooter1_2_2.setPolygon(polygon1_2);
        Scooter scooter2_1_1 = new Scooter("2_1_1", "S3", Scooter.Status.STEADY);
        scooter2_1_1.setPolygon(polygon2_1);
        Scooter scooter2_1_2 = new Scooter("2_1_2", "S1", Scooter.Status.BROKEN);
        scooter2_1_2.setPolygon(polygon2_1);
        Scooter scooter2_2_1 = new Scooter("2_2_1", "S4", Scooter.Status.BROKEN);
        scooter2_2_1.setPolygon(polygon2_2);
        Scooter scooter2_2_2 = new Scooter("2_2_2", "S4", Scooter.Status.BATTERY_EMPTY);
        scooter2_2_2.setPolygon(polygon2_2);

        scooterRepository.saveAll(List.of(scooter1_1_1, scooter1_1_2, scooter1_2_1, scooter1_2_2,
                scooter2_1_1, scooter2_1_2, scooter2_2_1, scooter2_2_2));
    }

    @Test
    public void findAllShouldBeCached() {
        scooterRepository.findAll();
        Cache cache = cacheManager.getCache("scooterById");
        Assertions.assertEquals("1_1_1", ((Scooter)cache.get("1_1_1").get()).getId());
        Assertions.assertEquals("1_1_2", ((Scooter)cache.get("1_1_2").get()).getId());
        Assertions.assertEquals("1_2_1", ((Scooter)cache.get("1_2_1").get()).getId());
        Assertions.assertEquals("1_2_2", ((Scooter)cache.get("1_2_2").get()).getId());
        Assertions.assertEquals("2_1_1", ((Scooter)cache.get("2_1_1").get()).getId());
        Assertions.assertEquals("2_1_2", ((Scooter)cache.get("2_1_2").get()).getId());
        Assertions.assertEquals("2_2_1", ((Scooter)cache.get("2_2_1").get()).getId());
        Assertions.assertEquals("2_2_2", ((Scooter)cache.get("2_2_2").get()).getId());
        Assertions.assertNull(cache.get("3_1_1"));
    }

    @Test
    public void findByPolygonIdsShouldBeCached() {
        Cache cache = cacheManager.getCache("scootersByPolygonId");
        cache.clear();
        scooterRepository.findByPolygon_IdIn(List.of("1_1", "2_2"));
        List<Scooter> scootersOfPolygon1_1 = (List<Scooter>) cache.get("1_1").get();
        Assertions.assertEquals(2, scootersOfPolygon1_1.size());
        Assertions.assertNull(cache.get("1_2"));
        Assertions.assertNull(cache.get("2_1"));
        List<Scooter> scootersOfPolygon2_2 = (List<Scooter>) cache.get("2_2").get();
        Assertions.assertEquals(2, scootersOfPolygon2_2.size());
    }

    @Test
    public void findByPolygonsShouldBeCached() {
        Cache cache = cacheManager.getCache("scootersByPolygonId");
        cache.clear();
        scooterRepository.findByPolygonIn(List.of(new Polygon("1_1"), new Polygon("2_2")));
        List<Scooter> scootersOfPolygon1_1 = (List<Scooter>) cache.get("1_1").get();
        Assertions.assertEquals(2, scootersOfPolygon1_1.size());
        Assertions.assertNull(cache.get("1_2"));
        Assertions.assertNull(cache.get("2_1"));
        List<Scooter> scootersOfPolygon2_2 = (List<Scooter>) cache.get("2_2").get();
        Assertions.assertEquals(2, scootersOfPolygon2_2.size());
    }

    @Test
    public void findByRegionIdsShouldBeCached() {
        Cache cache = cacheManager.getCache("scootersByRegionId");
        cache.clear();
        scooterRepository.findByPolygon_Region_IdIn(List.of("1", "2"));
        List<Scooter> scootersOfRegion1 = (List<Scooter>) cache.get("1").get();
        Assertions.assertEquals(4, scootersOfRegion1.size());
        List<Scooter> scootersOfRegion2 = (List<Scooter>) cache.get("2").get();
        Assertions.assertEquals(4, scootersOfRegion2.size());
    }

    @Test
    public void findByRegionsShouldBeCached() {
        Cache cache = cacheManager.getCache("scootersByRegionId");
        cache.clear();
        scooterRepository.findByPolygon_RegionIn(List.of(new Region("1"), new Region("2")));
        List<Scooter> scootersOfRegion1 = (List<Scooter>) cache.get("1").get();
        Assertions.assertEquals(4, scootersOfRegion1.size());
        List<Scooter> scootersOfRegion2 = (List<Scooter>) cache.get("2").get();
        Assertions.assertEquals(4, scootersOfRegion2.size());
    }

    @Test
    public void deleteByIdsShoudBeEvictedFromCache() {
        Cache cache = cacheManager.getCache("scooterById");
        cache.clear();
        cache.put("1_1_1", new Scooter("1_1_1"));
        cache.put("2_1_1", new Scooter("2_1_1"));
        scooterRepository.deleteAllByIdIn(List.of("1_1_1", "2_1_1"));
        Assertions.assertNull(cache.get("1_1_1"));
        Assertions.assertNull(cache.get("2_1_1"));
    }
}