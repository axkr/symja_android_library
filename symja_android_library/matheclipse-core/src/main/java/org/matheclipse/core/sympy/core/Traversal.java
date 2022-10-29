package org.matheclipse.core.sympy.core;

import java.util.function.Function;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

public class Traversal {

  /**
   * Apply <code>function</code> to all expressions in an expression tree from the bottom up.
   * 
   * @param expr
   * @param function does a transformation on a single expression, or returns {@link F#NIL}
   * @return the transformed expression or the original expression if the expression tree wasn't
   *         changed
   */
  public static IExpr bottomUp(IExpr expr, Function<IExpr, IExpr> function) {
    return bottomUpNIL(expr, function, false, false).orElse(expr);
  }

  /**
   * Apply <code>function</code> to all expressions in an expression tree from the bottom up. If
   * <code>atoms</code> is <code>true</code>, apply <code>function</code> even if there are no
   * arguments; if <code>nonbasic</code> is <code>true</code>, try to apply <code>function</code> to
   * non-Basic objects.
   * 
   * @param expr
   * @param function does a transformation on a single expression, or returns {@link F#NIL}
   * @param atoms
   * @param nonbasic
   * @return the transformed expression or the original expression if the expression tree wasn't
   *         changed
   */
  public static IExpr bottomUp(IExpr expr, Function<IExpr, IExpr> function, boolean atoms,
      boolean nonbasic) {
    return bottomUpNIL(expr, function, atoms, nonbasic).orElse(expr);
  }

  /**
   * Apply <code>function</code> to all expressions in an expression tree from the bottom up. If
   * <code>atoms</code> is <code>true</code>, apply <code>function</code> even if there are no
   * arguments; if <code>nonbasic</code> is <code>true</code>, try to apply <code>function</code> to
   * non-Basic objects.
   * 
   * @param expr
   * @param function does a transformation on a single expression, or returns {@link F#NIL}
   * @return
   */
  public static IExpr bottomUpNIL(IExpr expr, Function<IExpr, IExpr> function) {
    return bottomUpNIL(expr, function, false, false);
  }

  /**
   * Apply <code>function</code> to all expressions in an expression tree from the bottom up. If
   * <code>atoms</code> is <code>true</code>, apply <code>function</code> even if there are no
   * arguments; if <code>nonbasic</code> is <code>true</code>, try to apply <code>function</code> to
   * non-Basic objects.
   * 
   * @param expr
   * @param function does a transformation on a single expression, or returns {@link F#NIL}
   * @param atoms
   * @param nonbasic
   * @return {@link F#NIL} if the expression tree wasn't changed, through the applying of the
   *         <code>function</code>.
   */
  private static IExpr bottomUpNIL(IExpr expr, Function<IExpr, IExpr> function, boolean atoms,
      boolean nonbasic) {
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      final int size = ast.size();
      if (size > 1) {
        IASTMutable result = F.NIL;
        for (int i = 1; i < size; i++) {
          IExpr arg = bottomUpNIL(ast.get(i), function, atoms, nonbasic);
          if (arg.isPresent()) {
            if (result.isNIL()) {
              result = ast.copy();
            }
            result.set(i, arg);
          }
        }
        return function.apply(result.orElse(ast)).orElse(result);
      } else if (atoms) {
        return function.apply(expr);
      }
    } else if (nonbasic) {
      return function.apply(expr);
    }
    return F.NIL;
  }
}
