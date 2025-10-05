package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class FindGeneratingFunction extends AbstractFunctionEvaluator {

  /**
   * The maximum number of equal terms in the series coefficients to be considered for a cross
   * check.
   */
  private static final int MAX_SERIES_EQUAL_TERMS = 7;

  public FindGeneratingFunction() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.arg1().isList() && ast.arg2().isVariable()) {
      IAST coefficientsList = (IAST) ast.arg1();
      IExpr x = ast.arg2();
      IASTAppendable polynomial = F.PlusAlloc(coefficientsList.size() - 1);
      for (int i = 1; i < coefficientsList.size(); i++) {
        IExpr coefficient = coefficientsList.get(i);
        if (!coefficient.isZero()) {
          if (coefficient.isOne()) {
            if (i == 1) {
              polynomial.append(F.C1);
            } else {
              polynomial.append(F.Power(x, i - 1));
            }
          } else {
            if (i == 1) {
              polynomial.append(coefficient);
            } else {
              polynomial.append(F.Times(coefficient, F.Power(x, i - 1)));
            }
          }
        }
      }

      int n = coefficientsList.argSize();
      int m = (n - 1) / 2;
      int k = n - 1 - m;

      try {
        IExpr approximant =
            engine.evaluate(F.PadeApproximant(polynomial, F.List(x, F.C0, F.List(m, k))));
        if (approximant.isNumericFunction(x)) {
          if (crossCheckPade2SeriesCoefficient(approximant, coefficientsList, x, MAX_SERIES_EQUAL_TERMS,
              engine)) {
            return approximant;
          }
        }
      } catch (IllegalArgumentException e) {
        if (Config.SHOW_STACKTRACE) {
          e.printStackTrace();
        }
        //
      }
    }
    return F.NIL;
  }

  /**
   * Cross-checks if the approximant series coefficients matching the given list of coefficients.
   * 
   * @param approximant
   * @param sequenceCoefficients
   * @param x
   * @param maxNumbers the maximum number of series coefficients to check
   * @param engine
   * @return
   */
  private static boolean crossCheckPade2SeriesCoefficient(IExpr approximant,
      IAST sequenceCoefficients, IExpr x, int maxNumbers, EvalEngine engine) {
    // cross check if the approximant is a numeric function of x
    int length = Math.min(sequenceCoefficients.size(), maxNumbers);
    for (int i = 1; i < length; i++) {
      IExpr sequenceCoefficient = sequenceCoefficients.get(i);
      IExpr seriesCoefficient =
          engine.evaluate(F.SeriesCoefficient(approximant, F.List(x, F.C0, F.ZZ(i - 1))));
      if (!sequenceCoefficient.equals(seriesCoefficient)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_2;
  }
}
