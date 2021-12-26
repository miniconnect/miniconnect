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
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.SelectItemContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.SelectItemsContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.SelectPartContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.SelectQueryContext;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.SimplifiedQueryContext;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class AntlrSqlParser implements SqlParser {

    @Override
    public Query parse(String sql) {
        SimplifiedQueryLexer lexer = new SimplifiedQueryLexer(CharStreams.fromString(sql));
        SimplifiedQueryParser parser = new SimplifiedQueryParser(new CommonTokenStream(lexer));
        
        // TODO: proper error handling
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new IllegalArgumentException(e);
            }
        });

        SimplifiedQueryContext rootNode = parser.simplifiedQuery();
        return parseRootNode(rootNode);
    }

    private Query parseRootNode(SimplifiedQueryContext rootNode) {
        SelectQueryContext selectQueryNode = rootNode.selectQuery();
        if (selectQueryNode != null) {
            return parseSelectNode(selectQueryNode);
        }
        
        // TODO
        throw new IllegalArgumentException("Query type not supported");
        
    }

    private SimpleSelectQuery parseSelectNode(SelectQueryContext selectQueryNode) {
        IdentifierContext identifierNode = selectQueryNode.tableName().identifier();
        String fromTableName = parseIdentifierNode(identifierNode);
        SelectPartContext selectPartNode = selectQueryNode.selectPart();
        LinkedHashMap<String, String> selected = parseSelectPartNode(selectPartNode);
        ImmutableList<String> fields = selected != null ?
                new ImmutableList<>(selected.values()) :
                null;
        ImmutableList<String> aliases = selected != null ?
                new ImmutableList<>(selected.keySet()) :
                null;
        return Queries.select()
                .fields(fields)
                .aliases(aliases)
                .from(fromTableName)
                //.where(null) // TODO
                //.orderBy(null, true) // TODO
                .build();
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

    private LinkedHashMap<String, String> parseSelectPartNode(SelectPartContext selectPartNode) {
        SelectItemsContext selectItemsNode = selectPartNode.selectItems();
        if (selectItemsNode == null) {
            return null;
        }
        
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (SelectItemContext selectItemNode : selectItemsNode.selectItem()) {
            String fieldName = selectItemNode.field().identifier().getText();
            String alias = selectItemNode.alias != null ?
                    selectItemNode.alias.getText() :
                    fieldName;
            result.put(alias, fieldName);
        }
        return result;
    }

}
