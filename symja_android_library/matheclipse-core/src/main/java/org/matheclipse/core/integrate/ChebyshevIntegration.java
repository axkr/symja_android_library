package org.matheclipse.core.integrate;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Integration of binomial differentials <code>x^m * (a + b*x^n)^p</code> by Chebyshev's theorem.
 *
 * <p>
 * For rational <code>m, n, p</code> and constants <code>a, b</code> free of <code>x</code>, the
 * integral is elementary iff at least one of
 * <ul>
 * <li><code>p &isin; &#8484;</code> &mdash; substitute <code>x = t^s</code> (case A),
 * <li><code>(m+1)/n &isin; &#8484;</code> &mdash; substitute <code>t^s = a + b*x^n</code> (case B),
 * <li><code>(m+1)/n + p &isin; &#8484;</code> &mdash; substitute <code>t^s = a*x^-n + b</code>
 * (case C),
 * </ul>
 * is an integer, where <code>s</code> is the denominator of <code>p</code>. Each substitution turns
 * the integrand into a rational function of <code>t</code>, which is handed back to the engine and
 * back-substituted.
 *
 * <p>
 * Cases A and B are implemented; case C is currently deferred &mdash; its reciprocal substitution
 * leaves nested radicals that the current normaliser does not reliably rationalise or diff-back
 * verify, so those integrands fall through to the Rubi rules instead.
 * 
 */
public class ChebyshevIntegration {

  private ChebyshevIntegration() {}

  /** Extracted parameters of a binomial differential {@code c * x^m * (a + b*x^n)^p}. */
  private static final class Params {
    IExpr c; // constant multiplier free of x
    IRational m;
    IExpr a;
    IExpr b;
    IRational n;
    IRational p;
  }

  /**
   * Try Chebyshev's binomial-differential method.
   *
   * @param integrand the integrand
   * @param x the integration variable
   * @param engine the evaluation engine
   * @return the antiderivative or {@link F#NIL}
   */
  public static IExpr integrate(IExpr integrand, IExpr x, EvalEngine engine) {
    if (!Config.INTEGRATE_ALGORITHM_CHEBYCHEV) {
      return F.NIL;
    }
    Params par = extract(integrand, x);
    if (par == null) {
      return F.NIL;
    }
    ISymbol t = F.Dummy("cheb$t");
    IExpr xOfT = substitutionForX(par, t, engine);
    if (xOfT.isNIL()) {
      return F.NIL;
    }
    // integrand(x)|x->xOfT * dx/dt, reduced to a rational function of t. PowerExpand collapses the
    // fractional powers that the case condition guarantees are integer powers; any unsound branch it
    // introduces is caught by the final diff-back verification, so it is safe to apply here.
    IExpr dxdt = engine.evaluate(F.D(xOfT, t));
    IExpr subIntegrand = engine.evaluate(F.subst(integrand, x, xOfT));
    // PowerExpand splits the substituted radicals into integer powers (the case condition guarantees
    // the reduced integrand is rational in t); any unsound branch it assumes is caught by the final
    // diff-back verification.
    IExpr integrandT = engine.evaluate(F.Together(F.PowerExpand(F.Times(subIntegrand, dxdt))));
    if (!integrandT.isFree(x, true)) {
      return F.NIL;
    }
    IExpr inner = engine.evaluateNIL(F.Integrate(integrandT, t));
    if (inner.isNIL() || !inner.isFreeAST(S.Integrate) || !inner.isSpecialsFree()) {
      return F.NIL;
    }
    IExpr result = engine.evaluate(F.subst(inner, t, backSubstitution(par, x)));
    if (verifyAntiderivative(result, integrand, x, engine)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Choose the substitution {@code x = xOfT} for the first applicable Chebyshev case, or
   * {@link F#NIL} if none applies.
   */
  private static IExpr substitutionForX(Params par, ISymbol t, EvalEngine engine) {
    IInteger s = par.p.denominator();
    IExpr invN = par.n.inverse();
    // (m+1)/n
    IExpr q = engine.evaluate(F.Divide(par.m.inc(), par.n));
    if (par.p.isInteger()) {
      // case A: x = t^s with s = lcm(denominator(m), denominator(n))
      IInteger sA = par.m.denominator().lcm(par.n.denominator());
      if (sA.isOne()) {
        // m, n, p all integers: the integrand is already rational in x, so x = t is a no-op that
        // would just re-integrate the same expression (infinite recursion). Leave such integrands to
        // the rational-function / Rubi stages.
        return F.NIL;
      }
      return F.Power(t, sA);
    }
    if (q.isInteger()) {
      // case B: t^s = a + b*x^n  =>  x = ((t^s - a)/b)^(1/n)
      return F.Power(F.Divide(F.Subtract(F.Power(t, s), par.a), par.b), invN);
    }
    // TODO(clean-room) case C ((m+1)/n + p integer; substitute t^s = a*x^-n + b) is not yet robust:
    // the reciprocal substitution leaves nested radicals that Symja's PowerExpand/Simplify do not
    // reliably rationalise or diff-back verify. Deferred; such integrands fall through to Rubi.
    return F.NIL;
  }

  /** The inverse substitution {@code t = t(x)} matching {@link #substitutionForX}. */
  private static IExpr backSubstitution(Params par, IExpr x) {
    IInteger s = par.p.denominator();
    IExpr binomial = F.Plus(par.a, F.Times(par.b, F.Power(x, par.n)));
    IExpr q = par.m.inc().divide(par.n);
    if (par.p.isInteger()) {
      IInteger sA = par.m.denominator().lcm(par.n.denominator());
      return F.Power(x, sA.inverse()); // x^(1/s)
    }
    if (q.isInteger()) {
      // t = (a + b*x^n)^(1/s)
      return F.Power(binomial, s.inverse());
    }
    // case C: t = (a*x^-n + b)^(1/s) = ((a + b*x^n)/x^n)^(1/s)
    return F.Power(F.Divide(binomial, F.Power(x, par.n)), s.inverse());
  }

  /**
   * Recognise {@code c * x^m * (a + b*x^n)^p} and pull out its parameters, or {@code null} if the
   * integrand is not a (rational-exponent) binomial differential.
   */
  private static Params extract(IExpr integrand, IExpr x) {
    IExpr c = F.C1;
    IRational m = F.C0;
    IExpr binomialPower = F.NIL;

    IAST factors = integrand.isTimes() ? (IAST) integrand : F.Times(integrand);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x, true)) {
        c = F.Times(c, factor);
        continue;
      }
      if (factor.equals(x)) {
        m = m.inc();
        continue;
      }
      if (factor.isPower() && factor.base().equals(x)) {
        IExpr e = factor.exponent();
        if (!e.isRational()) {
          return null;
        }
        m = m.add((IRational) e);
        continue;
      }
      // otherwise this must be the single (a + b*x^n)^p factor
      if (binomialPower.isPresent()) {
        return null; // more than one non-x-power x-dependent factor
      }
      binomialPower = factor;
    }
    if (binomialPower.isNIL()) {
      return null;
    }

    IExpr base;
    IRational p;
    if (binomialPower.isPower()) {
      base = binomialPower.base();
      IExpr e = binomialPower.exponent();
      if (!e.isRational()) {
        return null;
      }
      p = (IRational) e;
    } else {
      base = binomialPower;
      p = F.C1;
    }
    // base must be a + b*x^n
    if (!base.isPlus() || base.size() != 3) {
      return null;
    }
    IAST plus = (IAST) base;
    IExpr aTerm = F.NIL;
    IExpr bxnTerm = F.NIL;
    for (int i = 1; i < plus.size(); i++) {
      IExpr term = plus.get(i);
      if (term.isFree(x, true)) {
        aTerm = aTerm.isPresent() ? F.NIL : term;
      } else {
        bxnTerm = bxnTerm.isPresent() ? F.NIL : term;
      }
    }
    if (aTerm.isNIL() || bxnTerm.isNIL()) {
      return null;
    }
    // bxnTerm = b * x^n
    IExpr b = F.C1;
    IRational n = null;
    IAST bFactors = bxnTerm.isTimes() ? (IAST) bxnTerm : F.Times(bxnTerm);
    for (int i = 1; i < bFactors.size(); i++) {
      IExpr factor = bFactors.get(i);
      if (factor.isFree(x, true)) {
        b = F.Times(b, factor);
      } else if (factor.equals(x)) {
        if (n != null) {
          return null;
        }
        n = F.C1;
      } else if (factor.isPower() && factor.base().equals(x) && factor.exponent().isRational()) {
        if (n != null) {
          return null;
        }
        n = (IRational) factor.exponent();
      } else {
        return null;
      }
    }
    if (n == null || n.isZero()) {
      return null;
    }
    Params par = new Params();
    par.c = c;
    par.m = m;
    par.a = aTerm;
    par.b = b;
    par.n = n;
    par.p = p;
    return par;
  }

  /**
   * Diff-back self-verification: {@code true} iff {@code D(result, x)} equals the integrand. Because
   * the reduction uses {@code PowerExpand} (which assumes principal branches), this check is what
   * guarantees the returned antiderivative is actually correct before it can short-circuit anything.
   */
  private static boolean verifyAntiderivative(IExpr result, IExpr integrand, IExpr x,
      EvalEngine engine) {
    if (result.isNIL() || !result.isFreeAST(S.Integrate) || !result.isSpecialsFree()) {
      return false;
    }
    try {
      IExpr diff = engine.evaluate(F.Together(F.Subtract(F.D(result, x), integrand)));
      if (diff.isZero()) {
        return true;
      }
      return engine.evaluate(F.Simplify(diff)).isZero();
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
      return false;
    }
  }
}
