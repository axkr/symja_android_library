package org.matheclipse.core.visit;

import java.util.Map;
import java.util.function.Function;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Replace all occurrences in depth first search of expressions where the given <code>
 * function.apply()</code> method returns a non <code>F.NIL</code> value. The visitors <code>visit()
 * </code> methods return <code>F.NIL</code> if no substitution occurred.
 */
public class VisitorReplaceAllDFS extends VisitorReplaceAll {

  public VisitorReplaceAllDFS(Function<IExpr, IExpr> function) {
    super(function, 0);
  }

  public VisitorReplaceAllDFS(Function<IExpr, IExpr> function, int offset) {
    super(function, offset);
  }

  public VisitorReplaceAllDFS(Map<? extends IExpr, ? extends IExpr> map) {
    super(map, 0);
  }

  public VisitorReplaceAllDFS(Map<? extends IExpr, ? extends IExpr> map, int offset) {
    super(map, offset);
  }

  public VisitorReplaceAllDFS(IAST ast) {
    super(ast);
  }

  public VisitorReplaceAllDFS(IAST ast, int offset) {
    super(ast, offset);
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    // depth first search
    IExpr t1 = visitAST(ast);
    if (t1.isPresent()) {
      return fFunction.apply(t1).orElse(t1);
    }
    return fFunction.apply(ast);
  }
}
