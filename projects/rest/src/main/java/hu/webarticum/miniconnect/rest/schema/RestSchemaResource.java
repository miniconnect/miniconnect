package hu.webarticum.miniconnect.rest.schema;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;

public class RestSchemaResource {
    
    private final String tableName;
    
    private final ImmutableList<String> primaryKey;
    
    private final ImmutableMap<String, RestSchemaResourceAssociation> childResources;
    

    public RestSchemaResource(
            String tableName,
            ImmutableList<String> primaryKey,
            ImmutableMap<String, RestSchemaResourceAssociation> childResources) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.childResources = childResources;
    }
    

    @JsonGetter("tableName")
    public String tableName() {
        return tableName;
    }

    public ImmutableList<String> primaryKey() {
        return primaryKey;
    }

    @JsonGetter("primaryKey")
    public List<String> primaryKeyAsList() {
        return primaryKey().asList();
    }

    public ImmutableMap<String, RestSchemaResourceAssociation> childResources() {
        return childResources;
    }

    @JsonGetter("childResources")
    public Map<String, RestSchemaResourceAssociation> childResourcesAsMap() {
        return childResources().asMap();
    }
    
}
