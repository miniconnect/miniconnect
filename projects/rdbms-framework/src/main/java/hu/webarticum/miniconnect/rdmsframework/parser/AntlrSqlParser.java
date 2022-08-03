package hu.webarticum.miniconnect.rdmsframework.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.TerminalNode;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.query.DeleteQuery;
import hu.webarticum.miniconnect.rdmsframework.query.InsertQuery;
import hu.webarticum.miniconnect.rdmsframework.query.Queries;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SelectQuery;
import hu.webarticum.miniconnect.rdmsframework.query.SelectVariableQuery;
import hu.webarticum.miniconnect.rdmsframework.query.SetVariableQuery;
import hu.webarticum.miniconnect.rdmsframework.query.ShowSchemasQuery;
import hu.webarticum.miniconnect.rdmsframework.query.ShowTablesQuery;
import hu.webarticum.miniconnect.rdmsframework.query.SpecialCondition;
import hu.webarticum.miniconnect.rdmsframework.query.SelectSpecialQuery;
import hu.webarticum.miniconnect.rdmsframework.query.SpecialSelectableType;
import hu.webarticum.miniconnect.rdmsframework.query.SqlUtil;
import hu.webarticum.miniconnect.rdmsframework.query.UpdateQuery;
import hu.webarticum.miniconnect.rdmsframework.query.UseQuery;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryLexer;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.DeleteQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.FieldListContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.FieldNameContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.IdentifierContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.InsertQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.LikePartContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.LimitPartContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.NullableValueContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.OrderByItemContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.OrderByPartContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.PostfixConditionContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SchemaNameContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.ScopeableFieldNameContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SelectItemContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SelectItemsContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SelectPartContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SelectQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SelectSpecialQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SelectVariableQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SetVariableQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.ShowSchemasQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.ShowTablesQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SpecialSelectableNameContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SqlQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.TableNameContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.UpdateItemContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.UpdatePartContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.UpdateQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.UseQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.ValueContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.ValueListContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.WhereItemContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.WherePartContext;

public class AntlrSqlParser implements SqlParser {

    @Override
    public Query parse(String sql) {
        SqlQueryLexer lexer = new SqlQueryLexer(CharStreams.fromString(sql));
        SqlQueryParser parser = new SqlQueryParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(new ParseErrorListener());
        SqlQueryContext rootNode = parser.sqlQuery();
        return parseRootNode(rootNode);
    }

    private Query parseRootNode(SqlQueryContext rootNode) {
        SelectQueryContext selectQueryNode = rootNode.selectQuery();
        if (selectQueryNode != null) {
            return parseSelectNode(selectQueryNode);
        }

        SelectSpecialQueryContext selectSpecialQueryNode = rootNode.selectSpecialQuery();
        if (selectSpecialQueryNode != null) {
            return parseSelectSpecialNode(selectSpecialQueryNode);
        }

        SelectVariableQueryContext selectVariableQueryNode = rootNode.selectVariableQuery();
        if (selectVariableQueryNode != null) {
            return parseSelectVariableNode(selectVariableQueryNode);
        }
        
        InsertQueryContext insertQueryNode = rootNode.insertQuery();
        if (insertQueryNode != null) {
            return parseInsertNode(insertQueryNode);
        }
        
        UpdateQueryContext updateQueryNode = rootNode.updateQuery();
        if (updateQueryNode != null) {
            return parseUpdateNode(updateQueryNode);
        }
        
        DeleteQueryContext deleteQueryNode = rootNode.deleteQuery();
        if (deleteQueryNode != null) {
            return parseDeleteNode(deleteQueryNode);
        }
        
        ShowSchemasQueryContext showSchemasQueryNode = rootNode.showSchemasQuery();
        if (showSchemasQueryNode != null) {
            return parseShowSchemasNode(showSchemasQueryNode);
        }

        ShowTablesQueryContext showTablesQueryNode = rootNode.showTablesQuery();
        if (showTablesQueryNode != null) {
            return parseShowTablesNode(showTablesQueryNode);
        }

        UseQueryContext useQueryNode = rootNode.useQuery();
        if (useQueryNode != null) {
            return parseUseNode(useQueryNode);
        }
        
        SetVariableQueryContext setVariableNode = rootNode.setVariableQuery();
        if (setVariableNode != null) {
            return parseSetVariableNode(setVariableNode);
        }
        
        throw new IllegalArgumentException("Query type not supported");
    }

    private SelectQuery parseSelectNode(SelectQueryContext selectQueryNode) {
        SelectPartContext selectPartNode = selectQueryNode.selectPart();
        SchemaNameContext schemaNameNode = selectQueryNode.schemaName();
        String schemaName = schemaNameNode != null ?
                parseIdentifierNode(schemaNameNode.identifier()) :
                null;
        IdentifierContext identifierNode = selectQueryNode.tableName().identifier();
        String tableName = parseIdentifierNode(identifierNode);
        String tableAlias = tableName;
        IdentifierContext aliasIdentifierNode = selectQueryNode.tableAlias;
        if (aliasIdentifierNode != null) {
            tableAlias = parseIdentifierNode(aliasIdentifierNode);
        }
        LinkedHashMap<String, String> fields = parseSelectPartNode(selectPartNode, tableAlias);
        WherePartContext wherePartNode = selectQueryNode.wherePart();
        LinkedHashMap<String, Object> where = parseWherePartNode(wherePartNode, tableAlias);
        OrderByPartContext orderByNode = selectQueryNode.orderByPart();
        LinkedHashMap<String, Boolean> orderBy = parseOrderByPartNode(orderByNode, tableAlias);
        LimitPartContext limitPartNode = selectQueryNode.limitPart();
        Integer limit = limitPartNode != null ?
                parseIntegerNode(limitPartNode.TOKEN_INTEGER()) :
                null;
        
        return Queries.select()
                .fields(fields)
                .inSchema(schemaName)
                .from(tableName)
                .where(where)
                .orderBy(orderBy)
                .limit(limit)
                .build();
    }
    
    private SelectSpecialQuery parseSelectSpecialNode(SelectSpecialQueryContext selectSpecialQueryNode) {
        SpecialSelectableNameContext specialSelectableNameNode =
                selectSpecialQueryNode.specialSelectable().specialSelectableName();
        SpecialSelectableType queryType;
        if (specialSelectableNameNode.CURRENT_USER() != null) {
            queryType = SpecialSelectableType.CURRENT_USER;
        } else if (specialSelectableNameNode.CURRENT_SCHEMA() != null) {
            queryType = SpecialSelectableType.CURRENT_SCHEMA;
        } else if (specialSelectableNameNode.CURRENT_CATALOG() != null) {
            queryType = SpecialSelectableType.CURRENT_CATALOG;
        } else if (specialSelectableNameNode.READONLY() != null) {
            queryType = SpecialSelectableType.READONLY;
        } else if (specialSelectableNameNode.AUTOCOMMIT() != null) {
            queryType = SpecialSelectableType.AUTOCOMMIT;
        } else if (specialSelectableNameNode.IDENTITY() != null || specialSelectableNameNode.LAST_INSERT_ID() != null) {
            queryType = SpecialSelectableType.LAST_INSERT_ID;
        } else {
            throw new IllegalArgumentException("Unknown selectable: " + specialSelectableNameNode.getText());
        }
        
        IdentifierContext aliasNode = selectSpecialQueryNode.alias;
        String alias = aliasNode != null ? parseIdentifierNode(aliasNode) : null;
        
        return Queries.selectSpecial()
                .queryType(queryType)
                .alias(alias)
                .build();
    }

    private SelectVariableQuery parseSelectVariableNode(SelectVariableQueryContext selectVariableQueryNode) {
        IdentifierContext variableNameNode = selectVariableQueryNode.variable().identifier();
        String variableName = parseIdentifierNode(variableNameNode);

        IdentifierContext aliasNode = selectVariableQueryNode.alias;
        String alias = aliasNode != null ? parseIdentifierNode(aliasNode) : null;
        
        return Queries.selectVariable()
                .name(variableName)
                .alias(alias)
                .build();
    }
    
    private InsertQuery parseInsertNode(InsertQueryContext insertQueryNode) {
        SchemaNameContext schemaNameNode = insertQueryNode.schemaName();
        String schemaName = schemaNameNode != null ?
                parseIdentifierNode(schemaNameNode.identifier()) :
                null;
        IdentifierContext identifierNode = insertQueryNode.tableName().identifier();
        String tableName = parseIdentifierNode(identifierNode);
        FieldListContext fieldListNode = insertQueryNode.fieldList();
        ImmutableList<String> fields = parseInsertFieldListNode(fieldListNode);
        ValueListContext valueListNode = insertQueryNode.valueList();
        ImmutableList<Object> values = parseInsertValueListNode(valueListNode);
        
        return Queries.insert()
                .inSchema(schemaName)
                .into(tableName)
                .fields(fields)
                .values(values)
                .build();
    }

    private ImmutableList<String> parseInsertFieldListNode(FieldListContext fieldListNode) {
        if (fieldListNode == null) {
            return null;
        }
        
        List<String> resultBuilder = new ArrayList<>();
        for (FieldNameContext fieldNameNode : fieldListNode.fieldName()) {
            String fieldName = parseIdentifierNode(fieldNameNode.identifier());
            resultBuilder.add(fieldName);
        }
        return ImmutableList.fromCollection(resultBuilder);
    }

    private ImmutableList<Object> parseInsertValueListNode(ValueListContext valueListNode) {
        List<Object> resultBuilder = new ArrayList<>();
        for (NullableValueContext nullableValueNode : valueListNode.nullableValue()) {
            Object value = parseNullableValueNode(nullableValueNode);
            resultBuilder.add(value);
        }
        return ImmutableList.fromCollection(resultBuilder);
    }

    private UpdateQuery parseUpdateNode(UpdateQueryContext updateQueryNode) {
        SchemaNameContext schemaNameNode = updateQueryNode.schemaName();
        String schemaName = schemaNameNode != null ?
                parseIdentifierNode(schemaNameNode.identifier()) :
                null;
        IdentifierContext identifierNode = updateQueryNode.tableName().identifier();
        String tableName = parseIdentifierNode(identifierNode);
        UpdatePartContext updatePartNode = updateQueryNode.updatePart();
        LinkedHashMap<String, Object> values = parseUpdatePartNode(updatePartNode);
        WherePartContext wherePartNode = updateQueryNode.wherePart();
        LinkedHashMap<String, Object> where = parseWherePartNode(wherePartNode, tableName); // TODO: alias?

        return Queries.update()
                .inSchema(schemaName)
                .table(tableName)
                .set(values)
                .where(where)
                .build();
    }

    private DeleteQuery parseDeleteNode(DeleteQueryContext deleteQueryNode) {
        SchemaNameContext schemaNameNode = deleteQueryNode.schemaName();
        String schemaName = schemaNameNode != null ?
                parseIdentifierNode(schemaNameNode.identifier()) :
                null;
        IdentifierContext identifierNode = deleteQueryNode.tableName().identifier();
        String tableName = parseIdentifierNode(identifierNode);
        WherePartContext wherePartNode = deleteQueryNode.wherePart();
        LinkedHashMap<String, Object> where = parseWherePartNode(wherePartNode, tableName); // TODO: alias?
        
        return Queries.delete()
                .inSchema(schemaName)
                .from(tableName)
                .where(where)
                .build();
    }
    
    private ShowSchemasQuery parseShowSchemasNode(ShowSchemasQueryContext showSchemasNode) {
        LikePartContext likePartContext = showSchemasNode.likePart();
        String like = parseLikePart(likePartContext);
        
        return Queries.showSchemas()
                .like(like)
                .build();
    }

    private ShowTablesQuery parseShowTablesNode(ShowTablesQueryContext showTablesNode) {
        SchemaNameContext schemaNameNode = showTablesNode.schemaName();
        String schemaName = schemaNameNode != null ?
                parseIdentifierNode(schemaNameNode.identifier()):
                null;
        LikePartContext likePartContext = showTablesNode.likePart();
        String like = parseLikePart(likePartContext);
        
        return Queries.showTables()
                .from(schemaName)
                .like(like)
                .build();
    }

    private UseQuery parseUseNode(UseQueryContext useNode) {
        IdentifierContext identifierNode = useNode.schemaName().identifier();
        String schemaName = parseIdentifierNode(identifierNode);
        
        return Queries.use()
                .schema(schemaName)
                .build();
    }
    
    private SetVariableQuery parseSetVariableNode(SetVariableQueryContext setVariableNode) {
        IdentifierContext identifierNode = setVariableNode.variable().identifier();
        String variableName = parseIdentifierNode(identifierNode);
        NullableValueContext valueNode = setVariableNode.nullableValue();
        Object value = parseNullableValueNode(valueNode);
        
        return Queries.setVariable()
                .name(variableName)
                .value(value)
                .build();
    }
    
    private String parseLikePart(LikePartContext likePartContext) {
        if (likePartContext == null) {
            return null;
        }
        return parseStringNode(likePartContext.TOKEN_STRING());
    }

    private LinkedHashMap<String, String> parseSelectPartNode(SelectPartContext selectPartNode, String tableAlias) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        
        SelectItemsContext selectItemsNode = selectPartNode.selectItems();
        if (selectItemsNode == null) {
            return result;
        }
        
        for (SelectItemContext selectItemNode : selectItemsNode.selectItem()) {
            ScopeableFieldNameContext scopeableFieldNameNode = selectItemNode.scopeableFieldName();
            String fieldName = parseIdentifierNode(scopeableFieldNameNode.fieldName().identifier());
            
            checkTableNameNode(scopeableFieldNameNode.tableName(), tableAlias);
            
            String alias = selectItemNode.alias != null ?
                    selectItemNode.alias.getText() :
                    fieldName;
            result.put(alias, fieldName);
        }
        return result;
    }
    
    private LinkedHashMap<String, Object> parseWherePartNode(WherePartContext wherePartNode, String tableAlias) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        if (wherePartNode == null) {
            return result;
        }
        
        for (WhereItemContext whereItemNode : wherePartNode.whereItem()) {
            Object[] fieldNameAndValue = parseWhereItemNode(whereItemNode, tableAlias);
            result.put((String) fieldNameAndValue[0], fieldNameAndValue[1]);
        }
        return result;
    }
    
    private Object[] parseWhereItemNode(WhereItemContext whereItemNode, String tableAlias) {
        WhereItemContext subItemNode = whereItemNode.whereItem();
        if (subItemNode != null) {
            return parseWhereItemNode(subItemNode, tableAlias);
        }
        
        ScopeableFieldNameContext scopeableFieldNameNode = whereItemNode.scopeableFieldName();
        String fieldName = parseIdentifierNode(scopeableFieldNameNode.fieldName().identifier());
        
        checkTableNameNode(scopeableFieldNameNode.tableName(), tableAlias);
        
        Object value = parsePostfixConditionNode(whereItemNode.postfixCondition());
        return new Object[] { fieldName, value };
    }

    private LinkedHashMap<String, Boolean> parseOrderByPartNode(OrderByPartContext orderByPartNode, String tableAlias) {
        LinkedHashMap<String, Boolean> result = new LinkedHashMap<>();
        if (orderByPartNode == null) {
            return result;
        }
        
        for (OrderByItemContext orderByItemNode : orderByPartNode.orderByItem()) {
            ScopeableFieldNameContext scopeableFieldNameNode = orderByItemNode.scopeableFieldName();
            String fieldName = parseIdentifierNode(scopeableFieldNameNode.fieldName().identifier());
            
            checkTableNameNode(scopeableFieldNameNode.tableName(), tableAlias);
            
            Boolean ascOrder = (orderByItemNode.DESC() == null);
            result.put(fieldName, ascOrder);
        }
        return result;
    }
    
    private LinkedHashMap<String, Object> parseUpdatePartNode(UpdatePartContext updatePartNode) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        for (UpdateItemContext updateItemNode : updatePartNode.updateItem()) {
            String fieldName = parseIdentifierNode(updateItemNode.fieldName().identifier());
            Object value = parseValueNode(updateItemNode.value());
            result.put(fieldName, value);
        }
        return result;
    }
    
    private void checkTableNameNode(TableNameContext tableNameNode, String expectedTableName) {
        if (tableNameNode == null) {
            return;
        }
        
        String fieldTableName = parseIdentifierNode(tableNameNode.identifier());
        if (!fieldTableName.equals(expectedTableName)) {
            throw new IllegalArgumentException("Unknown table: " + fieldTableName);
        }
    }

    private String parseIdentifierNode(IdentifierContext identifierNode) {
        TerminalNode simpleNameNode = identifierNode.TOKEN_SIMPLENAME();
        if (simpleNameNode != null) {
            return simpleNameNode.getText();
        }
        
        TerminalNode quotedNameNode = identifierNode.TOKEN_QUOTEDNAME();
        if (quotedNameNode != null) {
            return SqlUtil.unquoteIdentifier(quotedNameNode.getText());
        }
        
        TerminalNode backtickedNameNode = identifierNode.TOKEN_BACKTICKEDNAME();
        if (backtickedNameNode != null) {
            return SqlUtil.unbacktickIdentifier(backtickedNameNode.getText());
        }
        
        throw new IllegalArgumentException("Invalid identifier: " + identifierNode.getText());
    }

    private Object parsePostfixConditionNode(PostfixConditionContext postfixConditionNode) {
        ValueContext valueNode = postfixConditionNode.value();
        if (valueNode != null) {
            return parseValueNode(valueNode);
        } else if (postfixConditionNode.isNull() != null) {
            return SpecialCondition.IS_NULL;
        } else if (postfixConditionNode.isNotNull() != null) {
            return SpecialCondition.IS_NOT_NULL;
        } else {
            throw new IllegalArgumentException("Invalid postfix condition: " + postfixConditionNode.getText());
        }
    }

    private Object parseNullableValueNode(NullableValueContext nullableValueNode) {
        ValueContext valueNode = nullableValueNode.value();
        if (valueNode != null) {
            return parseValueNode(valueNode);
        }
        
        if (nullableValueNode.NULL() != null) {
            return null;
        }

        throw new IllegalArgumentException("Invalid value: " + nullableValueNode.getText());
    }
    
    private Object parseValueNode(ValueContext valueNode) {
        TerminalNode integerNode = valueNode.TOKEN_INTEGER();
        if (integerNode != null) {
            return parseIntegerNode(integerNode);
        }
        
        TerminalNode stringNode = valueNode.TOKEN_STRING();
        if (stringNode != null) {
            return parseStringNode(stringNode);
        }
        
        throw new IllegalArgumentException("Invalid literal: " + valueNode.getText());
    }
    
    private Integer parseIntegerNode(TerminalNode integerNode) {
        return Integer.parseInt(integerNode.getText());
    }

    private String parseStringNode(TerminalNode stringNode) {
        return SqlUtil.unquoteString(stringNode.getText());
    }
    
    
    private static class ParseErrorListener extends BaseErrorListener {
        
        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String message,
                RecognitionException e) {
            String fullMessage = String.format(
                    "SQL syntax error at line %d at %d: %s",
                    line,
                    charPositionInLine,
                    message);
            throw new IllegalArgumentException(fullMessage, e);
        }
        
    }

}
