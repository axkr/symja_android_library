package org.matheclipse.core.reduce;

import java.math.BigInteger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>Reduce(formula, x, Reals)</code> for a formula in which <code>x</code> occurs only in
 * powers <code>x^k</code> whose exponents have a common divisor <code>g &gt; 1</code>, such as
 * <code>x^600 - 5*x^300 + 6 == 0</code>, of degree at most 4 in <code>x^g</code>. The formula is
 * reduced for <code>y = x^g</code>, and every condition on <code>y</code> becomes one on
 * <code>x</code>: <code>x^g</code> is increasing for an odd <code>g</code>, and for an even
 * <code>g</code> it is <code>|x|^g</code>, which is never negative.
 */
public final class PowerSubstitution {

  /** Below this degree the polynomial is reduced as it is. */
  private static final int MIN_DEGREE = 5;

  /**
   * The largest degree in <code>y</code>: its roots are radicals then, whose real <code>g</code>-th
   * roots are radicals again. A higher degree is left to the real root isolation.
   */
  private static final int MAX_DEGREE_IN_Y = 4;

  private PowerSubstitution() {}

  /**
   * @return the reduced formula, or {@link F#NIL} if the formula isn't one in a power of
   *         <code>x</code> or its reduction in that power has no form this can turn back
   */
  public static IExpr reduce(IExpr formula, ISymbol x, EvalEngine engine) {
    long[] exponents = {0, 0};
    if (!collectExponents(formula, x, exponents) || exponents[0] < 2
        || exponents[1] < MIN_DEGREE || exponents[1] / exponents[0] > MAX_DEGREE_IN_Y) {
      return F.NIL;
    }
    final long g = exponents[0];
    final ISymbol y = F.Dummy("y");
    IExpr inY = formula.replaceAll(e -> {
      if (e.isPower() && e.base().equals(x) && e.exponent().isInteger()) {
        long k = e.exponent().toLongDefault();
        return k / g == 1 ? y : F.Power(y, F.ZZ(k / g));
      }
      return F.NIL;
    });
    if (inY.isNIL() || !inY.isFree(x)) {
      return F.NIL;
    }
    IExpr reducedInY = engine.evaluate(F.Reduce(inY, y, S.Reals));
    if (reducedInY.isAST(S.Reduce)) {
      return F.NIL;
    }
    IExpr inX = toX(reducedInY, x, y, g, engine);
    if (inX.isNIL()) {
      return F.NIL;
    }
    if (inX.isTrue() || inX.isFalse()) {
      return inX;
    }
    // the conditions on x are linear: their reduction orders and merges them
    IExpr result = engine.evaluate(F.Reduce(inX, x, S.Reals));
    return result.isAST(S.Reduce) ? F.NIL : ascendingRoots(result, x);
  }

  /**
   * The disjunction <code>x == r1 || x == r2 || ...</code> with the rational roots first in
   * ascending order, then the others by their absolute value, the negative one of a pair first:
   * <code>x == -1 || x == 1 || x == -2^(1/500) || x == 2^(1/500)</code>.
   */
  private static IExpr ascendingRoots(IExpr result, ISymbol x) {
    if (!result.isOr() || !((IAST) result).forAll(
        e -> e.isEqual() && e.first().equals(x) && e.second().isRealResult())) {
      return result;
    }
    IASTAppendable sorted = ((IAST) result).copyAppendable();
    try {
      sorted.sortInplace((a, b) -> {
        boolean rationalA = a.second().isRational();
        boolean rationalB = b.second().isRational();
        if (rationalA != rationalB) {
          return rationalA ? -1 : 1;
        }
        double va = a.second().evalf();
        double vb = b.second().evalf();
        if (!rationalA) {
          int byAbs = Double.compare(Math.abs(va), Math.abs(vb));
          if (byAbs != 0) {
            return byAbs;
          }
        }
        return Double.compare(va, vb);
      });
    } catch (RuntimeException rex) {
      return result;
    }
    return sorted;
  }

  /**
   * The greatest common divisor of the exponents of <code>x</code> in <code>exponents[0]</code>
   * and the largest exponent in <code>exponents[1]</code>.
   *
   * @return <code>false</code> if <code>x</code> occurs other than as the base of a positive
   *         integer power
   */
  private static boolean collectExponents(IExpr expr, ISymbol x, long[] exponents) {
    if (expr.equals(x)) {
      return false;
    }
    if (!expr.isAST()) {
      return true;
    }
    IAST ast = (IAST) expr;
    if (ast.isPower() && ast.base().equals(x)) {
      long k = ast.exponent().isInteger() ? ast.exponent().toLongDefault() : -1;
      if (k <= 0 || k == Long.MIN_VALUE) {
        return false;
      }
      exponents[0] = BigInteger.valueOf(exponents[0]).gcd(BigInteger.valueOf(k)).longValue();
      exponents[1] = Math.max(exponents[1], k);
      return true;
    }
    for (int i = 0; i < ast.size(); i++) {
      if (!collectExponents(ast.get(i), x, exponents)) {
        return false;
      }
    }
    return true;
  }

  /** The conditions on <code>y = x^g</code> as conditions on <code>x</code>, or {@link F#NIL}. */
  private static IExpr toX(IExpr expr, ISymbol x, ISymbol y, long g, EvalEngine engine) {
    if (expr.isTrue() || expr.isFalse()) {
      return expr;
    }
    if (expr.isAnd() || expr.isOr()) {
      IAST junction = (IAST) expr;
      IASTAppendable result = F.ast(junction.head(), junction.argSize());
      for (IExpr arg : junction) {
        IExpr inX = toX(arg, x, y, g, engine);
        if (inX.isNIL()) {
          return F.NIL;
        }
        result.append(inX);
      }
      return result;
    }
    if (expr.isAST(S.Inequality, 6) && expr.getAt(3).equals(y)) {
      // a < y < b
      IAST inequality = (IAST) expr;
      IExpr lower = toX(F.binaryAST2(inequality.arg2(), inequality.arg1(), y), x, y, g, engine);
      IExpr upper = toX(F.binaryAST2(inequality.arg4(), y, inequality.arg5()), x, y, g, engine);
      return lower.isPresent() && upper.isPresent() ? F.And(lower, upper) : F.NIL;
    }
    if (!expr.isAST2()) {
      return F.NIL;
    }
    IAST relation = (IAST) expr;
    IExpr head = relation.head();
    if (relation.arg1().equals(y) && relation.arg2().isFree(y)) {
      return atom(head, relation.arg2(), x, g, engine);
    }
    if (relation.arg2().equals(y) && relation.arg1().isFree(y)) {
      IExpr flipped = flip(head);
      return flipped.isPresent() ? atom(flipped, relation.arg1(), x, g, engine) : F.NIL;
    }
    return F.NIL;
  }

  /** The head of <code>c op y</code> written as <code>y op' c</code>. */
  private static IExpr flip(IExpr head) {
    if (head == S.Less) {
      return S.Greater;
    }
    if (head == S.LessEqual) {
      return S.GreaterEqual;
    }
    if (head == S.Greater) {
      return S.Less;
    }
    if (head == S.GreaterEqual) {
      return S.LessEqual;
    }
    return head == S.Equal || head == S.Unequal ? head : F.NIL;
  }

  /** <code>y op c</code> with <code>y = x^g</code> as a condition on <code>x</code>. */
  private static IExpr atom(IExpr head, IExpr c, ISymbol x, long g, EvalEngine engine) {
    if (!c.isRealResult() || !c.isNumericFunction(true)) {
      return F.NIL;
    }
    int sign = c.isZero() ? 0 : engine.evaluate(F.Sign(c)).toIntDefault();
    if (sign != -1 && sign != 0 && sign != 1) {
      return F.NIL;
    }
    IExpr exponent = F.QQ(1, g);
    if (g % 2 == 1) {
      // increasing: the real g-th root keeps the sign
      IExpr root = sign >= 0 ? F.Power(c, exponent) : F.Negate(F.Power(F.Negate(c), exponent));
      return F.binaryAST2(head, x, engine.evaluate(root));
    }
    IExpr r = sign > 0 ? engine.evaluate(F.Power(c, exponent)) : F.C0;
    IExpr minusR = engine.evaluate(F.Negate(r));
    if (head == S.Equal) {
      return sign < 0 ? S.False
          : sign == 0 ? F.Equal(x, F.C0) : F.Or(F.Equal(x, minusR), F.Equal(x, r));
    }
    if (head == S.Unequal) {
      return sign < 0 ? S.True
          : sign == 0 ? F.Unequal(x, F.C0) : F.And(F.Unequal(x, minusR), F.Unequal(x, r));
    }
    if (head == S.Less) {
      return sign <= 0 ? S.False : F.And(F.Greater(x, minusR), F.Less(x, r));
    }
    if (head == S.LessEqual) {
      return sign < 0 ? S.False
          : sign == 0 ? F.Equal(x, F.C0) : F.And(F.GreaterEqual(x, minusR), F.LessEqual(x, r));
    }
    if (head == S.Greater) {
      return sign < 0 ? S.True
          : sign == 0 ? F.Unequal(x, F.C0) : F.Or(F.Less(x, minusR), F.Greater(x, r));
    }
    if (head == S.GreaterEqual) {
      return sign <= 0 ? S.True : F.Or(F.LessEqual(x, minusR), F.GreaterEqual(x, r));
    }
    return F.NIL;
  }
}
