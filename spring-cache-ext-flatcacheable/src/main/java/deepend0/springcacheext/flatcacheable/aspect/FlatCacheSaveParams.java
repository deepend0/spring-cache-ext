package deepend0.springcacheext.flatcacheable.aspect;

import deepend0.springcacheext.flatcacheable.MappingCardinality;

public class FlatCacheSaveParams extends FlatCacheParams{
    private String [] mappingKeyField;
    private String [] mappingValueField;
    private MappingCardinality cardinality;

    public String[] getMappingKeyField() {
        return mappingKeyField;
    }

    public void setMappingKeyField(String[] mappingKeyField) {
        this.mappingKeyField = mappingKeyField;
    }

    public String[] getMappingValueField() {
        return mappingValueField;
    }

    public void setMappingValueField(String[] mappingValueField) {
        this.mappingValueField = mappingValueField;
    }

    public MappingCardinality getCardinality() {
        return cardinality;
    }

    public void setCardinality(MappingCardinality cardinality) {
        this.cardinality = cardinality;
    }
}
