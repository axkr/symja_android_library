package org.matheclipse.core.integrate;

import java.math.BigInteger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.AbstractVisitorBoolean;

/**
 * Integration by substitution of radicals of a linear function.
 *
 * <p>
 * If the integrand is built from <code>x</code> and fractional powers <code>(a+b*x)^(p/q)</code>
 * of one common linear base <code>u = a+b*x</code>, the substitution <code>t = u^(1/n)</code>
 * (where <code>n</code> is the least common multiple of all exponent denominators) transforms the
 * integral into the integral of a rational function in <code>t</code>:
 *
 * <pre>
 * x = (t^n - a)/b,  dx = (n/b) * t^(n-1) dt
 * </pre>
 *
 * The transformed integrand is delegated to {@link RationalIntegration} (or the general
 * <code>Integrate</code> evaluator for symbolic coefficients) and the result is back-substituted.
 */
public class RadicalSubstitution {

  /** Recursion guard, the substitution should not trigger itself through Integrate. */
  private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> Boolean.FALSE);

  /** Maximum least common multiple of the radical denominators. */
  private static final int MAX_LCM = 12;

  private RadicalSubstitution() {}

  /**
   * Try integration by substitution <code>t = (a+b*x)^(1/n)</code>.
   *
   * @param integrand the integrand
   * @param x the integration variable
   * @param engine the evaluation engine
   * @return the antiderivative or {@link F#NIL}
   */
  public static IExpr integrate(IExpr integrand, IExpr x, EvalEngine engine) {
    if (!Config.INTEGRATE_ALGORITHM_RADICAL_SUBSTITUTION || ACTIVE.get().booleanValue()) {
      return F.NIL;
    }
    try {
      // find the common radical base u = a+b*x and the lcm n of the exponent denominators
      RadicalInfo info = new RadicalInfo(x);
      try {
        integrand.accept(new RadicalVisitor(info));
      } catch (StopException se) {
        return F.NIL;
      }
      if (info.base == null || info.lcm <= 1 || info.lcm > MAX_LCM) {
        return F.NIL;
      }
      final IExpr u = info.base;
      final IExpr a = info.linear[0];
      final IExpr b = info.linear[1];
      final int n = info.lcm;
      final ISymbol t = F.Dummy("rs$t");

      // x -> (t^n - a)/b ; u^r -> t^(r*n)
      IExpr substituted = substitute(integrand, x, u, n, a, b, t);
      if (substituted.isNIL()) {
        return F.NIL;
      }
      // dx = (n/b) * t^(n-1) dt
      IExpr inner = F.Times(F.QQ(n, 1), F.Power(b, F.CN1), F.Power(t, F.ZZ(n - 1L)), substituted);
      inner = engine.evaluate(F.PowerExpand(F.Together(inner)));
      if (!inner.isFree(x, true)) {
        return F.NIL;
      }
      // integrate the transformed integrand
      IExpr innerResult = RationalIntegration.integrate(inner, t, engine);
      if (innerResult.isNIL()) {
        if (!RationalIntegration.isRationalFunction(inner, t)
            && !isSymbolicRationalFunction(inner, t)) {
          return F.NIL;
        }
        ACTIVE.set(Boolean.TRUE);
        try {
          innerResult = engine.evaluateNIL(F.Integrate(inner, t));
        } finally {
          ACTIVE.set(Boolean.FALSE);
        }
        if (innerResult.isNIL() || !innerResult.isFreeAST(org.matheclipse.core.expression.S.Integrate)
            || !innerResult.isSpecialsFree()) {
          return F.NIL;
        }
      }
      // back-substitute t -> u^(1/n)
      IExpr result = F.subst(innerResult, t, F.Power(u, F.QQ(1, n)));
      return engine.evaluate(result);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /**
   * Substitute <code>x -&gt; (t^n - a)/b</code> and <code>u^r -&gt; t^(r*n)</code> in the
   * integrand.
   */
  private static IExpr substitute(IExpr integrand, IExpr x, IExpr u, int n, IExpr a, IExpr b,
      ISymbol t) {
    IExpr xReplacement = F.Times(F.Power(b, F.CN1), F.Subtract(F.Power(t, F.ZZ(n)), a));
    IExpr result = F.subst(integrand, expr -> {
      if (expr.equals(u)) {
        return F.Power(t, F.ZZ(n));
      }
      if (expr.isPower() && expr.base().equals(u) && expr.exponent().isRational()) {
        IRational r = (IRational) expr.exponent();
        IRational newExponent = r.multiply(F.QQ(n, 1));
        if (newExponent.isInteger()) {
          return F.Power(t, newExponent);
        }
        return F.NIL;
      }
      if (expr.equals(x)) {
        return xReplacement;
      }
      return F.NIL;
    });
    return result.isFree(x, true) ? result : F.NIL;
  }

  /**
   * Test whether <code>expr</code> is a rational function in <code>t</code> allowing symbolic
   * coefficients which are free of <code>t</code>.
   */
  private static boolean isSymbolicRationalFunction(IExpr expr, IExpr t) {
    if (expr.isFree(t, true)) {
      return true;
    }
    if (expr.equals(t)) {
      return true;
    }
    if (expr.isPlus() || expr.isTimes()) {
      IAST ast = (IAST) expr;
      for (int i = 1; i < ast.size(); i++) {
        if (!isSymbolicRationalFunction(ast.get(i), t)) {
          return false;
        }
      }
      return true;
    }
    if (expr.isPower()) {
      return expr.exponent().isInteger() && isSymbolicRationalFunction(expr.base(), t);
    }
    return false;
  }

  private static final class StopException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    StopException() {
      super(null, null, false, false);
    }
  }

  private static final class RadicalInfo {
    final IExpr x;
    IExpr base; // common linear base u = a+b*x
    IExpr[] linear; // {a, b}
    int lcm = 1;

    RadicalInfo(IExpr x) {
      this.x = x;
    }
  }

  /**
   * Visitor collecting fractional powers of linear bases; throws {@link StopException} if two
   * different radical bases occur or a non-linear radical base is found.
   */
  private static final class RadicalVisitor extends AbstractVisitorBoolean {
    final RadicalInfo info;

    RadicalVisitor(RadicalInfo info) {
      this.info = info;
    }

    @Override
    public boolean visit(IAST ast) {
      if (ast.isPower() && ast.exponent().isFraction() && !ast.base().isFree(info.x, true)) {
        IExpr base = ast.base();
        IExpr[] lin = base.linear(info.x);
        if (lin == null || lin[1].isZero() || !lin[0].isFree(info.x, true)
            || !lin[1].isFree(info.x, true)) {
          throw new StopException();
        }
        if (info.base == null) {
          info.base = base;
          info.linear = lin;
        } else if (!info.base.equals(base)) {
          throw new StopException();
        }
        IFraction exponent = (IFraction) ast.exponent();
        BigInteger denominator = exponent.toBigDenominator();
        if (denominator.bitLength() > 5) {
          throw new StopException();
        }
        int q = denominator.intValueExact();
        info.lcm = lcm(info.lcm, q);
        if (info.lcm > MAX_LCM) {
          throw new StopException();
        }
        // visit the base for nested radicals
        base.accept(this);
        return false;
      }
      for (int i = 1; i < ast.size(); i++) {
        ast.get(i).accept(this);
      }
      return false;
    }

    private static int lcm(int a, int b) {
      return a / gcd(a, b) * b;
    }

    private static int gcd(int a, int b) {
      while (b != 0) {
        int t = b;
        b = a % b;
        a = t;
      }
      return a;
    }
  }
}
