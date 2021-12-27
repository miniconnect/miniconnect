package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.LinkedHashMap;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.TerminalNode;

import hu.webarticum.miniconnect.rdmsframework.execution.SqlParser;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryLexer;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.IdentifierContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.OrderByItemContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.OrderByPartContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.SelectItemContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.SelectItemsContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.SelectPartContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.SelectQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.SimplifiedQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.ValueContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.WhereItemContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.WherePartContext;

public class AntlrSqlParser implements SqlParser {

    @Override
    public Query parse(String sql) {
        SimplifiedQueryLexer lexer = new SimplifiedQueryLexer(CharStreams.fromString(sql));
        SimplifiedQueryParser parser = new SimplifiedQueryParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(new ParseErrorListener());
        SimplifiedQueryContext rootNode = parser.simplifiedQuery();
        return parseRootNode(rootNode);
    }

    private Query parseRootNode(SimplifiedQueryContext rootNode) {
        SelectQueryContext selectQueryNode = rootNode.selectQuery();
        if (selectQueryNode != null) {
            return parseSelectNode(selectQueryNode);
        }
        
        // TODO: insert, update, delete
        
        throw new IllegalArgumentException("Query type not supported");
    }

    private SimpleSelectQuery parseSelectNode(SelectQueryContext selectQueryNode) {
        IdentifierContext identifierNode = selectQueryNode.tableName().identifier();
        String fromTableName = parseIdentifierNode(identifierNode);
        SelectPartContext selectPartNode = selectQueryNode.selectPart();
        LinkedHashMap<String, String> fields = parseSelectPartNode(selectPartNode);
        WherePartContext wherePartNode = selectQueryNode.wherePart();
        LinkedHashMap<String, Object> where = parseWherePartNode(wherePartNode);

        OrderByPartContext orderByNode = selectQueryNode.orderByPart();
        LinkedHashMap<String, Boolean> orderBy = parseOrderByPartNode(orderByNode);
        
        return Queries.select()
                .fields(fields)
                .from(fromTableName)
                .where(where)
                .orderBy(orderBy)
                .build();
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
                String msg,
                RecognitionException e) {
            throw new IllegalArgumentException(e);
        }
        
    }

}
