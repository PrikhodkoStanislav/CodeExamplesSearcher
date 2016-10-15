import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link CVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class CNewVisitor extends CBaseVisitor<Void> {
    @Override public Void visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) { System.out.println(ctx.getText()); return visitChildren(ctx); }
}