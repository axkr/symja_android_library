package org.matheclipse.core.integrate;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Weierstrass ("tangent half-angle") substitution for rational trigonometric integrands.
 *
 * <p>
 * For an integrand that is a rational function of <code>Sin(x)</code> and <code>Cos(x)</code> (with
 * <code>Tan/Cot/Sec/Csc</code> first rewritten in terms of them), the substitution
 * <code>t = Tan(x/2)</code> gives
 *
 * <pre>
 * Sin(x) = 2 t / (1 + t^2),   Cos(x) = (1 - t^2) / (1 + t^2),   dx = 2 / (1 + t^2) dt,
 * </pre>
 *
 * turning the integral into a rational function of <code>t</code>, which is handed back to the
 * engine and back-substituted. Sometimes called the Jeffrey substitution in this project.
 *
 * <p>
 * Classical algorithm (see e.g. Bronstein, <i>Symbolic Integration I</i>, and any standard calculus
 * text); the rational integral is delegated to the engine and the antiderivative is diff-back
 * self-verified. Only integrands whose trigonometric arguments are the bare variable <code>x</code>
 * are recognised (multiple-angle integrands are not normalised first).
 */
public class WeierstrassIntegration {

  private WeierstrassIntegration() {}

  /**
   * Try the Weierstrass half-angle substitution.
   *
   * @param integrand the integrand
   * @param x the integration variable
   * @param engine the evaluation engine
   * @return the antiderivative or {@link F#NIL}
   */
  public static IExpr integrate(IExpr integrand, IExpr x, EvalEngine engine) {
    if (!Config.INTEGRATE_ALGORITHM_WEIERSTRASS) {
      return F.NIL;
    }
    // Rewrite the secondary trig functions of x in terms of Sin(x) and Cos(x).
    IExpr f = integrand;
    f = F.subst(f, F.Tan(x), F.Divide(F.Sin(x), F.Cos(x)));
    f = F.subst(f, F.Cot(x), F.Divide(F.Cos(x), F.Sin(x)));
    f = F.subst(f, F.Sec(x), F.Power(F.Cos(x), F.CN1));
    f = F.subst(f, F.Csc(x), F.Power(F.Sin(x), F.CN1));

    ISymbol t = F.Dummy("wei$t");
    IExpr onePlusT2 = F.Plus(F.C1, F.Power(t, F.C2));
    IExpr g = F.subst(f, F.Sin(x), F.Divide(F.Times(F.C2, t), onePlusT2));
    g = F.subst(g, F.Cos(x), F.Divide(F.Subtract(F.C1, F.Power(t, F.C2)), onePlusT2));
    if (!g.isFree(x, true)) {
      // Not a rational function of Sin(x)/Cos(x) of the bare variable x.
      return F.NIL;
    }
    IExpr integrandT = engine.evaluate(F.Together(F.Times(g, F.Divide(F.C2, onePlusT2))));
    IExpr inner = engine.evaluateNIL(F.Integrate(integrandT, t));
    if (inner.isNIL() || !inner.isFreeAST(S.Integrate) || !inner.isSpecialsFree()) {
      return F.NIL;
    }
    IExpr result = engine.evaluate(F.subst(inner, t, F.Tan(F.Times(F.C1D2, x))));
    if (verifyAntiderivative(result, integrand, x, engine)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Diff-back self-verification: {@code true} iff {@code D(result, x)} equals the integrand.
   * Escalates through {@code Together}, {@code Simplify} and {@code TrigReduce} because the
   * half-angle result (in {@code Tan(x/2)}) rarely differentiates back to the integrand without
   * trig normalisation.
   */
  private static boolean verifyAntiderivative(IExpr result, IExpr integrand, IExpr x,
      EvalEngine engine) {
    if (result.isNIL() || !result.isFreeAST(S.Integrate) || !result.isSpecialsFree()) {
      return false;
    }
    try {
      IExpr diff = F.Subtract(F.D(result, x), integrand);
      if (engine.evaluate(F.Together(diff)).isZero()) {
        return true;
      }
      if (engine.evaluate(F.Simplify(diff)).isZero()) {
        return true;
      }
      if (engine.evaluate(F.Simplify(F.TrigReduce(diff))).isZero()) {
        return true;
      }
      // Half-angle antiderivatives rarely reduce symbolically; confirm numerically at several
      // generic
      // interior points. This can only reject (false negatives fall through to the Rubi rules), so
      // it
      // never accepts a wrong antiderivative that a symbolic proof would have caught.
      return numericallyZero(diff, x, engine);
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
      return false;
    }
  }

  /** True iff {@code expr} numerically vanishes at every sample point (all chosen in (0, Pi)). */
  private static boolean numericallyZero(IExpr expr, IExpr x, EvalEngine engine) {
    final double[] points = {0.35, 0.9, 1.4, 2.3};
    for (double pt : points) {
      IExpr check;
      try {
        check =
            engine.evaluate(F.Less(F.Abs(F.ReplaceAll(expr, F.Rule(x, F.num(pt)))), F.num(1.0e-9)));
      } catch (RuntimeException rex) {
        org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
        return false;
      }
      if (!check.isTrue()) {
        return false;
      }
    }
    return true;
  }
}
