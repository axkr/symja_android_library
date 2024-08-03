package org.matheclipse.core.sympy.concrete;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.exception.NotImplementedError;

public class Products {
  public static boolean isConvergent(IAST sumAST) {
    IExpr sequence_term = sumAST.arg1();
    IAST limits = (IAST) sumAST.arg2();
    IExpr log_sum = F.Log(sequence_term);
    boolean is_conv = false;
    try {
      is_conv = Summations.isConvergent(F.Sum(log_sum, limits));
    } catch (NotImplementedError nie) {
      if (Summations.isAbsolutelyConvergent(F.Sum(sequence_term.subtract(1), limits))) {
        return true;
      }
      throw new NotImplementedError(
          "The algorithm to find the product convergence is not yet implemented");
    }
    return is_conv;
  }
}
