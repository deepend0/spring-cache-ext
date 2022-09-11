package deepend0.springcacheext.flatcacheable;

public @interface FieldMapping {
    MappingCardinality cardinality() default MappingCardinality.SINGLE;
    String keyField() default "";
    String valueField() default "";
}
