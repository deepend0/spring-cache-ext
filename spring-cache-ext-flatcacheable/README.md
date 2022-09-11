## Spring Cache Extension - FlatCacheable

Spring Cache abstraction currently doesn't support working iteratively with collection arguments. Some operations like fetching data from a database, network etc. can only handle collections effectively. It is still possible caching them via creating a singular cache method and iterating through outcome collection. However, that is a setback for having a good cache abstraction. For that, handling collections are important part for caching abstractions. 

FlatCacheable is an extension for handling collections feasibly in Spring context. This is a small package composed of a few annotations and their implementations with AOP. There are 3 annotations which are "flat" correspondents of standard Spring Cache method annotations:
```
- FlatCacheable
- FlatCachePut
- FlatCacheEvict
```

It is attributed with "flat" because it, rhetorically, handles collections not as a whole but flattens them. Also, the internal implementation makes use of flattening operation (namely Stream.flatMap) quite a lot.

## Usage

First, its context configuration should be imported, caching and AspectJ proxies should be enabled:
```
@SpringBootApplication
@EnableCaching
@EnableAspectJAutoProxy
@Import(FlatCacheableContextConfig.class)
public class SampleApplication {
	public static void main(String[] args) {
		SpringApplication.run(SampleApplication.class, args);
	}
}
```

Then, you can annotate methods with `@FlatCacheable`, `@FlatCachePut` or `@FlatCacheEvict` annotations.

`@FlatCacheable` does the computation for an object in a collection argument and adds the computed value into the cache, only if it doesn't exist in the cache. Otherwise, computation will not be done for that object. A key can be extracted from each input object using dotted field notation. Key field notation should contain the argument name and sub-fields if any. Besides, objects in input and output collections must be mapped. That can be done by specifying only sub-fields in corresponding object for both input and output objects as join point. In addition, mapping cardinality should be specified. If only a single value corresponds to a key then `MappingCardinality.SINGLE` or else `MappingCardinality.MULTIPLE` should be given as enum value.

`@FlatCachePut` does the computation for an object in a collection argument and adds the computed value into the cache no matter what. So existing values will be overwritten in the cache. Key and field mapping can be specified in the same way as `@FlatCacheable`. In addition, keys can also be specified from value object using `#result` context object and then sub-fields. If keys are also selected from output object, no mapping between input and output collections is required. Even no input object is mandatory in this case. 

`@FlatCacheEvict` evicts values from the cache based on either input or output objects. Computation is done no matter key exists in cache or not. If key is to be extracted from output object, again, `#result` context object should be specified. In this case, input objects are not mandatory as well.

The example below demonstrates general usage of annotations via a Spring JPA repository use case:

```
public interface EntityRepository extends CrudRepository<Entity, String> {
    @Cacheable(cacheNames = "entityById", key = "#id")
    Optional<Entity> findById(String id);

    @FlatCachePut(cacheName = "entityById", key = "#result.id")
    List<Entity> findAll();

    @Cacheable(cacheNames = "entityBySubentityId", key = "#subentityId")
    Entity findBySubentitys_Id(String subentityId);

    @FlatCacheable(cacheName = "entityBySubentityId", key = "#subentityIds",
            mapping = @FieldMapping(valueField = "subentitys.id",
                    cardinality = MappingCardinality.SINGLE))
    List<Entity> findBySubentitys_IdIn(List<String> subentityIds);

    @FlatCacheable(cacheName = "entityBySubentityId", key = "#subentitys.id",
            mapping = @FieldMapping(keyField="id", valueField = "subentitys.id",
                    cardinality = MappingCardinality.SINGLE))
    List<Entity> findBySubentitysIn(List<Subentity> subentitys);

    @Cacheable(cacheNames = "entityBySubsubentityId", key = "#subsubentityId")
    Entity findBySubentitys_Subsubentitys_Id(String subsubentityId);

    @FlatCacheable(cacheName = "entityBySubsubentityId", key = "#subsubentityIds",
            mapping = @FieldMapping(valueField = "subentitys.subsubentitys.id",
                    cardinality = MappingCardinality.SINGLE))
    List<Entity> findBySubentitys_Subsubentitys_IdIn(List<String> subsubentityIds);

    @FlatCacheable(cacheName = "entityBySubsubentityId", key = "#subsubentitys.id",
            mapping = @FieldMapping(keyField = "id", valueField = "subentitys.subsubentitys.id",
                    cardinality = MappingCardinality.SINGLE))
    List<Entity> findBySubentitys_SubsubentitysIn(List<Subsubentity> subsubentitys);

    @FlatCacheEvict(cacheName = "entityById", key = "#ids")
    void deleteAllByIdIn(List<String> ids);
}
```

In the use case above, an entity has many sub-entities and a subentity has many sub-sub-entities. Single and multi-level mapping is demonstrated with this example.
 
## Authors

- Orkun Akile - orkun.akile@gmail.com

## Version History

* 1.0.0
    * Initial Release