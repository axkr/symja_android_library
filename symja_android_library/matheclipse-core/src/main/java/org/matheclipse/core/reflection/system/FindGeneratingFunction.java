package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISeqBase;
import org.matheclipse.core.sympy.series.Sequences;

public class FindGeneratingFunction extends AbstractFunctionEvaluator {

  /** Seconds for the Pade approximant fallback. */
  private static final int PADE_SECONDS = 2;

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

      try {
        // a polynomial sequence of degree d has the generating function N(x)/(1-x)^(d+1)
        IExpr rational = polynomialGeneratingFunction(coefficientsList, x, engine);
        if (rational.isPresent()) {
          return rational;
        }
        // the rational generating function of the shortest linear recurrence confirmed by at
        // least one term beyond the 2*order fitted ones
        rational = linearRecurrenceGeneratingFunction(coefficientsList, x, engine);
        if (rational.isPresent()) {
          return rational;
        }

        // a Pade approximant of the polynomial, fitted to all but the last coefficient, has to
        // reproduce the last one too; a long list makes the approximant expensive
        if (n >= 3) {
          int m = (n - 2) / 2;
          int k = n - 2 - m;
          IExpr approximant = engine.evaluate(F.TimeConstrained(
              F.PadeApproximant(polynomial, F.List(x, F.C0, F.List(m, k))), F.ZZ(PADE_SECONDS)));
          if (approximant != S.$Aborted && approximant.isNumericFunction(x)) {
            if (crossCheckPade2SeriesCoefficient(approximant, coefficientsList, x,
                coefficientsList.size(), engine)) {
              return approximant;
            }
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
   * If the list are the values of a polynomial of degree <code>d</code> at <code>0, 1, ...</code>,
   * confirmed by at least one value beyond the <code>d+1</code> fitted ones, the generating function
   * <code>N(x)/(1-x)^(d+1)</code> where <code>N</code> is the product of the list's polynomial and
   * <code>(1-x)^(d+1)</code> truncated to degree <code>d</code>.
   */
  private static IExpr polynomialGeneratingFunction(IAST coefficientsList, IExpr x,
      EvalEngine engine) {
    int n = coefficientsList.argSize();
    if (n < 3 || !coefficientsList.forAll(c -> c.isRational())) {
      return F.NIL;
    }
    // repeated differences; degree d when the (d+1)-th differences vanish
    IExpr[] differences = new IExpr[n];
    for (int i = 0; i < n; i++) {
      differences[i] = coefficientsList.get(i + 1);
    }
    int degree = -1;
    for (int d = 0; d < n; d++) {
      // differences[0 .. n-1-d] hold the d-th differences
      boolean zero = true;
      for (int i = 0; i < n - d; i++) {
        if (!differences[i].isZero()) {
          zero = false;
          break;
        }
      }
      if (zero) {
        // degree d-1, confirmed by the n-d >= 1 vanishing values
        degree = d - 1;
        break;
      }
      for (int i = 0; i < n - d - 1; i++) {
        differences[i] = engine.evaluate(F.Subtract(differences[i + 1], differences[i]));
      }
    }
    if (degree < 0 || n < degree + 2) {
      return F.NIL;
    }
    // N(x) = ((1-x)^(d+1) * Sum(a(k)*x^k)) truncated to degree d
    IASTAppendable numerator = F.PlusAlloc(degree + 1);
    for (int j = 0; j <= degree; j++) {
      IASTAppendable c = F.PlusAlloc(j + 1);
      for (int i = 0; i <= j; i++) {
        c.append(F.Times(F.Power(F.CN1, F.ZZ(j - i)), F.Binomial(F.ZZ(degree + 1), F.ZZ(j - i)),
            coefficientsList.get(i + 1)));
      }
      numerator.append(F.Times(c, F.Power(x, F.ZZ(j))));
    }
    // (1-x)^(d+1) == (-1)^(d+1)*(-1+x)^(d+1)
    IExpr sign = (degree & 1) == 0 ? F.CN1 : F.C1;
    IExpr generatingFunction = engine.evaluate(F.Times(sign, F.Expand(numerator),
        F.Power(F.Plus(F.CN1, x), F.ZZ(-(degree + 1)))));
    if (!crossCheckPade2SeriesCoefficient(generatingFunction, coefficientsList, x,
        coefficientsList.size(), engine)) {
      return F.NIL;
    }
    return generatingFunction;
  }

  /**
   * The generating function <code>P(x)/Q(x)</code> with <code>Q(0) == 1</code> of the shortest
   * linear recurrence with constant coefficients of the list, or {@link F#NIL}.
   */
  private static IExpr linearRecurrenceGeneratingFunction(IAST coefficientsList, IExpr x,
      EvalEngine engine) {
    int n = coefficientsList.argSize();
    if (n < 3 || !coefficientsList.forAll(c -> c.isRational())) {
      return F.NIL;
    }
    ISeqBase seq = Sequences.sequence(coefficientsList);
    IAST result = seq.find_linear_recurrence(n, F.ZZ((n - 1) / 2), x);
    if (!result.isList2() || !result.arg1().isList() || result.arg1().isEmptyList()) {
      return F.NIL;
    }
    int order = result.arg1().argSize();
    if (2 * order >= n) {
      return F.NIL;
    }
    IExpr together = engine.evaluate(F.Together(result.arg2()));
    IExpr numerator = engine.evaluate(F.Expand(F.Numerator(together)));
    IExpr denominator = engine.evaluate(F.Expand(F.Denominator(together)));
    IExpr constant = engine.evaluate(F.subst(denominator, x, F.C0));
    if (constant.isZero()) {
      return F.NIL;
    }
    numerator = engine.evaluate(F.Expand(F.Divide(numerator, constant)));
    denominator = engine.evaluate(F.Expand(F.Divide(denominator, constant)));
    IExpr generatingFunction = F.Divide(numerator, denominator);
    if (!crossCheckPade2SeriesCoefficient(generatingFunction, coefficientsList, x,
        coefficientsList.size(), engine)) {
      return F.NIL;
    }
    return engine.evaluate(generatingFunction);
  }

  /**
   * Cross-checks if the approximant series coefficients matching the given list of coefficients.
   * 
   * @param approximantNumericFunction
   * @param sequenceCoefficients
   * @param x
   * @param maxNumbers the maximum number of series coefficients to check
   * @param engine
   * @return
   */
  private static boolean crossCheckPade2SeriesCoefficient(final IExpr approximantNumericFunction,
      IAST sequenceCoefficients, IExpr x, int maxNumbers, EvalEngine engine) {
    int length = Math.min(sequenceCoefficients.size(), maxNumbers);
    for (int i = 1; i < length; i++) {
      IExpr sequenceCoefficient = sequenceCoefficients.get(i);
      IExpr seriesCoefficient = S.SeriesCoefficient.of(engine, approximantNumericFunction,
          F.List(x, F.C0, F.ZZ(i - 1)));
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
