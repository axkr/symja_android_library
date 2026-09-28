package org.matheclipse.core.sympy.calculus;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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
    IExpr result = reduceSolved(condition, domain, x, engine);
    if (result.isPresent()) {
      return result;
    }
    // Reduce can't solve relations with Sec, Csc, Tan, Cot: split into numerator and denominator
    if (condition.isAST2() && condition.second().isZero()
        && (condition.isAST(S.Equal) || condition.isAST(S.Less) || condition.isAST(S.Greater))) {
      try {
        IExpr expr = condition.first();
        IExpr numerator = engine.evaluate(F.binaryAST2(S.Numerator, expr, F.Rule(S.Trig, S.True)));
        IExpr denominator = engine.evaluate(F.binaryAST2(S.Denominator, expr, F.Rule(S.Trig, S.True)));
        if (denominator.isOne()) {
          return F.NIL;
        }
        if (condition.isAST(S.Equal)) {
          return reduceZerosOfNumerator(numerator, denominator, domain, x, engine);
        }
        // sign(numerator/denominator) == sign(numerator*denominator) where the quotient is
        // defined; a denominator which is an even power doesn't change the sign
        IExpr product = isEvenPower(denominator) ? numerator : F.Times(numerator, denominator);
        return reduceSolved(F.binaryAST2(condition.head(), product, F.C0), domain, x, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
    }
    return F.NIL;
  }

  /**
   * Reduce <code>condition &amp;&amp; domain</code> and accept only a result which is solved for
   * <code>x</code>.
   *
   * @return {@link F#NIL} if the result isn't solved for <code>x</code>
   */
  private static IExpr reduceSolved(IExpr condition, IExpr domain, ISymbol x,
      EvalEngine engine) {
    if (domain.isOr()) {
      // reduce every part of the domain separately
      IAST or = (IAST) domain;
      IASTAppendable result = F.ast(S.Or, or.argSize());
      for (int i = 1; i < or.size(); i++) {
        IExpr part = reduceSolved(condition, or.get(i), x, engine);
        if (part.isNIL()) {
          return F.NIL;
        }
        if (!part.isFalse()) {
          result.append(part);
        }
      }
      return sortPoints(engine.evaluate(result.oneIdentity0()), x);
    }
    try {
      IExpr expr = domain.isTrue() ? condition : F.And(domain, condition);
      IExpr result = engine.evaluate(F.Reduce(expr, x, S.Reals));
      if (isSolvedFor(result, x)) {
        return result;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  /**
   * The zeros of <code>numerator/denominator</code>: the zeros of the numerator in the domain,
   * where the denominator doesn't vanish.
   */
  private static IExpr reduceZerosOfNumerator(IExpr numerator, IExpr denominator, IExpr domain,
      ISymbol x, EvalEngine engine) {
    IExpr zeros = reduceSolved(F.Equal(numerator, F.C0), domain, x, engine);
    if (zeros.isNIL() || zeros.isFalse()) {
      return zeros;
    }
    if (!isIsolatedPoints(zeros, x)) {
      return F.NIL;
    }
    IAST points = zeros.isOr() ? (IAST) zeros : F.Or(zeros);
    IASTAppendable result = F.ast(S.Or, points.argSize());
    for (int i = 1; i < points.size(); i++) {
      IExpr point = points.get(i).second();
      IExpr value = engine.evaluate(F.subst(denominator, x, point));
      if (!value.isNumericFunction(true)) {
        return F.NIL;
      }
      if (!value.isZero()) {
        result.append(points.get(i));
      }
    }
    if (result.argSize() == 0) {
      return S.False;
    }
    return result.oneIdentity0();
  }

  /** Sort the isolated points <code>x==c1||x==c2||...</code> by their numeric value. */
  private static IExpr sortPoints(IExpr points, ISymbol x) {
    if (!points.isOr() || !isIsolatedPoints(points, x)) {
      return points;
    }
    try {
      IASTAppendable sorted = ((IAST) points).copyAppendable();
      sorted.sortInplace((a, b) -> Double.compare(a.second().evalf(), b.second().evalf()));
      return sorted;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return points;
    }
  }

  /**
   * Test if the <code>condition</code> is satisfied for some real <code>x</code> inside the
   * <code>domain</code>. If <code>Reduce</code> can't decide the condition, a sample point which
   * satisfies the condition is a proof for <code>True</code>.
   *
   * @return {@link S#True}, {@link S#False} or {@link F#NIL} if it can't be decided
   */
  public static IExpr isSatisfiable(IExpr condition, IExpr domain, ISymbol x,
      EvalEngine engine) {
    IExpr reduced = reduce(condition, domain, x, engine);
    if (reduced.isPresent()) {
      return reduced.isFalse() ? S.False : S.True;
    }
    return hasWitness(F.And(domain, condition), x, engine) ? S.True : F.NIL;
  }

  /** The numerators of the rational sample points */
  private static final int[] SAMPLES =
      {0, 1, -1, 2, -2, 3, -3, 5, -5, 7, -7, 10, -10, 13, -13, 20, -20, 50, -50, 100, -100};

  /**
   * Search a rational point <code>x0</code> for which the <code>condition</code> evaluates to
   * <code>True</code>.
   */
  private static boolean hasWitness(IExpr condition, ISymbol x, EvalEngine engine) {
    if (!condition.isFree(t -> t.isSymbol() && !t.isBuiltInSymbol() && !t.equals(x), true)) {
      // the condition depends on other variables
      return false;
    }
    for (int denominator : new int[] {1, 2, 3, 7, 16}) {
      for (int numerator : SAMPLES) {
        try {
          IExpr value = engine.evalQuiet(F.subst(condition, x, F.QQ(numerator, denominator)));
          if (value.isTrue()) {
            return true;
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
        }
      }
    }
    return false;
  }

  /** Test if the expression is a positive number or a product of even powers. */
  private static boolean isEvenPower(IExpr expr) {
    if (expr.isNumber()) {
      return expr.isPositive();
    }
    if (expr.isPower()) {
      // the base is real for the real variable, if it contains no complex numbers
      return expr.exponent().isInteger() && expr.exponent().isEven()
          && expr.base().isFree(t -> t.isNumber() && !t.isReal(), true);
    }
    if (expr.isTimes()) {
      IAST times = (IAST) expr;
      for (int i = 1; i < times.size(); i++) {
        if (!isEvenPower(times.get(i))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Test if the reduced condition is solved for <code>x</code>: <code>True</code>,
   * <code>False</code> or a boolean combination of relations between <code>x</code> and
   * constants. A result which still contains other variables or <code>x</code> inside a function
   * isn't solved.
   */
  static boolean isSolvedFor(IExpr result, ISymbol x) {
    if (result.isTrue() || result.isFalse()) {
      return true;
    }
    if (!result.isAST()) {
      return false;
    }
    IAST ast = (IAST) result;
    if (ast.isAnd() || ast.isOr()) {
      for (int i = 1; i < ast.size(); i++) {
        if (!isSolvedFor(ast.get(i), x)) {
          return false;
        }
      }
      return true;
    }
    if (ast.isEqual() || ast.isAST(S.Unequal) || ast.isRelational()) {
      boolean hasX = false;
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.equals(x)) {
          hasX = true;
        } else if (!arg.isFree(x) || !arg.isNumericFunction(true)) {
          return false;
        }
      }
      return hasX;
    }
    return false;
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
    IExpr satisfiable = isSatisfiable(violation, domain, x, engine);
    if (satisfiable.isNIL()) {
      return F.NIL;
    }
    if (satisfiable.isTrue()) {
      return S.False;
    }
    if (strict) {
      IExpr reduced = reduce(F.Equal(derivative, F.C0), domain, x, engine);
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
