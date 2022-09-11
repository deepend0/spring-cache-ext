package deepend0.springcacheext.flatcacheable.aspect;

public class FlatCacheParams {
    private String cacheName;
    private String keyArgument;
    private String [] keyField;

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getKeyArgument() {
        return keyArgument;
    }

    public void setKeyArgument(String keyArgument) {
        this.keyArgument = keyArgument;
    }

    public String[] getKeyField() {
        return keyField;
    }

    public void setKeyField(String[] keyField) {
        this.keyField = keyField;
    }
}
