package hu.webarticum.miniconnect.rest.schema;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;

import hu.webarticum.miniconnect.lang.ImmutableList;

public class RestSchemaResourceAssociation {
    
    private final ImmutableList<String> uniqueKey;
    
    private final ImmutableList<String> childKey;
    
    private final RestSchemaResource resource;
    

    public RestSchemaResourceAssociation(
            ImmutableList<String> uniqueKey,
            ImmutableList<String> childKey,
            RestSchemaResource resource) {
        this.uniqueKey = uniqueKey;
        this.childKey = childKey;
        this.resource = resource;
    }
    

    public ImmutableList<String> uniqueKey() {
        return uniqueKey;
    }

    @JsonGetter("uniqueKey")
    public List<String> uniqueKeyAsList() {
        return uniqueKey().asList();
    }

    public ImmutableList<String> childKey() {
        return childKey;
    }

    @JsonGetter("childKey")
    public List<String> childKeyAsList() {
        return childKey().asList();
    }

    @JsonGetter("resource")
    public RestSchemaResource resource() {
        return resource;
    }
    
}
