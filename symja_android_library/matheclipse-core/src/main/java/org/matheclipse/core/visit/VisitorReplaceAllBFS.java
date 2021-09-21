package org.matheclipse.core.visit;

import java.util.Map;
import java.util.function.Function;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Replace all occurrences in breath first search of expressions where the given <code>
 * function.apply()</code> method returns a non <code>F.NIL</code> value. The visitors <code>visit()
 * </code> methods return <code>F.NIL</code> if no substitution occurred.
 */
public class VisitorReplaceAllBFS extends VisitorReplaceAll {

  public VisitorReplaceAllBFS(Function<IExpr, IExpr> function) {
    super(function, 0);
  }

  public VisitorReplaceAllBFS(Function<IExpr, IExpr> function, int offset) {
    super(function, offset);
  }

  public VisitorReplaceAllBFS(Map<? extends IExpr, ? extends IExpr> map) {
    super(map, 0);
  }

  public VisitorReplaceAllBFS(Map<? extends IExpr, ? extends IExpr> map, int offset) {
    super(map, offset);
  }

  public VisitorReplaceAllBFS(IAST ast) {
    super(ast);
  }

  public VisitorReplaceAllBFS(IAST ast, int offset) {
    super(ast, offset);
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    IExpr t1 = fFunction.apply(ast);
    if (t1.isPresent()) {
      return fFunction.apply(t1).orElse(t1);
    }
    return visitAST(ast);
  }
}
