package hu.webarticum.miniconnect.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.record.ResultRecord;
import hu.webarticum.miniconnect.record.ResultTable;
import hu.webarticum.miniconnect.rest.crud.EntityCrud;
import hu.webarticum.miniconnect.rest.crud.EntityCrudStrategy;
import hu.webarticum.miniconnect.rest.query.EntityListQuery;
import hu.webarticum.miniconnect.rest.query.EntityListQueryExecutor;
import hu.webarticum.miniconnect.rest.query.EntityListQueryExecutorStrategy;
import hu.webarticum.miniconnect.rest.schema.RestSchema;
import hu.webarticum.miniconnect.rest.schema.RestSchemaResource;
import hu.webarticum.miniconnect.rest.schema.RestSchemaResourceAssociation;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.exceptions.HttpStatusException;

@Controller("/tables")
public class TableController {
    
    private final MiniSession session;

    private final RestSchema schema;
    
    private final EntityListQueryExecutorStrategy queryExecutorStrategy;
    
    private final EntityCrudStrategy crudStrategy;
    
    
    public TableController(
            MiniSession session,
            RestSchema schema,
            EntityListQueryExecutorStrategy queryExecutorStrategy,
            EntityCrudStrategy crudStrategy) {
        this.session = session;
        this.schema = schema;
        this.queryExecutorStrategy = queryExecutorStrategy;
        this.crudStrategy = crudStrategy;
    }

    @Get("/")
    public Map<String, List<String>> handle() {
        List<String> resources = new ArrayList<>();
        schema.resources().forEach((name, resource) -> {
            String listUrl = "/" + name;
            String itemUrl = buildItemUrlTemplate(listUrl, resource.primaryKey());
            resources.add(listUrl);
            if (itemUrl != null) {
                resources.add(itemUrl);
            }
            addAssociatedResources(itemUrl, resource, resources);
        });
        Collections.sort(resources);
        Map<String, List<String>> result = new HashMap<>();
        result.put("resources", resources);
        return result;
    }
    
    private void addAssociatedResources(
            String prefix, RestSchemaResource resource, List<String> target) {
        for (Map.Entry<String, RestSchemaResourceAssociation> entry :
                resource.childResources().entrySet()) {
            String name = entry.getKey();
            RestSchemaResourceAssociation association = entry.getValue();
            RestSchemaResource subResource = association.resource();
            String subListUrl = prefix + "/" + name;
            String subItemUrl = buildItemUrlTemplate(subListUrl, subResource.primaryKey());
            target.add(subListUrl);
            if (subItemUrl != null) {
                target.add(subItemUrl);
            }
            addAssociatedResources(subItemUrl, subResource, target);
        }
    }
    
    private String buildItemUrlTemplate(String prefix, ImmutableList<String> primaryKey) {
        if (primaryKey.isEmpty()) {
            return null;
        }
        
        StringBuilder resultBuilder = new StringBuilder(prefix);
        resultBuilder.append("/{");
        boolean first = true;
        for (String columnName : primaryKey) {
            if (first) {
                first = false;
            } else {
                resultBuilder.append("},{");
            }
            resultBuilder.append(columnName);
        }
        resultBuilder.append('}');
        return resultBuilder.toString();
    }

    @Get("{/prefix:[^/]+/[^/]+(/[^/]+/[^/]+)*}/{name}")
    public Object handleList(
            @PathVariable("prefix") @Nullable String prefix,
            @PathVariable("name") @NonNull String name) {
        List<Pair<String, ImmutableList<String>>> pathItems = new ArrayList<>();
        if (prefix != null) { // NOSONAR can not be null
            pathItems.addAll(splitPath(prefix));
        }
        List<RestSchemaResource> pathResources = resolveEntityPath(pathItems);
        validateEntityPath(pathItems, pathResources);

        RestSchemaResource resource;
        if (pathResources.isEmpty()) {
            resource = schema.resources().get(name);
        } else {
            RestSchemaResource lastResource = pathResources.get(pathResources.size() - 1);
            RestSchemaResourceAssociation tailAssociation = lastResource.childResources().get(name);
            if (tailAssociation == null) {
                throw new IllegalArgumentException("No such resource");
            }
            resource = tailAssociation.resource();
        }
        String tableName = resource.tableName();
        
        List<Map<String, Object>> result = new ArrayList<>();
        EntityListQuery query = new EntityListQuery();
        EntityListQueryExecutor queryExecutor = queryExecutorStrategy.createFor(session, tableName);
        MiniResult dbResult = queryExecutor.execute(query);
        try (MiniResultSet resultSet = dbResult.resultSet()) {
            for (ResultRecord resultRecord : new ResultTable(resultSet)) {
                Map<String, Object> entityData =  resultRecord.rowMap().asMap();
                result.add(entityData);
            }
        }
        return result;
    }

    @Get("{/prefix:[^/]+/[^/]+(/[^/]+/[^/]+)*}/{name}/{id}")
    public Map<String, Object> handleEntity(
            @PathVariable("prefix") @Nullable String prefix,
            @PathVariable("name") @NonNull String name,
            @PathVariable("id") @NonNull String id) {
        List<Pair<String, ImmutableList<String>>> pathItems = new ArrayList<>();
        if (prefix != null) { // NOSONAR can not be null
            pathItems.addAll(splitPath(prefix));
        }
        pathItems.add(Pair.of(name, splitId(id)));
        List<RestSchemaResource> pathResources = resolveEntityPath(pathItems);
        validateEntityPath(pathItems, pathResources);
        
        int lastIndex = pathItems.size() - 1;
        ImmutableList<String> key = pathItems.get(lastIndex).getRight();
        RestSchemaResource lastResource = pathResources.get(lastIndex);
        EntityCrud crud = crudStrategy.createFor(
                session, lastResource.tableName(), lastResource.primaryKey());
        MiniResult dbResult = crud.read(key).requireSuccess();
        try (MiniResultSet resultSet = dbResult.resultSet()) {
            for (ResultRecord resultRecord : new ResultTable(resultSet)) {
                return resultRecord.rowMap().asMap(); // NOSONAR loop is OK
            }
        }
        throw new HttpStatusException(HttpStatus.NOT_FOUND, "No such entity");
    }
    
    private List<Pair<String, ImmutableList<String>>> splitPath(String path) {
        String[] tokens = path.split("/");
        List<Pair<String, ImmutableList<String>>> result = new ArrayList<>(tokens.length);
        for (int i = 0; i < tokens.length; i += 2) {
            String name = decodeUriComponent(tokens[i]);
            String id = decodeUriComponent(tokens[i + 1]);
            ImmutableList<String> key = splitId(id);
            result.add(Pair.of(name, key));
        }
        return result;
    }
    
    private String decodeUriComponent(String token) {
        
        // TODO
        return token;
        
    }

    private ImmutableList<String> splitId(String id) {
        
        // TODO: proper splitting wit escape support
        return ImmutableList.of(id.split(","));
        
    }

    private void validateEntityPath(
            List<Pair<String, ImmutableList<String>>> pathItems,
            List<RestSchemaResource> pathResources) {
        
        // TODO
        
    }

    private List<RestSchemaResource> resolveEntityPath(
            List<Pair<String, ImmutableList<String>>> pathItems) {
        int size = pathItems.size();
        List<RestSchemaResource> result = new ArrayList<>();
        if (size == 0) {
            return result;
        }
        
        String firstName = pathItems.get(0).getLeft();
        RestSchemaResource resource = schema.resources().get(firstName);
        result.add(resource);
        for (int i = 1; i < size; i++) {
            String nextName = pathItems.get(i).getLeft();
            RestSchemaResourceAssociation nextAssociation =
                    resource.childResources().get(nextName);
            if (nextAssociation == null) {
                throw new IllegalArgumentException("Invalid path");
            }
            resource = nextAssociation.resource();
            result.add(resource);
        }
        return result;
    }

}
