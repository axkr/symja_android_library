package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.VectorSymbolExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Implementation of the VectorSymbol function.
 * <p>
 * Usage: VectorSymbol(name, dim) VectorSymbol(name, dim, domain)
 * </p>
 */
public class VectorSymbol extends AbstractEvaluator {

  public VectorSymbol() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    int argSize = ast.argSize();

    IExpr name = ast.arg1();
    IAST dimensions = ast.arg2().makeList();

    if (dimensions.argSize() != 1) {
      // The list `1` of dimensions `3` must have length `2`.
      return Errors.printMessage(S.VectorSymbol, "rankl",
          F.List(dimensions, F.C1, F.stringx("for a vector")));
    }
    IExpr dimension = dimensions.arg1();
    // Validate dimension: Must be a positive integer or a symbolic expression
    if (dimension.isInteger()) {
      if (dimension.isNegative()) {
        // Invalid dimension specification `1`.
        return Errors.printMessage(S.VectorSymbol, "nodim", F.List(dimension));
      }
      // } else if (!dimension.isSymbol() && !dimension.isAST()) {
      // If not integer, it should be a symbol or a valid expression representing size.
      // We allow ASTs (e.g. 2*n) or Symbols.
    }

    IExpr domain = S.Reals;
    if (argSize == 3) {
      domain = ast.arg3();
    }

    return new VectorSymbolExpr(name, dimension, domain);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }
}
