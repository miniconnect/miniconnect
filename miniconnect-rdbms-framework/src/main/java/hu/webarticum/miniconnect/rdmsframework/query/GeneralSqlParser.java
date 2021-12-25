package hu.webarticum.miniconnect.rdmsframework.query;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import hu.webarticum.miniconnect.rdmsframework.execution.SqlParser;
import hu.webarticum.miniconnect.rdmsframework.execution.fake.FakeQuery;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryLexer;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.visitor.SimplifiedQueryVisitor;

public class GeneralSqlParser implements SqlParser {

    @Override
    public Query parse(String sql) {
        SimplifiedQueryLexer lexer = new SimplifiedQueryLexer(CharStreams.fromString(sql));
        SimplifiedQueryParser parser = new SimplifiedQueryParser(new CommonTokenStream(lexer));
        ParseTree parseTree = parser.simplifiedQuery();
        Object result = new SimplifiedQueryVisitor().visit(parseTree);
        
        System.out.println("result: " + result);
        
        return new FakeQuery();
    }

}
