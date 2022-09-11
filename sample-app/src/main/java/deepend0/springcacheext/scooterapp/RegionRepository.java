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

public interface RegionRepository extends CrudRepository<Region, String> {
    @Cacheable(cacheNames = "regionById", key = "#id")
    Optional<Region> findById(String id);

    @FlatCachePut(cacheName = "regionById", key = "#result.id")
    List<Region> findAll();

    @Cacheable(cacheNames = "regionByPolygonId", key = "#polygonId")
    Region findByPolygons_Id(String polygonId);

    @FlatCacheable(cacheName = "regionByPolygonId", key = "#polygonIds",
            mapping = @FieldMapping(valueField = "polygons.id",
                    cardinality = MappingCardinality.SINGLE))
    List<Region> findByPolygons_IdIn(List<String> polygonIds);

    @FlatCacheable(cacheName = "regionByPolygonId", key = "#polygons.id",
            mapping = @FieldMapping(keyField="id", valueField = "polygons.id",
                    cardinality = MappingCardinality.SINGLE))
    List<Region> findByPolygonsIn(List<Polygon> polygons);

    @Cacheable(cacheNames = "regionByScooterId", key = "#scooterId")
    Region findByPolygons_Scooters_Id(String scooterId);

    @FlatCacheable(cacheName = "regionByScooterId", key = "#scooterIds",
            mapping = @FieldMapping(valueField = "polygons.scooters.id",
                    cardinality = MappingCardinality.SINGLE))
    List<Region> findByPolygons_Scooters_IdIn(List<String> scooterIds);

    @FlatCacheable(cacheName = "regionByScooterId", key = "#scooters.id",
            mapping = @FieldMapping(keyField = "id", valueField = "polygons.scooters.id",
                    cardinality = MappingCardinality.SINGLE))
    List<Region> findByPolygons_ScootersIn(List<Scooter> scooters);

    @FlatCacheEvict(cacheName = "regionById", key = "#ids")
    void deleteAllByIdIn(List<String> ids);
}
