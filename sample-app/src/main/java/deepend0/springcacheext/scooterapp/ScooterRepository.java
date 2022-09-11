package deepend0.springcacheext.scooterapp;

import deepend0.springcacheext.flatcacheable.FieldMapping;
import deepend0.springcacheext.flatcacheable.FlatCacheEvict;
import deepend0.springcacheext.flatcacheable.FlatCachePut;
import deepend0.springcacheext.flatcacheable.FlatCacheable;
import deepend0.springcacheext.flatcacheable.MappingCardinality;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ScooterRepository extends CrudRepository<Scooter, String> {
    @Cacheable(cacheNames = "scooterById", key = "#id")
    Optional<Scooter> findById(String id);

    @FlatCachePut(cacheName = "scooterById", key = "#result.id")
    List<Scooter> findAll();

    @Cacheable(cacheNames = "scootersByPolygonId", key = "#polygonId")
    List<Scooter> findByPolygon_Id(String polygonId);

    @FlatCacheable(cacheName = "scootersByPolygonId", key = "#polygonIds",
            mapping = @FieldMapping(valueField = "polygon.id",
                    cardinality = MappingCardinality.MULTIPLE))
    Set<Scooter> findByPolygon_IdIn(List<String> polygonIds);

    @FlatCacheable(cacheName = "scootersByPolygonId", key = "#polygons.id",
            mapping = @FieldMapping(keyField="id", valueField = "polygon.id",
                    cardinality = MappingCardinality.MULTIPLE))
    Set<Scooter> findByPolygonIn(List<Polygon> polygons);

    @Cacheable(cacheNames = "scootersByRegionId", key = "#regionId")
    List<Scooter> findByPolygon_Region_Id(String regionId);

    @FlatCacheable(cacheName = "scootersByRegionId", key = "#regionIds",
            mapping = @FieldMapping(valueField = "polygon.region.id",
                    cardinality = MappingCardinality.MULTIPLE))
    List<Scooter> findByPolygon_Region_IdIn(List<String> regionIds);

    @FlatCacheable(cacheName = "scootersByRegionId", key = "#regions.id",
            mapping = @FieldMapping(keyField = "id", valueField = "polygon.region.id",
                    cardinality = MappingCardinality.MULTIPLE))
    List<Scooter> findByPolygon_RegionIn(List<Region> regions);

    @FlatCacheEvict(cacheName = "scooterById", key = "#ids")
    void deleteAllByIdIn(List<String> ids);
}
