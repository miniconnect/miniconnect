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
import hu.webarticum.miniconnect.rdmsframework.query.ShowSchemasQuery;
import hu.webarticum.miniconnect.rdmsframework.query.ShowTablesQuery;
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
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.OrderByItemContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.OrderByPartContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SchemaNameContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SelectItemContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SelectItemsContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SelectPartContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SelectQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.ShowSchemasQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.ShowTablesQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SqlQueryParser.SqlQueryContext;
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
        
        throw new IllegalArgumentException("Query type not supported");
    }

    private SelectQuery parseSelectNode(SelectQueryContext selectQueryNode) {
        SelectPartContext selectPartNode = selectQueryNode.selectPart();
        LinkedHashMap<String, String> fields = parseSelectPartNode(selectPartNode);
        SchemaNameContext schemaNameNode = selectQueryNode.schemaName();
        String schemaName = schemaNameNode != null ?
                parseIdentifierNode(schemaNameNode.identifier()) :
                null;
        IdentifierContext identifierNode = selectQueryNode.tableName().identifier();
        String tableName = parseIdentifierNode(identifierNode);
        WherePartContext wherePartNode = selectQueryNode.wherePart();
        LinkedHashMap<String, Object> where = parseWherePartNode(wherePartNode);
        OrderByPartContext orderByNode = selectQueryNode.orderByPart();
        LinkedHashMap<String, Boolean> orderBy = parseOrderByPartNode(orderByNode);
        
        return Queries.select()
                .fields(fields)
                .fromSchema(schemaName)
                .from(tableName)
                .where(where)
                .orderBy(orderBy)
                .build();
    }

    private InsertQuery parseInsertNode(InsertQueryContext insertQueryNode) {
        IdentifierContext identifierNode = insertQueryNode.tableName().identifier();
        String tableName = parseIdentifierNode(identifierNode);
        FieldListContext fieldListNode = insertQueryNode.fieldList();
        ImmutableList<String> fields = parseFieldListNode(fieldListNode);
        ValueListContext valueListNode = insertQueryNode.valueList();
        ImmutableList<Object> values = parseValueListNode(valueListNode);
        return Queries.insert()
                .into(tableName)
                .fields(fields)
                .values(values)
                .build();
    }

    private ImmutableList<String> parseFieldListNode(FieldListContext fieldListNode) {
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

    private ImmutableList<Object> parseValueListNode(ValueListContext valueListNode) {
        List<Object> resultBuilder = new ArrayList<>();
        for (ValueContext valueNode : valueListNode.value()) {
            Object value = parseValueNode(valueNode);
            resultBuilder.add(value);
        }
        return ImmutableList.fromCollection(resultBuilder);
    }

    private UpdateQuery parseUpdateNode(UpdateQueryContext updateQueryNode) {
        IdentifierContext identifierNode = updateQueryNode.tableName().identifier();
        String tableName = parseIdentifierNode(identifierNode);
        UpdatePartContext updatePartNode = updateQueryNode.updatePart();
        LinkedHashMap<String, Object> values = parseUpdatePartNode(updatePartNode);
        WherePartContext wherePartNode = updateQueryNode.wherePart();
        LinkedHashMap<String, Object> where = parseWherePartNode(wherePartNode);

        return Queries.update()
                .table(tableName)
                .set(values)
                .where(where)
                .build();
    }

    private DeleteQuery parseDeleteNode(DeleteQueryContext deleteQueryNode) {
        IdentifierContext identifierNode = deleteQueryNode.tableName().identifier();
        String tableName = parseIdentifierNode(identifierNode);
        WherePartContext wherePartNode = deleteQueryNode.wherePart();
        LinkedHashMap<String, Object> where = parseWherePartNode(wherePartNode);
        
        return Queries.delete()
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
        IdentifierContext identifierNode = showTablesNode.schemaName().identifier();
        String schemaName = parseIdentifierNode(identifierNode);
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
    
    private String parseLikePart(LikePartContext likePartContext) {
        if (likePartContext == null) {
            return null;
        }
        return parseStringNode(likePartContext.LIT_STRING());
    }

    private LinkedHashMap<String, String> parseSelectPartNode(SelectPartContext selectPartNode) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        
        SelectItemsContext selectItemsNode = selectPartNode.selectItems();
        if (selectItemsNode == null) {
            return result;
        }
        
        for (SelectItemContext selectItemNode : selectItemsNode.selectItem()) {
            String fieldName = parseIdentifierNode(selectItemNode.fieldName().identifier());
            String alias = selectItemNode.alias != null ?
                    selectItemNode.alias.getText() :
                    fieldName;
            result.put(alias, fieldName);
        }
        return result;
    }
    
    private LinkedHashMap<String, Object> parseWherePartNode(WherePartContext wherePartNode) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        if (wherePartNode == null) {
            return result;
        }
        
        for (WhereItemContext whereItemNode : wherePartNode.whereItem()) {
            String fieldName = parseIdentifierNode(whereItemNode.fieldName().identifier());
            Object value = parseValueNode(whereItemNode.value());
            result.put(fieldName, value);
        }
        return result;
    }

    private LinkedHashMap<String, Boolean> parseOrderByPartNode(
            OrderByPartContext orderByPartNode) {
        LinkedHashMap<String, Boolean> result = new LinkedHashMap<>();
        if (orderByPartNode == null) {
            return result;
        }
        
        for (OrderByItemContext orderByItemNode : orderByPartNode.orderByItem()) {
            String fieldName = parseIdentifierNode(orderByItemNode.fieldName().identifier());
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

    private String parseIdentifierNode(IdentifierContext identifierNode) {
        TerminalNode simpleNameNode = identifierNode.SIMPLENAME();
        if (simpleNameNode != null) {
            return simpleNameNode.getText();
        }
        
        TerminalNode quotedNameNode = identifierNode.QUOTEDNAME();
        if (quotedNameNode != null) {
            return SqlUtil.unquoteIdentifier(quotedNameNode.getText());
        }
        
        TerminalNode backtickedNameNode = identifierNode.BACKTICKEDNAME();
        if (backtickedNameNode != null) {
            return SqlUtil.unbacktickIdentifier(backtickedNameNode.getText());
        }
        
        throw new IllegalArgumentException("Invalid identifier: " + identifierNode.getText());
    }
    
    private Object parseValueNode(ValueContext valueNode) {
        TerminalNode integerNode = valueNode.LIT_INTEGER();
        if (integerNode != null) {
            return parseIntegerNode(integerNode);
        }
        
        TerminalNode stringNode = valueNode.LIT_STRING();
        if (stringNode != null) {
            return parseStringNode(stringNode);
        }
        
        if (valueNode.NULL() != null) {
            return null;
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