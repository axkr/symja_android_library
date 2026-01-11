package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.MatrixSymbolExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Implementation of the MatrixSymbol function.
 * <p>
 * Usage: MatrixSymbol(name, {m, n}) MatrixSymbol(name, {m, n}, domain) MatrixSymbol(name, {m, n},
 * domain, symmetry)
 * </p>
 */
public class MatrixSymbol extends AbstractEvaluator {

  public MatrixSymbol() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    int argSize = ast.argSize();
    IExpr name = ast.arg1();
    IExpr dimensions = ast.arg2();

    // Validate dimensions: Must be a List of length 2
    if (!dimensions.isList() || dimensions.argSize() != 2) {
      // The list `1` of dimensions `3` must have length `2`.
      return Errors.printMessage(S.MatrixSymbol, "rankl",
          F.List(dimensions, F.C2, F.stringx("for a matrix")));
    }

    // Validate dimension elements (Positive Integers or Symbols)
    // Example: ( MatrixSymbol(x, {2, n}) ) is valid.
    IAST dimsList = (IAST) dimensions;
    for (int i = 1; i < dimsList.size(); i++) {
      IExpr dim = dimsList.get(i);
      if (dim.isInteger()) {
        if (!dim.isPositive()) {
          // Invalid dimension specification `1`.
          return Errors.printMessage(S.MatrixSymbol, "nodim", F.List(dim));
        }
        // } else if (!dim.isSymbol()) {
        // If not integer, it generally should be symbolic.
        // Complex expressions might be invalid for dimensions.
      }
    }

    IExpr domain = S.Reals;
    if (argSize >= 3) {
      domain = ast.arg3();
      // Validate domain if necessary (Complexes, Integers, Reals, ...)
    }

    IExpr symmetry = S.None;
    if (argSize == 4) {
      symmetry = ast.arg4();
      // Validate symmetry (Symmetric[{1,2}], Antisymmetric[{1,2}], etc.)
    }

    return new MatrixSymbolExpr(name, dimsList, domain, symmetry);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_4;
  }
}
