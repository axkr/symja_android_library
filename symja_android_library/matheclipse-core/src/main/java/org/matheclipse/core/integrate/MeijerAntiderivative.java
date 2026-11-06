package org.matheclipse.core.integrate;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Antiderivatives through Meijer G-functions: a power of <code>x</code> times one Bessel function
 * is written as a G-function, whose integral from <code>0</code> is again one G-function, and that
 * one is reduced to hypergeometric functions. Every answer is differentiated back.
 */
public final class MeijerAntiderivative {

  /** Points of the check: two with <code>x &gt; 0</code> and one with <code>x &lt; 0</code>. */
  private static final IExpr[] POINTS = {F.QQ(13, 10), F.QQ(11, 20), F.QQ(-9, 10)};


  /** The digits of the numerical check. */
  private static final int DIGITS = 30;

  private MeijerAntiderivative() {}

  /**
   * An antiderivative of <code>f</code>, or {@link F#NIL}.
   *
   * @param f a sum of terms <code>C*x^s*K(b*x)</code>, <code>K</code> one of
   *        <code>BesselJ, BesselY, BesselK</code>
   */
  public static IExpr integrate(IExpr f, IExpr x, EvalEngine engine) {
    if (!x.isSymbol() || !f.has(y -> y.isAST(S.BesselJ, 3) || y.isAST(S.BesselY, 3)
        || y.isAST(S.BesselK, 3), true)) {
      return F.NIL;
    }
    IExpr expanded = f.isPlus() ? f : engine.evaluate(F.Expand(f));
    IAST terms = expanded.isPlus() ? (IAST) expanded : F.Plus(expanded);
    IExpr[] antiderivatives = new IExpr[terms.argSize()];
    for (int i = 1; i <= terms.argSize(); i++) {
      engine.checkTimeLimit();
      antiderivatives[i - 1] = MeijerConvolution.antiderivative(terms.get(i), (ISymbol) x, engine);
      if (antiderivatives[i - 1].isNIL()) {
        return F.NIL;
      }
    }
    IExpr result = engine.evaluate(F.Plus(antiderivatives));
    return differentiatesBack(result, f, x, engine) ? result : F.NIL;
  }

  /**
   * Whether <code>D(result, x)</code> equals <code>f</code>: symbolically, or numerically with
   * generic values of the parameters at {@link #POINTS}. A second set of values is tried if the
   * first one lands on a pole.
   */
  private static boolean differentiatesBack(IExpr result, IExpr f, IExpr x, EvalEngine engine) {
    try {
      IExpr derivative = engine.evaluate(F.D(result, x));
      if (engine.evaluate(F.Subtract(derivative, f)).isZero()) {
        return true;
      }
      IAST parameters = new VariablesSet(F.list(f, result)).getVarList();
      for (int set = 0; set < 2; set++) {
        IExpr d = derivative;
        IExpr g = f;
        int k = 0;
        for (IExpr parameter : parameters) {
          if (!parameter.equals(x) && parameter.isSymbol()) {
            // distinct generic values (2k+3)/(4k+11), shifted for the second set
            IExpr value = F.QQ(2 * k + 3 + set, 4 * k + 11 + 3 * set);
            d = F.subst(d, parameter, value);
            g = F.subst(g, parameter, value);
            k++;
          }
        }
        IExpr.COMPARE_TERNARY agrees = agreesAtPoints(d, g, x, engine);
        if (agrees != IExpr.COMPARE_TERNARY.UNDECIDABLE) {
          return agrees == IExpr.COMPARE_TERNARY.TRUE;
        }
      }
      return false;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return false;
    }
  }

  /**
   * Whether <code>derivative</code> and <code>f</code> agree at {@link #POINTS}, or
   * <code>UNDECIDABLE</code> if one of them is not a number there.
   */
  private static IExpr.COMPARE_TERNARY agreesAtPoints(IExpr derivative, IExpr f, IExpr x,
      EvalEngine engine) {
    for (IExpr point : POINTS) {
      IExpr d = engine.evaluate(F.N(F.subst(derivative, x, point), F.ZZ(DIGITS)));
      IExpr value = engine.evaluate(F.N(F.subst(f, x, point), F.ZZ(DIGITS)));
      if (!d.isNumber() || !value.isNumber()) {
        return IExpr.COMPARE_TERNARY.UNDECIDABLE;
      }
      IExpr bound = engine.evaluate(F.Times(F.num(1.0e-20), F.Plus(F.C1, F.Abs(value))));
      if (!engine.evaluate(F.LessEqual(F.Abs(F.Subtract(d, value)), bound)).isTrue()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }
    }
    return IExpr.COMPARE_TERNARY.TRUE;
  }
}
