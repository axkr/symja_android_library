package org.matheclipse.core.sympy.calculus;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Monotonicity of real functions. Ported from
 * <a href="https://github.com/sympy/sympy/blob/master/sympy/calculus/singularities.py">sympy/calculus/singularities.py</a>
 *
 * <p>
 * The interval of sympy is given as a <code>domain</code> condition like <code>0&lt;x&lt;1</code>;
 * use {@link S#True} for all real numbers. All methods return a three-valued result:
 * {@link S#True}, {@link S#False} or {@link F#NIL} if the property can't be decided.
 *
 * <p>
 * Deviation from sympy: a strictly monotonic function is allowed to have isolated stationary
 * points (for example <code>x^3</code> is strictly increasing).
 */
public class Singularities {

  private Singularities() {}

  /**
   * Test if the <code>condition</code> can be satisfied for a real <code>x</code> inside the
   * <code>domain</code>.
   *
   * @return the reduced condition, {@link S#False} if the condition can't be satisfied or
   *         {@link F#NIL} if the condition can't be decided
   */
  public static IExpr reduce(IExpr condition, IExpr domain, ISymbol x, EvalEngine engine) {
    try {
      IExpr expr = domain.isTrue() ? condition : F.And(domain, condition);
      IExpr result = engine.evaluate(F.Reduce(expr, x, S.Reals));
      if (result.isFree(S.Reduce, true) && result.isFree(S.ConditionalExpression, true)) {
        return result;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  /**
   * Test if the function has a singularity inside the domain.
   *
   * @return {@link S#True}, {@link S#False} or {@link F#NIL} if the singularities are unknown
   */
  public static IExpr hasSingularity(IExpr function, IExpr domain, ISymbol x, EvalEngine engine) {
    try {
      IExpr sings = engine.evaluate(F.binaryAST2(S.FunctionSingularities, function, x));
      if (sings.isFalse()) {
        return S.False;
      }
      if (sings.isFree(S.FunctionSingularities, true)) {
        IExpr result = reduce(sings, domain, x, engine);
        if (result.isPresent()) {
          return result.isFalse() ? S.False : S.True;
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  /** Test if the reduced condition describes only isolated points <code>x==c</code>. */
  private static boolean isIsolatedPoints(IExpr reduced, ISymbol x) {
    if (reduced.isFalse()) {
      return true;
    }
    if (reduced.isEqual()) {
      IAST equal = (IAST) reduced;
      return equal.isAST2() && equal.arg1().equals(x) && equal.arg2().isFree(x);
    }
    if (reduced.isOr()) {
      IAST or = (IAST) reduced;
      for (int i = 1; i < or.size(); i++) {
        if (!isIsolatedPoints(or.get(i), x)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * @param sign <code>1</code> for increasing, <code>-1</code> for decreasing
   */
  private static IExpr monotonicityHelper(IExpr function, IExpr domain, ISymbol x, int sign,
      boolean strict, EvalEngine engine) {
    // sings = singularities(expression, variable, interval)
    // interior_sings = interval.interior.intersection(sings)
    // if interior_sings != S.EmptySet: return False
    if (hasSingularity(function, domain, x, engine).isTrue()) {
      return S.False;
    }
    // derivative = expression.diff(variable)
    // predicate_interval = solveset(predicate(derivative), variable, S.Reals)
    // return interval.is_subset(predicate_interval)
    IExpr derivative = engine.evaluate(F.D(function, x));
    if (!derivative.isFree(S.D, true) || !derivative.isFree(S.Derivative, true)) {
      return F.NIL;
    }
    if (derivative.isZero()) {
      return strict ? S.False : S.True;
    }
    IExpr violation = sign > 0 ? F.Less(derivative, F.C0) : F.Greater(derivative, F.C0);
    IExpr reduced = reduce(violation, domain, x, engine);
    if (reduced.isNIL()) {
      return F.NIL;
    }
    if (!reduced.isFalse()) {
      return S.False;
    }
    if (strict) {
      reduced = reduce(F.Equal(derivative, F.C0), domain, x, engine);
      if (reduced.isNIL()) {
        return F.NIL;
      }
      return isIsolatedPoints(reduced, x) ? S.True : S.False;
    }
    return S.True;
  }

  /**
   * Return whether the function is increasing in the given domain.
   */
  public static IExpr isIncreasing(IExpr function, IExpr domain, ISymbol x, EvalEngine engine) {
    // >>> is_increasing(x**3 - 3*x**2 + 4*x, S.Reals)
    // True
    // >>> is_increasing(-x**2, Interval(-oo, 0))
    // True
    // >>> is_increasing(-x**2, Interval(0, oo))
    // False
    // >>> is_increasing(4*x**3 - 6*x**2 - 72*x + 30, Interval(-2, 3))
    // False
    return monotonicityHelper(function, domain, x, 1, false, engine);
  }

  /**
   * Return whether the function is strictly increasing in the given domain.
   */
  public static IExpr isStrictlyIncreasing(IExpr function, IExpr domain, ISymbol x,
      EvalEngine engine) {
    // >>> is_strictly_increasing(4*x**3 - 6*x**2 - 72*x + 30, Interval.Ropen(-oo, -2))
    // True
    // >>> is_strictly_increasing(4*x**3 - 6*x**2 - 72*x + 30, Interval.open(-2, 3))
    // False
    // >>> is_strictly_increasing(-x**2, Interval(0, oo))
    // False
    return monotonicityHelper(function, domain, x, 1, true, engine);
  }

  /**
   * Return whether the function is decreasing in the given domain.
   */
  public static IExpr isDecreasing(IExpr function, IExpr domain, ISymbol x, EvalEngine engine) {
    // >>> is_decreasing(1/(x**2 - 3*x), Interval.open(S(3)/2, 3))
    // True
    // >>> is_decreasing(1/(x**2 - 3*x), Interval.open(1.5, 3))
    // True
    // >>> is_decreasing(1/(x**2 - 3*x), Interval.Lopen(3, oo))
    // True
    // >>> is_decreasing(1/(x**2 - 3*x), Interval.Ropen(-oo, S(3)/2))
    // False
    // >>> is_decreasing(-x**2, Interval(-oo, 0))
    // False
    return monotonicityHelper(function, domain, x, -1, false, engine);
  }

  /**
   * Return whether the function is strictly decreasing in the given domain.
   */
  public static IExpr isStrictlyDecreasing(IExpr function, IExpr domain, ISymbol x,
      EvalEngine engine) {
    // >>> is_strictly_decreasing(1/(x**2 - 3*x), Interval.Lopen(3, oo))
    // True
    // >>> is_strictly_decreasing(1/(x**2 - 3*x), Interval.Ropen(-oo, S(3)/2))
    // False
    // >>> is_strictly_decreasing(-x**2, Interval(-oo, 0))
    // False
    return monotonicityHelper(function, domain, x, -1, true, engine);
  }

  /**
   * Return whether the function is monotonic (i.e. increasing or decreasing) in the given domain.
   */
  public static IExpr isMonotonic(IExpr function, IExpr domain, ISymbol x, EvalEngine engine) {
    // >>> is_monotonic(1/(x**2 - 3*x), Interval.open(S(3)/2, 3))
    // True
    // >>> is_monotonic(1/(x**2 - 3*x), Interval.Lopen(3, oo))
    // True
    // >>> is_monotonic(x**3 - 3*x**2 + 4*x, S.Reals)
    // True
    // >>> is_monotonic(-x**2, S.Reals)
    // False
    IExpr increasing = isIncreasing(function, domain, x, engine);
    if (increasing.isTrue()) {
      return S.True;
    }
    IExpr decreasing = isDecreasing(function, domain, x, engine);
    if (decreasing.isTrue()) {
      return S.True;
    }
    if (increasing.isFalse() && decreasing.isFalse()) {
      return S.False;
    }
    return F.NIL;
  }
}
