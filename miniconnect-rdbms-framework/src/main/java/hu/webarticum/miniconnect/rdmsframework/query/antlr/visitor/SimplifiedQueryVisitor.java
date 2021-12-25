package hu.webarticum.miniconnect.rdmsframework.query.antlr.visitor;

import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryBaseVisitor;
import hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar.SimplifiedQueryParser.SimplifiedQueryContext;

public class SimplifiedQueryVisitor extends SimplifiedQueryBaseVisitor<Object> {

    @Override
    public String visitSimplifiedQuery(SimplifiedQueryContext ctx) {
        return ctx.selectQuery().alias().NAME().getText();
    }
    
}
