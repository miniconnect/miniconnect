package hu.webarticum.miniconnect.rest.schema;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;

import hu.webarticum.miniconnect.lang.ImmutableMap;

public class RestSchema {

    private final ImmutableMap<String, RestSchemaResource> resources;
    
    
    public RestSchema(ImmutableMap<String, RestSchemaResource> resources) {
        this.resources = resources;
    }
    

    public ImmutableMap<String, RestSchemaResource> resources() {
        return resources;
    }

    @JsonGetter("resources")
    public Map<String, RestSchemaResource> resourcesAsMap() {
        return resources().asMap();
    }
    
}
