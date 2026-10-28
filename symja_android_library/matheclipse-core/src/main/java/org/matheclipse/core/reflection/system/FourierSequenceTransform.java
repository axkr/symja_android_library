package org.matheclipse.core.reflection.system;

import java.util.Map;
import java.util.TreeMap;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>FourierSequenceTransform(u, n, w)</code> - the discrete-time Fourier transform of the
 * sequence <code>u(n)</code>.
 *
 * <p>
 * With the option <code>FourierParameters-&gt;{a, b}</code> (default <code>{1, 1}</code>) the
 * transform is defined as
 *
 * <pre>
 * FourierSequenceTransform(u, n, w) = (Abs(b)/(2*Pi))^((1-a)/2) * Sum(u(n) * E^(-I*b*n*w), {n, -Infinity, Infinity})
 * </pre>
 *
 * <p>
 * Sequences with a finite support are transformed: <code>KroneckerDelta</code>,
 * <code>DiscreteDelta</code>, <code>Boole</code> of a bounded condition, <code>If</code> or
 * <code>Piecewise</code> with bounded conditions, <code>UnitStep</code> bounded from both sides,
 * and sums and products of these with other factors. The sum is returned as the lowest power of
 * <code>E^(I*w)</code> times a polynomial in <code>E^(I*w)</code>.
 */
public class FourierSequenceTransform extends AbstractFunctionOptionEvaluator {

  /** The largest number of terms of a finite support which is summed up. */
  private static final long MAX_TERMS = 4096L;

  /** The support of a sequence as the closed integer interval <code>[lo, hi]</code>. */
  private static final class Support {
    static final Support ALL = new Support(Long.MIN_VALUE, Long.MAX_VALUE);
    static final Support EMPTY = new Support(1L, 0L);

    final long lo;
    final long hi;

    Support(long lo, long hi) {
      this.lo = lo;
      this.hi = hi;
    }

    boolean isEmpty() {
      return lo > hi;
    }

    boolean isFinite() {
      return isEmpty() || (lo != Long.MIN_VALUE && hi != Long.MAX_VALUE);
    }

    Support intersect(Support other) {
      if (isEmpty() || other.isEmpty()) {
        return EMPTY;
      }
      long l = Math.max(lo, other.lo);
      long h = Math.min(hi, other.hi);
      return l > h ? EMPTY : new Support(l, h);
    }

    /** The smallest interval which contains both intervals. */
    Support union(Support other) {
      if (isEmpty()) {
        return other;
      }
      if (other.isEmpty()) {
        return this;
      }
      return new Support(Math.min(lo, other.lo), Math.max(hi, other.hi));
    }
  }

  public FourierSequenceTransform() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] option, final EvalEngine engine,
      IAST originalAST) {
    if (argSize != 3) {
      return F.NIL;
    }
    final IExpr u = ast.arg1();
    final IExpr n = ast.arg2();
    final IExpr w = ast.arg3();
    if (!n.isVariable()) {
      if (n.isList()) {
        return F.NIL;
      }
      // `1` is not a valid variable.
      return Errors.printMessage(ast.topHead(), "ivar", F.List(n), engine);
    }
    final IExpr[] ab = FourierSeries.fourierParametersAB(option[0]);
    if (ab == null) {
      return F.NIL;
    }
    final IExpr a = ab[0];
    final IExpr b = ab[1];
    try {
      final Support support = support(u, n, engine);
      if (!support.isFinite() || (!support.isEmpty()
          && (support.hi - support.lo < 0 || support.hi - support.lo >= MAX_TERMS))) {
        return F.NIL;
      }
      // (Abs(b)/(2*Pi))^((1-a)/2), written as (2*Pi/Abs(b))^((a-1)/2) to keep 2*Pi under one root
      final IExpr factor = engine.evaluate(F.Power(F.Times(F.C2Pi, F.Power(F.Abs(b), F.CN1)),
          F.Times(F.C1D2, F.Subtract(a, F.C1))));
      if (support.isEmpty()) {
        return F.C0;
      }
      return engine.evaluate(F.Times(factor, sum(u, n, w, b, support, engine)));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  /**
   * <code>Sum(u(n) * E^(-I*b*n*w), {n, lo, hi})</code> as the lowest power of the exponential
   * times a polynomial in it. For an integer <code>b</code> the exponential is
   * <code>E^(I*w)</code>, otherwise <code>E^(I*b*w)</code>.
   */
  private static IExpr sum(IExpr u, IExpr n, IExpr w, IExpr b, Support support,
      EvalEngine engine) {
    final int bInt = b.toIntDefault();
    final boolean integerB = b.isInteger() && !F.isNotPresent(bInt);
    final IExpr z = engine.evaluate(F.Exp(F.Times(F.CI, integerB ? F.C1 : b, w)));
    // the coefficients of the powers of z
    final Map<Long, IExpr> coefficients = new TreeMap<Long, IExpr>();
    for (long k = support.lo; k <= support.hi; k++) {
      IExpr value = engine.evaluate(F.subst(u, n, F.ZZ(k)));
      if (value.isZero()) {
        continue;
      }
      final long exponent = integerB ? -bInt * k : -k;
      IExpr old = coefficients.get(exponent);
      coefficients.put(exponent, old == null ? value : engine.evaluate(F.Plus(old, value)));
    }
    coefficients.values().removeIf(IExpr::isZero);
    if (coefficients.isEmpty()) {
      return F.C0;
    }
    final long lowest = coefficients.keySet().iterator().next();
    // a factor which all coefficients have in common up to a rational number is put in front
    IExpr common = coefficients.get(lowest);
    for (IExpr coefficient : coefficients.values()) {
      if (!engine.evaluate(F.Divide(coefficient, common)).isRational()) {
        common = F.C1;
        break;
      }
    }
    IASTAppendable polynomial = F.PlusAlloc(coefficients.size());
    for (Map.Entry<Long, IExpr> entry : coefficients.entrySet()) {
      polynomial.append(F.Times(F.Divide(entry.getValue(), common),
          F.Power(z, F.ZZ(entry.getKey() - lowest))));
    }
    IExpr together = engine.evaluate(F.Together(engine.evaluate(polynomial.oneIdentity0())));
    return F.Times(common, F.Power(z, F.ZZ(lowest)), together);
  }

  /**
   * The integers <code>n</code> at which the sequence <code>u</code> may be different from
   * <code>0</code>. The interval may be larger than the support but never smaller.
   */
  private static Support support(IExpr u, IExpr n, EvalEngine engine) {
    if (u.isFree(n)) {
      return u.isZero() ? Support.EMPTY : Support.ALL;
    }
    if (u.isTimes()) {
      Support result = Support.ALL;
      for (IExpr factor : (IAST) u) {
        result = result.intersect(support(factor, n, engine));
      }
      return result;
    }
    if (u.isPlus()) {
      Support result = Support.EMPTY;
      for (IExpr term : (IAST) u) {
        result = result.union(support(term, n, engine));
        if (!result.isFinite()) {
          return Support.ALL;
        }
      }
      return result;
    }
    if (u.isPower() && u.exponent().isPositiveResult()) {
      return support(u.base(), n, engine);
    }
    if (u.isAST(S.KroneckerDelta) && u.argSize() >= 1) {
      IAST delta = (IAST) u;
      if (delta.argSize() == 1) {
        return equalZero(delta.arg1(), n, engine);
      }
      Support result = Support.ALL;
      for (int i = 2; i < delta.size(); i++) {
        result = result.intersect(equalZero(F.Subtract(delta.get(i), delta.arg1()), n, engine));
      }
      return result;
    }
    if (u.isAST(S.DiscreteDelta) && u.argSize() >= 1) {
      Support result = Support.ALL;
      for (IExpr arg : (IAST) u) {
        result = result.intersect(equalZero(arg, n, engine));
      }
      return result;
    }
    if (u.isAST(S.UnitStep) && u.argSize() >= 1) {
      Support result = Support.ALL;
      for (IExpr arg : (IAST) u) {
        result = result.intersect(compare(arg, S.GreaterEqual, F.C0, n, engine));
      }
      return result;
    }
    if (u.isAST(S.Boole, 2)) {
      return condition(u.first(), n, engine);
    }
    if (u.isAST(S.If, 4)) {
      IAST ifAST = (IAST) u;
      return condition(ifAST.arg1(), n, engine).intersect(support(ifAST.arg2(), n, engine))
          .union(support(ifAST.arg3(), n, engine));
    }
    if (u.isAST(S.Piecewise, 2, 3) && u.first().isList()) {
      Support result = u.argSize() == 2 ? support(u.second(), n, engine) : Support.EMPTY;
      for (IExpr piece : (IAST) u.first()) {
        if (!piece.isList2()) {
          return Support.ALL;
        }
        result = result.union(condition(piece.second(), n, engine)
            .intersect(support(piece.first(), n, engine)));
      }
      return result;
    }
    return Support.ALL;
  }

  /** The integers <code>n</code> which may satisfy the condition. */
  private static Support condition(IExpr condition, IExpr n, EvalEngine engine) {
    if (condition.isTrue()) {
      return Support.ALL;
    }
    if (condition.isFalse()) {
      return Support.EMPTY;
    }
    if (condition.isAnd()) {
      Support result = Support.ALL;
      for (IExpr arg : (IAST) condition) {
        result = result.intersect(condition(arg, n, engine));
      }
      return result;
    }
    if (condition.isOr()) {
      Support result = Support.EMPTY;
      for (IExpr arg : (IAST) condition) {
        result = result.union(condition(arg, n, engine));
      }
      return result;
    }
    if (condition.isAST(S.Inequality) && condition.argSize() >= 3) {
      IAST inequality = (IAST) condition;
      Support result = Support.ALL;
      for (int i = 2; i + 1 < inequality.size(); i += 2) {
        result = result.intersect(
            compare(inequality.get(i - 1), inequality.get(i), inequality.get(i + 1), n, engine));
      }
      return result;
    }
    if (condition.isAST() && condition.argSize() >= 2 && (condition.isAST(S.Less)
        || condition.isAST(S.LessEqual) || condition.isAST(S.Greater)
        || condition.isAST(S.GreaterEqual) || condition.isAST(S.Equal))) {
      IAST comparison = (IAST) condition;
      Support result = Support.ALL;
      for (int i = 2; i < comparison.size(); i++) {
        result = result.intersect(
            compare(comparison.get(i - 1), comparison.head(), comparison.get(i), n, engine));
      }
      return result;
    }
    return Support.ALL;
  }

  /** The integers <code>n</code> which may satisfy <code>lhs operator rhs</code>. */
  private static Support compare(IExpr lhs, IExpr operator, IExpr rhs, IExpr n,
      EvalEngine engine) {
    if (lhs.isAbs() && rhs.isFree(n)) {
      return absBound(lhs.first(), operator, rhs, n, engine);
    }
    if (rhs.isAbs() && lhs.isFree(n)) {
      return absBound(rhs.first(), reverse(operator), lhs, n, engine);
    }
    if (operator == S.Equal) {
      return equalZero(F.Subtract(lhs, rhs), n, engine);
    }
    final IExpr[] linear = linear(F.Subtract(lhs, rhs), n, engine);
    if (linear == null) {
      return Support.ALL;
    }
    // c0 + c1*n operator 0, i.e. n operator' -c0/c1
    IExpr bound = engine.evaluate(F.Divide(F.Negate(linear[0]), linear[1]));
    if (!bound.isReal()) {
      return Support.ALL;
    }
    if (linear[1].isNegative()) {
      operator = reverse(operator);
    }
    // n < x is n <= Ceiling(x)-1 and n > x is n >= Floor(x)+1
    if (operator == S.Less || operator == S.LessEqual) {
      long hi = toLong(engine.evaluate(operator == S.Less //
          ? F.Subtract(F.Ceiling(bound), F.C1)
          : F.Floor(bound)), Long.MAX_VALUE);
      return new Support(Long.MIN_VALUE, hi);
    }
    if (operator == S.Greater || operator == S.GreaterEqual) {
      long lo = toLong(engine.evaluate(operator == S.Greater //
          ? F.Plus(F.Floor(bound), F.C1)
          : F.Ceiling(bound)), Long.MIN_VALUE);
      return new Support(lo, Long.MAX_VALUE);
    }
    return Support.ALL;
  }

  /** The integers <code>n</code> which may satisfy <code>Abs(expr) operator bound</code>. */
  private static Support absBound(IExpr expr, IExpr operator, IExpr bound, IExpr n,
      EvalEngine engine) {
    if (operator == S.Less || operator == S.LessEqual) {
      IExpr negative = F.Negate(bound);
      return compare(expr, operator, bound, n, engine)
          .intersect(compare(negative, operator, expr, n, engine));
    }
    if (operator == S.Equal) {
      return equalZero(F.Subtract(expr, bound), n, engine)
          .union(equalZero(F.Plus(expr, bound), n, engine));
    }
    return Support.ALL;
  }

  /** The integers <code>n</code> for which <code>expr == 0</code> may hold. */
  private static Support equalZero(IExpr expr, IExpr n, EvalEngine engine) {
    final IExpr[] linear = linear(expr, n, engine);
    if (linear == null) {
      return Support.ALL;
    }
    IExpr root = engine.evaluate(F.Divide(F.Negate(linear[0]), linear[1]));
    if (root.isInteger()) {
      long k = root.toLongDefault();
      if (k != Long.MIN_VALUE && k != Long.MAX_VALUE) {
        return new Support(k, k);
      }
      return Support.ALL;
    }
    return root.isReal() ? Support.EMPTY : Support.ALL;
  }

  /**
   * The coefficients <code>{c0, c1}</code> of <code>expr == c0 + c1*n</code> with real numbers
   * <code>c0</code> and <code>c1 != 0</code>, or <code>null</code>.
   */
  private static IExpr[] linear(IExpr expr, IExpr n, EvalEngine engine) {
    IExpr expanded = engine.evaluate(F.Expand(expr));
    IExpr[] linear = expanded.linear(n);
    if (linear == null) {
      return null;
    }
    IExpr c0 = engine.evaluate(linear[0]);
    IExpr c1 = engine.evaluate(linear[1]);
    if (!c0.isReal() || !c1.isReal() || c1.isZero()) {
      return null;
    }
    return new IExpr[] {c0, c1};
  }

  private static IExpr reverse(IExpr operator) {
    if (operator == S.Less) {
      return S.Greater;
    }
    if (operator == S.LessEqual) {
      return S.GreaterEqual;
    }
    if (operator == S.Greater) {
      return S.Less;
    }
    if (operator == S.GreaterEqual) {
      return S.LessEqual;
    }
    return operator;
  }

  /** A bound too large for a <code>long</code> is no bound. */
  private static long toLong(IExpr integer, long noBound) {
    if (integer.isInteger()) {
      long value = integer.toLongDefault();
      if (value > Long.MIN_VALUE / 2 && value < Long.MAX_VALUE / 2) {
        return value;
      }
    }
    return noBound;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, new IBuiltInSymbol[] {S.FourierParameters},
        new IExpr[] {S.Automatic});
  }
}
