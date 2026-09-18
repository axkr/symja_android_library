package org.matheclipse.core.eval;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Closed forms of the <code>n</code>-th derivative <code>D(f, {x, n})</code> for a symbolic order
 * <code>n</code> of an <i>exponential polynomial</i> <code>f</code> - a sum of terms
 *
 * <pre>
 * c * P(x) * g(a*x + b)
 * </pre>
 *
 * where <code>c</code>, <code>a</code>, <code>b</code> are free of <code>x</code>,
 * <code>P(x)</code> is a polynomial and <code>g</code> is <code>Sin</code>, <code>Cos</code>,
 * <code>Sinh</code>, <code>Cosh</code>, an exponential, a power or <code>Log</code>.
 *
 * <p>
 * Products and powers of trigonometric, hyperbolic and exponential functions like
 * <code>Sin(x)*Cos(x)</code> or <code>Sin(x)*E^x</code> are first linearised into such a sum by
 * <code>TrigReduce</code> or <code>Expand(TrigToExp(f))</code>. A polynomial factor is handled by
 * the Leibniz rule, which has only <code>Exponent(P, x) + 1</code> non-zero terms.
 */
public class DSymbolicOrder {

  /** Linearising huge expressions costs more than the closed form is worth. */
  private static final int MAX_LEAF_COUNT = 250;

  /** The highest polynomial degree for which the Leibniz rule is expanded. */
  private static final int MAX_POLYNOMIAL_DEGREE = 32;

  private final IExpr x;
  private final EvalEngine engine;

  private DSymbolicOrder(IExpr x, EvalEngine engine) {
    this.x = x;
    this.engine = engine;
  }

  /**
   * The closed form of <code>D(f, {x, n})</code> for the symbolic order <code>n</code>.
   *
   * @param f the function
   * @param x the differentiation variable
   * @param n the symbolic order
   * @param engine the evaluation engine
   * @return {@link F#NIL} if no part of <code>f</code> has a closed form
   */
  public static IExpr nThDerivative(IExpr f, IExpr x, IExpr n, EvalEngine engine) {
    if (f.leafCount() > MAX_LEAF_COUNT) {
      return F.NIL;
    }
    return new DSymbolicOrder(x, engine).closedForm(f, n, false);
  }

  /**
   * @param f the function
   * @param n the order, a symbol or a symbol minus an integer inside the Leibniz rule
   * @param linearised if <code>true</code> <code>f</code> was already linearised, and isn't
   *        rewritten again
   * @return {@link F#NIL} if there is no closed form
   */
  private IExpr closedForm(IExpr f, IExpr n, boolean linearised) {
    if (f.isFree(x, true)) {
      // Piecewise({{f, n == 0}}, 0)
      return F.Piecewise(F.list(F.list(f, F.Equal(n, F.C0))), F.C0);
    }
    if (f.equals(x)) {
      return F.Piecewise(F.list(F.list(x, F.Equal(n, F.C0)), F.list(F.C1, F.Equal(n, F.C1))),
          F.C0);
    }
    if (f.isPlus()) {
      IASTAppendable plus = F.PlusAlloc(f.argSize());
      for (IExpr term : (IAST) f) {
        IExpr termD = closedForm(term, n, linearised);
        if (termD.isNIL()) {
          return F.NIL;
        }
        plus.append(termD);
      }
      return plus;
    }
    if (f.isTimes()) {
      return times((IAST) f, n, linearised);
    }
    if (f.isPower()) {
      IExpr result = power(f.base(), f.exponent(), n);
      if (result.isPresent()) {
        return result;
      }
      return linearise(f, n, linearised);
    }
    if (f.isAST1()) {
      return function((IAST) f, n);
    }
    return F.NIL;
  }

  /**
   * <code>c * P(x) * r</code> - pull out the constant factors <code>c</code> and apply the Leibniz
   * rule to a polynomial factor <code>P(x)</code>.
   */
  private IExpr times(IAST times, IExpr n, boolean linearised) {
    IASTAppendable[] filter = times.filter(factor -> factor.isFree(x, true));
    if (filter[0].argSize() > 0) {
      IExpr rest = closedForm(filter[1].oneIdentity1(), n, linearised);
      return rest.isPresent() ? F.Times(filter[0].oneIdentity1(), rest) : F.NIL;
    }
    IExpr xPowerTimesExp = xPowerTimesExp(times, n);
    if (xPowerTimesExp.isPresent()) {
      return xPowerTimesExp;
    }
    IASTAppendable[] polynomial = times.filter(factor -> factor.isPolynomial(x));
    IExpr p = polynomial[0].oneIdentity1();
    IExpr r = polynomial[1].oneIdentity1();
    if (polynomial[0].argSize() > 0 && polynomial[1].argSize() > 0) {
      IExpr leibniz = leibniz(p, r, n, linearised);
      if (leibniz.isPresent()) {
        return leibniz;
      }
    }
    return linearise(times, n, linearised);
  }

  /**
   * The Leibniz rule <code>D(p*r, {x, n}) = Sum(Binomial(n, k)*D(p, {x, k})*D(r, {x, n-k}))</code>
   * for a polynomial <code>p</code>; only the terms with <code>k &lt;= Exponent(p, x)</code> are
   * non-zero.
   */
  private IExpr leibniz(IExpr p, IExpr r, IExpr n, boolean linearised) {
    int degree = engine.evaluate(F.Exponent(p, x)).toIntDefault();
    if (degree < 1 || degree > MAX_POLYNOMIAL_DEGREE) {
      return F.NIL;
    }
    ISymbol order = F.Dummy("k");
    IExpr rD = closedForm(r, order, linearised);
    if (rD.isNIL()) {
      return F.NIL;
    }
    IASTAppendable plus = F.PlusAlloc(degree + 1);
    IExpr pD = p;
    for (int k = 0; k <= degree; k++) {
      final IExpr orderValue = F.Subtract(n, F.ZZ(k));
      plus.append(F.Times(F.Binomial(n, F.ZZ(k)), pD,
          F.subst(rD, arg -> arg.equals(order) ? orderValue : F.NIL)));
      pD = engine.evaluate(F.D(pD, x));
    }
    return plus;
  }

  /**
   * <code>x^m * E^(a*x + b)</code> for <code>m</code> free of <code>x</code> and not a
   * non-negative integer (those are handled by the Leibniz rule):
   *
   * <pre>
   * E^(a*x + b) * x^(m-n) * Binomial(m, n) * n! * Hypergeometric1F1(-n, 1+m-n, -a*x)
   * </pre>
   */
  private IExpr xPowerTimesExp(IAST times, IExpr n) {
    if (!times.isAST2()) {
      return F.NIL;
    }
    for (int i = 1; i <= 2; i++) {
      IExpr exp = times.get(i);
      IExpr other = times.get(3 - i);
      if (!exp.isPower() || !exp.base().isE()) {
        continue;
      }
      IExpr[] linear = linear(exp.exponent());
      if (linear == null) {
        continue;
      }
      IExpr m;
      if (other.equals(x)) {
        m = F.C1;
      } else if (other.isPower() && other.base().equals(x) && other.exponent().isFree(x, true)) {
        m = other.exponent();
      } else {
        continue;
      }
      if (m.isInteger() && !m.isNegative()) {
        return F.NIL;
      }
      return F.Times(exp, F.Power(x, F.Subtract(m, n)), F.Binomial(m, n), F.Factorial(n),
          F.Hypergeometric1F1(F.Negate(n), F.Plus(F.C1, m, F.Negate(n)),
              F.Times(F.CN1, linear[1], x)));
    }
    return F.NIL;
  }

  /**
   * <code>base^exponent</code> with a linear exponent or a linear base.
   */
  private IExpr power(IExpr base, IExpr exponent, IExpr n) {
    if (base.isFree(x, true)) {
      IExpr[] linear = linear(exponent);
      if (linear == null) {
        return F.NIL;
      }
      IExpr a = linear[1];
      if (base.isE()) {
        // D(E^(a*x+b), {x,n}) -> a^n * E^(a*x+b)
        return F.Times(F.Power(a, n), F.Power(base, exponent));
      }
      // D(c^(a*x+b), {x,n}) -> (a*Log(c))^n * c^(a*x+b)
      return F.Times(F.Power(F.Times(a, F.Log(base)), n), F.Power(base, exponent));
    }
    if (exponent.isFree(x, true)) {
      IExpr[] linear = linear(base);
      if (linear == null) {
        return F.NIL;
      }
      // D((a*x+b)^m, {x,n}) -> a^n * FactorialPower(m, n) * (a*x+b)^(m-n)
      return F.Times(F.Power(linear[1], n), F.FactorialPower(exponent, n),
          F.Power(base, F.Subtract(exponent, n)));
    }
    return F.NIL;
  }

  /**
   * <code>Sin</code>, <code>Cos</code>, <code>Sinh</code>, <code>Cosh</code> and <code>Log</code>
   * of a linear argument.
   */
  private IExpr function(IAST function, IExpr n) {
    IExpr u = function.arg1();
    IExpr[] linear = linear(u);
    if (linear == null) {
      return F.NIL;
    }
    IExpr aPowerN = F.Power(linear[1], n);
    IExpr nPiHalf = F.Times(F.C1D2, n, S.Pi);
    switch (function.headID()) {
      case ID.Sin:
        return F.Times(aPowerN, F.Sin(F.Plus(u, nPiHalf)));
      case ID.Cos:
        return F.Times(aPowerN, F.Cos(F.Plus(u, nPiHalf)));
      case ID.Sinh:
        // I*(-I)^n*Sin(n*Pi/2 - I*u)
        return F.Times(aPowerN, F.CI, F.Power(F.CNI, n),
            F.Sin(F.Plus(nPiHalf, F.Times(F.CNI, u))));
      case ID.Cosh:
        // (-I)^n*Cos(n*Pi/2 - I*u)
        return F.Times(aPowerN, F.Power(F.CNI, n), F.Cos(F.Plus(nPiHalf, F.Times(F.CNI, u))));
      case ID.Log:
        // Piecewise({{(-1)^(n-1)*(n-1)!*a^n/u^n, n >= 1}}, Log(u))
        return F.Piecewise(
            F.list(F.list(F.Times(F.Power(F.CN1, F.Plus(F.CN1, n)),
                F.Factorial(F.Plus(F.CN1, n)), aPowerN, F.Power(u, F.Negate(n))),
                F.GreaterEqual(n, F.C1))),
            function);
      default:
        return F.NIL;
    }
  }

  /**
   * Rewrite a product or power of trigonometric, hyperbolic and exponential functions into a sum
   * of such functions with linear arguments.
   */
  private IExpr linearise(IExpr f, IExpr n, boolean linearised) {
    if (linearised) {
      return F.NIL;
    }
    IExpr reduced = engine.evaluate(F.TrigReduce(f));
    if (!reduced.equals(f)) {
      IExpr result = closedForm(reduced, n, true);
      if (result.isPresent()) {
        return result;
      }
    }
    IExpr exponential = engine.evaluate(F.Expand(F.TrigToExp(f)));
    if (!exponential.equals(f) && !exponential.equals(reduced)) {
      return closedForm(exponential, n, true);
    }
    return F.NIL;
  }

  /**
   * @return <code>{b, a}</code> for a linear expression <code>a*x + b</code> with a non-zero
   *         <code>a</code>, <code>null</code> otherwise
   */
  private IExpr[] linear(IExpr u) {
    if (u.isFree(x, true)) {
      return null;
    }
    IExpr[] linear = u.linear(x);
    if (linear == null || linear[1].isZero() || !linear[1].isFree(x, true)) {
      return null;
    }
    return linear;
  }
}
