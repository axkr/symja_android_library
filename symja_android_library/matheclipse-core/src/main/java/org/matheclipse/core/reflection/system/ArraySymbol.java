package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ArraySymbolExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Implementation of the ArraySymbol function.
 * <p>
 * Usage: ArraySymbol(name, {n1, n2, ...}), ArraySymbol(name, {n1, n2, ...}, domain),
 * ArraySymbol(name, {n1, n2, ...}, domain, symmetry)
 * </p>
 */
public class ArraySymbol extends AbstractEvaluator {

  public ArraySymbol() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    int argSize = ast.argSize();

    IExpr name = ast.arg1();
    IExpr dimensions = ast.arg2();

    // Validate dimensions: Must be a List
    if (!dimensions.isList()) {
      return F.NIL;
    }

    IAST dimsList = (IAST) dimensions;
    // Validate each dimension element (must be positive integer or symbolic)
    for (int i = 1; i <= dimsList.argSize(); i++) {
      IExpr dim = dimsList.get(i);
      if (dim.isInteger()) {
        if (dim.isNegative()) {
          // `1` is not a valid dimension for `2`.
          return Errors.printMessage(S.ArraySymbol, "dimss", F.List(dim, F.stringx("ArraySymbol")));
        }
      } else if (!dim.isSymbol() && !dim.isAST()) {
        // If not integer, it generally should be symbolic.
        // We permit ASTs (e.g. 2*n) or Symbols.
      }
    }

    IExpr domain = S.Reals;
    if (argSize >= 3) {
      domain = ast.arg3();
    }

    IExpr symmetry = S.None;
    if (argSize == 4) {
      symmetry = ast.arg4();
    }

    return new ArraySymbolExpr(name, dimsList, domain, symmetry);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_4;
  }
}
