package deepend0.springcacheext.flatcacheable.aspect;

import deepend0.springcacheext.flatcacheable.MappingCardinality;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class FlatCacheSaveOperation extends FlatCacheOperation {
    public static Map<?, ?> generateKeyValueMapByJoiningKeyValueObjects(Object keyObject, Collection<Object> newValues, String[] keyFields, String[] keyMappingFields, String[] valueMappingFields, MappingCardinality cardinality) {
        Map<?, List<Object>> joinFieldKeyObjectMap = associateFlatWithNestedFieldMultiple(keyObject, keyMappingFields);
        Map<?, List<Object>> joinFieldValueObjectMap = associateFlatWithNestedFieldMultiple(newValues, valueMappingFields);
        Stream<Map.Entry<?, ?>> keyValueStream = joinFieldKeyObjectMap.entrySet().stream()
                .flatMap(jfkoEntry ->
                        jfkoEntry.getValue().stream()
                                .map(ko ->
                                        new AbstractMap.SimpleEntry<>(
                                                getNestedFieldValues(ko, keyFields).get(0),
                                                joinFieldValueObjectMap.get(jfkoEntry.getKey()))))
                .flatMap(kvos -> kvos.getValue().stream()
                        .map(vo -> new AbstractMap.SimpleEntry<>(kvos.getKey(), vo)));
        Map<?, ?> keyValueMap;
        if (cardinality == MappingCardinality.SINGLE) {
            keyValueMap = keyValueStream.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        } else {
            keyValueMap = keyValueStream
                    .collect(Collectors.groupingBy(e -> e.getKey(), Collectors.mapping(e -> e.getValue(),
                            Collectors.toList())));
        }
        return keyValueMap;
    }

    public static Collection<Object> createDefaultCollectionInstance(Class<? extends Collection> collectionClass) {
        if (List.class.isAssignableFrom(collectionClass)) {
            return new ArrayList<>();
        } else if (Set.class.isAssignableFrom(collectionClass)) {
            return new HashSet<>();
        }
        throw new IllegalArgumentException("collection unknown");
    }

    public static Map<?, List<Object>> associateFlatWithNestedFieldMultiple(Object arg, String[] fields) {
        Stream<? extends Map.Entry<Object, Object>> stream = associateFlatWithNestedFieldStream(arg, fields);
        return stream.collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(e -> e.getValue(), Collectors.toList())));
    }

    public static Map<?, Object> associateFlatWithNestedFieldSingle(Object arg, String[] fields) {
        Stream<? extends Map.Entry<Object, Object>> stream = associateFlatWithNestedFieldStream(arg, fields);
        return stream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Stream<? extends Map.Entry<Object, Object>> associateFlatWithNestedFieldStream(Object arg, String[] fields) {
        Stream<? extends Map.Entry<Object, Object>> stream;

        if (arg instanceof Collection) {
            stream = ((Collection) arg).stream().map(o -> new AbstractMap.SimpleEntry<>(o, o));
        } else {
            stream = Stream.of(new AbstractMap.SimpleEntry<>(arg, arg));
        }

        for (String fieldName : fields) {
            stream = stream.flatMap(entry -> {
                Object key = entry.getKey();
                Object val = getFieldValue(key, fieldName);

                if (val instanceof Collection) {
                    return ((Collection<?>) val).stream().map(e -> new AbstractMap.SimpleEntry<>(e, entry.getValue()));
                } else {
                    return Stream.of(new AbstractMap.SimpleEntry<>(val, entry.getValue()));
                }
            });
        }
        return stream;
    }
}
