package org.matheclipse.core.sympy.simplify;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

/**
 * Denest nested square roots. Ported from
 * <a href="https://github.com/sympy/sympy/blob/master/sympy/simplify/sqrtdenest.py">sympy/simplify/sqrtdenest.py</a>
 *
 * <p>
 * The symbolic denesting (<code>_sqrt_symbolic_denest</code>) of sympy isn't ported, because it
 * depends on the assumptions of the symbols.
 */
public class SqrtDenest {

  /** The result of the recursive <code>_denester</code> function */
  private static final class Denested {
    final IExpr d;
    final int[] f;

    Denested(IExpr d, int[] f) {
      this.d = d;
      this.f = f;
    }
  }

  private SqrtDenest() {}

  private static IExpr eval(IExpr expr) {
    return EvalEngine.get().evaluate(expr);
  }

  private static IExpr mexpand(IExpr expr) {
    return RadSimp.mexpand(expr);
  }

  private static IExpr sqrt(IExpr expr) {
    if (expr.isRational() && !expr.isNegative()) {
      // the evaluation of Sqrt() doesn't detect a big perfect square
      BigInteger numerator = ((IRational) expr).toBigNumerator();
      BigInteger denominator = ((IRational) expr).toBigDenominator();
      BigInteger numeratorRoot = numerator.sqrt();
      BigInteger denominatorRoot = denominator.sqrt();
      if (numeratorRoot.multiply(numeratorRoot).equals(numerator)
          && denominatorRoot.multiply(denominatorRoot).equals(denominator)) {
        return F.QQ(numeratorRoot, denominatorRoot);
      }
    }
    return eval(F.Sqrt(expr));
  }

  /**
   * Split the square root <code>Sqrt(radicand)</code> of a sum into <code>
   * Sqrt(content) * Sqrt(primitive)</code>, where the positive rational <code>content</code> is
   * the gcd of the rational coefficients of the terms in the radicand.
   *
   * <p>
   * <b>Note:</b> sympy doesn't combine <code>sqrt(2)*sqrt(2+sqrt(2))</code> automatically; the
   * evaluation in Symja returns <code>Sqrt(4+2*Sqrt(2))</code>.
   *
   * @return <code>{Sqrt(content), primitive}</code>
   */
  private static IExpr[] primitiveRadicand(IExpr radicand) {
    if (radicand.isPlus()) {
      IAST plus = (IAST) radicand;
      IExpr content = F.C0;
      for (int i = 1; i < plus.size(); i++) {
        IExpr term = plus.get(i);
        IExpr coefficient = F.C1;
        if (term.isRational()) {
          coefficient = term;
        } else if (term.isTimes() && term.first().isRational()) {
          coefficient = term.first();
        }
        content = eval(F.GCD(content, coefficient));
      }
      if (content.isRational() && content.isPositive() && !content.isOne()) {
        return new IExpr[] {sqrt(content), mexpand(F.Times(F.Power(content, F.CN1), radicand))};
      }
    }
    return new IExpr[] {F.C1, radicand};
  }

  /**
   * Split a term into <code>multiplier * Sqrt(primitive)</code>, where
   * <code>Sqrt(primitive)</code> has the square root nesting <code>depth</code> and the
   * <code>multiplier</code> has a smaller nesting depth.
   *
   * @return <code>{multiplier, primitive}</code> or <code>null</code> if the term doesn't have
   *         this form
   */
  private static IExpr[] splitNestedTerm(IExpr term, int depth) {
    IAST factors = term.isTimes() ? (IAST) term : F.Times(term);
    IASTAppendable multiplier = F.TimesAlloc(factors.size());
    IExpr radicand = null;
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (sqrtDepth(factor) < depth) {
        multiplier.append(factor);
      } else if (radicand == null && factor.isSqrt()) {
        radicand = factor.base();
      } else {
        return null;
      }
    }
    if (radicand == null) {
      return null;
    }
    IExpr[] primitive = primitiveRadicand(radicand);
    multiplier.append(primitive[0]);
    return new IExpr[] {eval(multiplier), primitive[1]};
  }

  /** The numeric value of a real expression or <code>NaN</code> */
  private static double numeric(IExpr expr) {
    try {
      return expr.evalf();
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Double.NaN;
    }
  }

  /**
   * Return <code>true</code> if expr is a sqrt, otherwise <code>false</code>.
   */
  public static boolean isSqrt(IExpr expr) {
    // return expr.is_Pow and expr.exp.is_Rational and abs(expr.exp) is S.Half
    return expr.isPower() && (expr.exponent().equals(F.C1D2) || expr.exponent().equals(F.CN1D2));
  }

  /**
   * Return the maximum depth of any square root argument of <code>p</code>.
   */
  public static int sqrtDepth(IExpr p) {
    // >>> sqrt_depth(1 + sqrt(2)*(1 + sqrt(3)))
    // 1
    // >>> sqrt_depth(1 + sqrt(2)*sqrt(1 + sqrt(3)))
    // 2
    if (p.isNumber()) {
      // if p is S.ImaginaryUnit: return 1
      return p.isRealResult() ? 0 : 1;
    }
    if (p.isAtom()) {
      return 0;
    }
    if (p.isPlus() || p.isTimes()) {
      IAST ast = (IAST) p;
      int max = 0;
      for (int i = 1; i < ast.size(); i++) {
        max = Math.max(max, sqrtDepth(ast.get(i)));
      }
      return max;
    }
    if (isSqrt(p)) {
      return sqrtDepth(p.base()) + 1;
    }
    return 0;
  }

  /**
   * Return <code>true</code> if <code>p</code> is comprised of only rationals or square roots of
   * rationals and algebraic operations.
   */
  public static boolean isAlgebraic(IExpr p) {
    // >>> is_algebraic(sqrt(2)*(3/(sqrt(7) + sqrt(5)*sqrt(2))))
    // True
    // >>> is_algebraic(sqrt(2)*(3/(sqrt(7) + sqrt(5)*cos(2))))
    // False
    if (p.isRational()) {
      return true;
    } else if (p.isAtom()) {
      return false;
    } else if (isSqrt(p) || (p.isPower() && p.exponent().isInteger())) {
      return isAlgebraic(p.base());
    } else if (p.isPlus() || p.isTimes()) {
      IAST ast = (IAST) p;
      for (int i = 1; i < ast.size(); i++) {
        if (!isAlgebraic(ast.get(i))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Returns all possible subsets of the set <code>(0, 1, ..., n-1)</code> except the empty set,
   * listed in reversed lexicographical order according to binary representation, so that the case
   * of the fourth root is treated last.
   */
  static List<int[]> subsets(int n) {
    // >>> _subsets(2)
    // [[1, 0], [0, 1], [1, 1]]
    List<int[]> a = new ArrayList<int[]>();
    if (n == 1) {
      a.add(new int[] {1});
    } else if (n == 2) {
      a.add(new int[] {1, 0});
      a.add(new int[] {0, 1});
      a.add(new int[] {1, 1});
    } else if (n == 3) {
      a.add(new int[] {1, 0, 0});
      a.add(new int[] {0, 1, 0});
      a.add(new int[] {1, 1, 0});
      a.add(new int[] {0, 0, 1});
      a.add(new int[] {1, 0, 1});
      a.add(new int[] {0, 1, 1});
      a.add(new int[] {1, 1, 1});
    } else {
      List<int[]> b = subsets(n - 1);
      // a0 = [x + [0] for x in b]
      for (int[] x : b) {
        a.add(java.util.Arrays.copyOf(x, n));
      }
      // [[0]*(n - 1) + [1]]
      int[] last = new int[n];
      last[n - 1] = 1;
      a.add(last);
      // a1 = [x + [1] for x in b]
      for (int[] x : b) {
        int[] x1 = java.util.Arrays.copyOf(x, n);
        x1[n - 1] = 1;
        a.add(x1);
      }
    }
    return a;
  }

  /**
   * Denests sqrts in an expression that contain other square roots if possible, otherwise returns
   * the expr unchanged. This is based on the algorithms of [1].
   *
   * <ul>
   * <li>[1] https://web.archive.org/web/20210806201615/https://researcher.watson.ibm.com/researcher/files/us-fagin/symb85.pdf
   * <li>[2] D. J. Jeffrey and A. D. Rich, 'Symplifying Square Roots of Square Roots by Denesting'
   * </ul>
   */
  public static IExpr sqrtdenest(IExpr expr) {
    return sqrtdenest(expr, 3);
  }

  public static IExpr sqrtdenest(IExpr expr, int maxIter) {
    // >>> sqrtdenest(sqrt(5 + 2 * sqrt(6)))
    // sqrt(2) + sqrt(3)
    expr = eval(F.ExpandAll(expr));
    for (int i = 0; i < maxIter; i++) {
      IExpr z = sqrtdenest0(expr);
      if (expr.equals(z)) {
        return expr;
      }
      expr = z;
    }
    return expr;
  }

  /**
   * Return <code>{a, b, r}</code> for <code>p.match(a + b*sqrt(r))</code> where, in addition to
   * matching, <code>sqrt(r)</code> also has then maximal <code>sqrt_depth</code> among addends of
   * <code>p</code>.
   *
   * @return <code>null</code> if there is no match
   */
  static IExpr[] sqrtMatch(IExpr p) {
    // >>> _sqrt_match(1 + sqrt(2) + sqrt(2)*sqrt(3) + 2*sqrt(1+sqrt(5)))
    // [1 + sqrt(2) + sqrt(6), 2, 1 + sqrt(5)]
    p = mexpand(p);
    if (p.isNumber()) {
      return new IExpr[] {p, F.C0, F.C0};
    } else if (p.isPlus()) {
      IAST pargs = (IAST) p;
      if (RadSimp.isSurdSum(p)) {
        // r, b, a = split_surds(p)
        IExpr[] split = RadSimp.splitSurds(p);
        if (split != null) {
          return new IExpr[] {split[2], split[1], split[0]};
        }
      }
      // to make the process canonical, the argument is included in the tuple
      // so when the max is selected, it will be the largest arg having a
      // given depth
      int[] depths = new int[pargs.size()];
      int depth = 0;
      int index = -1;
      for (int i = 1; i < pargs.size(); i++) {
        depths[i] = sqrtDepth(pargs.get(i));
        if (depths[i] >= depth) {
          depth = depths[i];
          index = i;
        }
      }
      if (depth == 0) {
        return null;
      }
      // select r
      IExpr[] selected = splitNestedTerm(pargs.get(index), depth);
      if (selected == null) {
        return null;
      }
      IExpr r = selected[1];
      // collect terms containing r
      IASTAppendable a1 = F.PlusAlloc(pargs.size());
      IASTAppendable b1 = F.PlusAlloc(pargs.size());
      b1.append(selected[0]);
      for (int i = 1; i < pargs.size(); i++) {
        if (i == index) {
          continue;
        }
        IExpr x1 = pargs.get(i);
        if (depths[i] < depth) {
          a1.append(x1);
        } else {
          IExpr[] split = splitNestedTerm(x1, depth);
          if (split != null && split[1].equals(r)) {
            b1.append(split[0]);
          } else {
            a1.append(x1);
          }
        }
      }
      return new IExpr[] {eval(a1), eval(b1), r};
    } else {
      // b, r = p.as_coeff_Mul()
      IExpr b = F.C1;
      IExpr r = p;
      if (p.isTimes() && p.first().isNumber()) {
        b = p.first();
        r = ((IAST) p).rest().oneIdentity1();
      }
      if (r.isSqrt()) {
        IExpr[] primitive = primitiveRadicand(r.base());
        return new IExpr[] {F.C0, eval(F.Times(b, primitive[0])), primitive[1]};
      }
      return null;
    }
  }

  /** Returns expr after denesting its arguments. */
  private static IExpr sqrtdenest0(IExpr expr) {
    if (isSqrt(expr)) {
      if (expr.exponent().equals(F.C1D2)) {
        // n is a square root
        IExpr n = expr;
        if (n.base().isPlus()) {
          IAST args = (IAST) n.base();
          boolean allIntegerSquares = args.argSize() > 2;
          if (allIntegerSquares) {
            for (int i = 1; i < args.size(); i++) {
              if (!mexpand(F.Sqr(args.get(i))).isInteger()) {
                allIntegerSquares = false;
                break;
              }
            }
          }
          if (allIntegerSquares) {
            IExpr result = sqrtdenestRec(n);
            if (result.isPresent()) {
              return result;
            }
          }
          IASTAppendable plus = F.PlusAlloc(args.argSize());
          for (int i = 1; i < args.size(); i++) {
            plus.append(sqrtdenest0(args.get(i)));
          }
          expr = sqrt(mexpand(plus));
        }
        return sqrtdenest1(expr, true);
      }
      // n, d = [_sqrtdenest0(i) for i in (n, d)]
      // return n/d
      IExpr denominator = sqrt(expr.base());
      IExpr d = sqrtdenest0(denominator);
      if (d.equals(denominator)) {
        return expr;
      }
      return RadSimp.radsimp(eval(F.Power(d, F.CN1)));
    }
    if (expr.isPlus()) {
      IAST plus = (IAST) expr;
      List<IExpr> cs = new ArrayList<IExpr>(plus.argSize());
      List<IExpr> args = new ArrayList<IExpr>(plus.argSize());
      boolean ratcomb = true;
      for (int i = 1; i < plus.size(); i++) {
        IExpr arg = plus.get(i);
        IExpr c = F.C1;
        IExpr a = arg;
        if (arg.isTimes() && arg.first().isNumber()) {
          c = arg.first();
          a = ((IAST) arg).rest().oneIdentity1();
        }
        if (!c.isRational() || !isSqrt(a) || !a.exponent().equals(F.C1D2)) {
          ratcomb = false;
          break;
        }
        cs.add(c);
        args.add(a);
      }
      if (ratcomb) {
        return sqrtRatcomb(cs, args);
      }
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      if (ast.argSize() > 0 && ast.head().isSymbol()) {
        IASTAppendable result = F.ast(ast.head(), ast.argSize());
        for (int i = 1; i < ast.size(); i++) {
          result.append(sqrtdenest0(ast.get(i)));
        }
        return eval(result);
      }
    }
    return expr;
  }

  /**
   * Helper that denests the square root of three or more surds.
   *
   * <p>
   * Algorithm: <code>expr.base</code> is in the extension <code>Q_m = Q(sqrt(r_1),..,sqrt(r_k))
   * </code>; split <code>expr.base = a + b*sqrt(r_k)</code>, where <code>a</code> and
   * <code>b</code> are on <code>Q_(m-1) = Q(sqrt(r_1),..,sqrt(r_(k-1)))</code>; then
   * <code>a**2 - b**2*r_k</code> is on <code>Q_(m-1)</code>; denest
   * <code>sqrt(a**2 - b**2*r_k)</code> and so on.
   *
   * @return {@link F#NIL} if it fails to denest (<code>SqrtdenestStopIteration</code> in sympy)
   */
  private static IExpr sqrtdenestRec(IExpr expr) {
    // >>> _sqrtdenest_rec(sqrt(-72*sqrt(2) + 158*sqrt(5) + 498))
    // -sqrt(10) + sqrt(2) + 9 + 9*sqrt(5)
    // >>> w=-6*sqrt(55)-6*sqrt(35)-2*sqrt(22)-2*sqrt(14)+2*sqrt(77)+6*sqrt(10)+65
    // >>> _sqrtdenest_rec(sqrt(w))
    // -sqrt(11) - sqrt(7) + sqrt(2) + 3*sqrt(5)
    if (!expr.isPower()) {
      return sqrtdenest(expr);
    }
    double baseValue = numeric(expr.base());
    if (Double.isNaN(baseValue)) {
      return F.NIL;
    }
    if (baseValue < 0) {
      IExpr result = sqrtdenestRec(sqrt(eval(F.Negate(expr.base()))));
      return result.isPresent() ? eval(F.Times(F.CI, result)) : F.NIL;
    }
    IExpr[] split = RadSimp.splitSurds(expr.base());
    if (split == null) {
      return F.NIL;
    }
    IExpr a = eval(F.Times(split[1], F.Sqrt(split[0])));
    IExpr b = split[2];
    if (numeric(a) < numeric(b)) {
      IExpr t = a;
      a = b;
      b = t;
    }
    IExpr c2 = mexpand(F.Subtract(F.Sqr(a), F.Sqr(b)));
    IExpr c;
    if (c2.isPlus() && c2.argSize() > 2) {
      split = RadSimp.splitSurds(c2);
      if (split == null) {
        return F.NIL;
      }
      IExpr a1 = eval(F.Times(split[1], F.Sqrt(split[0])));
      IExpr b1 = split[2];
      if (numeric(a1) < numeric(b1)) {
        IExpr t = a1;
        a1 = b1;
        b1 = t;
      }
      IExpr c2_1 = mexpand(F.Subtract(F.Sqr(a1), F.Sqr(b1)));
      IExpr c_1 = sqrtdenestRec(sqrt(c2_1));
      if (c_1.isNIL()) {
        return F.NIL;
      }
      IExpr d_1 = sqrtdenestRec(sqrt(eval(F.Plus(a1, c_1))));
      if (d_1.isNIL() || !RadSimp.isSurdSum(d_1)) {
        return F.NIL;
      }
      IExpr[] numDen = RadSimp.radRationalize(b1, d_1);
      // c = _mexpand(d_1/sqrt(2) + num/(den*sqrt(2)))
      c = mexpand(F.Plus(F.Times(d_1, F.C1DSqrt2),
          F.Times(numDen[0], F.Power(numDen[1], F.CN1), F.C1DSqrt2)));
    } else {
      c = sqrtdenest1(sqrt(c2), true);
    }
    if (sqrtDepth(c) > 1) {
      return F.NIL;
    }
    IExpr ac = eval(F.Plus(a, c));
    if (ac.argSize() >= expr.base().argSize()) {
      if (ac.leafCount() >= expr.base().leafCount()) {
        return F.NIL;
      }
    }
    IExpr d = sqrtdenest(sqrt(ac));
    if (sqrtDepth(d) > 1 || !RadSimp.isSurdSum(d)) {
      return F.NIL;
    }
    IExpr[] numDen = RadSimp.radRationalize(b, d);
    // r = d/sqrt(2) + num/(den*sqrt(2))
    IExpr r = eval(F.Plus(F.Times(d, F.C1DSqrt2),
        F.Times(numDen[0], F.Power(numDen[1], F.CN1), F.C1DSqrt2)));
    r = RadSimp.radsimp(r);
    return mexpand(r);
  }

  /**
   * Return denested expr after denesting with simpler methods or, that failing, using the
   * denester.
   */
  private static IExpr sqrtdenest1(IExpr expr, boolean denester) {
    if (!isSqrt(expr) || !expr.exponent().equals(F.C1D2)) {
      return expr;
    }
    IExpr a = expr.base();
    if (a.isAtom()) {
      return expr;
    }
    IExpr[] val = sqrtMatch(a);
    if (val == null) {
      return expr;
    }
    a = val[0];
    IExpr b = val[1];
    IExpr r = val[2];
    // try a quick numeric denesting
    IExpr d2 = mexpand(F.Subtract(F.Sqr(a), F.Times(F.Sqr(b), r)));
    if (d2.isRational()) {
      if (d2.isPositive()) {
        IExpr z = sqrtNumericDenest(a, b, r, d2);
        if (z.isPresent()) {
          return z;
        }
      } else {
        // fourth root case
        // sqrtdenest(sqrt(3 + 2*sqrt(3))) =
        // sqrt(2)*3**(1/4)/2 + sqrt(2)*3**(3/4)/2
        IExpr dr2 = mexpand(F.Times(F.CN1, d2, r));
        IExpr dr = sqrt(dr2);
        if (dr.isRational()) {
          IExpr z = sqrtNumericDenest(mexpand(F.Times(b, r)), a, r, dr2);
          if (z.isPresent()) {
            return mexpand(F.Times(z, F.Power(r, F.CN1D4)));
          }
        }
      }
    }
    // else: z = _sqrt_symbolic_denest(a, b, r) isn't ported

    if (!denester || !isAlgebraic(expr)) {
      return expr;
    }
    IExpr res = sqrtBiquadraticDenest(expr, a, b, r, d2);
    if (res.isPresent()) {
      return res;
    }

    // now call to the denester
    IExpr[] av0 = new IExpr[] {a, b, r, d2};
    List<IExpr> nested = new ArrayList<IExpr>();
    nested.add(RadSimp.radsimp(mexpand(F.Sqr(expr))));
    Denested denested = denester(nested, av0, 0, sqrtDepth(expr));
    if (av0[1] == null) {
      return expr;
    }
    IExpr z = denested.d;
    if (z != null) {
      if (sqrtDepth(z) == sqrtDepth(expr) && z.leafCount() > expr.leafCount()) {
        return expr;
      }
      return z;
    }
    return expr;
  }

  /**
   * Helper that denest <code>sqrt(a + b*sqrt(r))</code>, with <code>d2 = a**2 - b**2*r &gt;
   * 0</code>
   *
   * @return {@link F#NIL} if not denested
   */
  private static IExpr sqrtNumericDenest(IExpr a, IExpr b, IExpr r, IExpr d2) {
    IExpr d = sqrt(d2);
    IExpr s = eval(F.Plus(a, d));
    // sqrt_depth(res) <= sqrt_depth(s) + 1
    // sqrt_depth(expr) = sqrt_depth(r) + 2
    // there is denesting if sqrt_depth(s) + 1 < sqrt_depth(r) + 2
    // if s**2 is Number there is a fourth root
    if (sqrtDepth(s) < sqrtDepth(r) + 1 || mexpand(F.Sqr(s)).isRational()) {
      double sValue = numeric(s);
      double bValue = numeric(b);
      if (Double.isNaN(sValue) || Double.isNaN(bValue) || sValue == 0.0 || bValue == 0.0) {
        return F.NIL;
      }
      int s1 = sValue < 0 ? -1 : 1;
      int s2 = bValue < 0 ? -1 : 1;
      if (s1 == -1 && s2 == -1) {
        s1 = 1;
        s2 = 1;
      }
      // res = (s1 * sqrt(a + d) + s2 * sqrt(a - d)) * sqrt(2) / 2
      IExpr res = F.Times(F.C1DSqrt2, F.Plus(F.Times(F.ZZ(s1), F.Sqrt(F.Plus(a, d))),
          F.Times(F.ZZ(s2), F.Sqrt(F.Subtract(a, d)))));
      return mexpand(res);
    }
    return F.NIL;
  }

  /**
   * denest <code>expr = sqrt(a + b*sqrt(r))</code> where a, b, r are linear combinations of
   * square roots of positive rationals on the rationals (SQRR) and r &gt; 0, b != 0, d2 =
   * a**2 - b**2*r &gt; 0
   *
   * <p>
   * If it cannot denest it returns {@link F#NIL}.
   */
  private static IExpr sqrtBiquadraticDenest(IExpr expr, IExpr a, IExpr b, IExpr r, IExpr d2) {
    // >>> z = sqrt((2*sqrt(2) + 4)*sqrt(2 + sqrt(2)) + 5*sqrt(2) + 8)
    // >>> a, b, r = _sqrt_match(z**2)
    // >>> d2 = a**2 - b**2*r
    // >>> sqrt_biquadratic_denest(z, a, b, r, d2)
    // sqrt(2) + sqrt(sqrt(2) + 2) + 2
    double rValue = numeric(r);
    double d2Value = numeric(d2);
    if (Double.isNaN(rValue) || Double.isNaN(d2Value)) {
      return F.NIL;
    }
    if (rValue <= 0 || d2Value < 0 || b.isZero() || sqrtDepth(expr.base()) < 2) {
      return F.NIL;
    }
    IExpr[] abr = new IExpr[] {a, b, r};
    for (IExpr x : abr) {
      if (x.isPlus() && !RadSimp.isSurdSum(x)) {
        return F.NIL;
      }
      if (!x.isPlus() && !x.isRational() && RadSimp.surdTerm(x) == null) {
        return F.NIL;
      }
    }
    IExpr sqd = mexpand(sqrtdenest(sqrt(RadSimp.radsimp(d2))));
    if (sqrtDepth(sqd) > 1) {
      return F.NIL;
    }
    IExpr x1 = eval(F.Plus(F.Times(F.C1D2, a), F.Times(F.C1D2, sqd)));
    IExpr x2 = eval(F.Subtract(F.Times(F.C1D2, a), F.Times(F.C1D2, sqd)));
    // look for a solution A with depth 1
    for (IExpr x : new IExpr[] {x1, x2}) {
      IExpr A = sqrtdenest(sqrt(x));
      if (sqrtDepth(A) > 1) {
        continue;
      }
      IExpr twoA = mexpand(F.Times(F.C2, A));
      if (twoA.isZero() || !(twoA.isRational() || RadSimp.isSurdSum(twoA))) {
        continue;
      }
      IExpr[] numDen = RadSimp.radRationalize(b, twoA);
      IExpr den = numDen[1];
      IExpr[] term = RadSimp.surdTerm(den);
      if (term == null) {
        continue;
      }
      IExpr B = eval(F.Times(numDen[0], F.Power(den, F.CN1)));
      IExpr z = eval(F.Plus(A, F.Times(B, F.Sqrt(r))));
      double zValue = numeric(z);
      if (Double.isNaN(zValue)) {
        continue;
      }
      if (zValue < 0) {
        z = eval(F.Negate(z));
      }
      return mexpand(z);
    }
    return F.NIL;
  }

  private static int[] zeros(int n) {
    return new int[n];
  }

  /**
   * Denests a list of expressions that contain nested square roots.
   *
   * <p>
   * Algorithm based on
   * <a href="https://web.archive.org/web/20210806201615/https://researcher.watson.ibm.com/researcher/files/us-fagin/symb85.pdf">symb85.pdf</a>.
   *
   * <p>
   * It is assumed that all of the elements of 'nested' share the same bottom-level radicand.
   * (This is stated in the paper, on page 177, in the paragraph immediately preceding the
   * algorithm.)
   *
   * <p>
   * When evaluating all of the arguments in parallel, the bottom-level radicand only needs to be
   * denested once. This means that calling _denester with x arguments results in a recursive
   * invocation with x+1 arguments; hence _denester has polynomial complexity.
   *
   * <p>
   * However, if the arguments were evaluated separately, each call would result in two recursive
   * invocations, and the algorithm would have exponential complexity.
   *
   * <p>
   * This is discussed in the paper in the middle paragraph of page 179.
   */
  private static Denested denester(List<IExpr> nested, IExpr[] av0, int h, int maxDepthLevel) {
    final Denested none = new Denested(null, null);
    if (h > maxDepthLevel) {
      return none;
    }
    if (av0[1] == null) {
      return none;
    }
    boolean allNumbers = true;
    for (IExpr n : nested) {
      if (!n.isNumber()) {
        allNumbers = false;
        break;
      }
    }
    final int size = nested.size();
    final IExpr last = nested.get(size - 1);
    if (av0[0] == null && allNumbers) {
      // no arguments are nested
      for (int[] f : subsets(size)) {
        // test subset 'f' of nested
        IASTAppendable times = F.TimesAlloc(size);
        int count = 0;
        for (int i = 0; i < f.length; i++) {
          if (f[i] != 0) {
            times.append(nested.get(i));
            count++;
          }
        }
        IExpr p = mexpand(times);
        if (count > 1 && f[f.length - 1] != 0) {
          p = eval(F.Negate(p));
        }
        IExpr sqp = sqrt(p);
        if (sqp.isRational()) {
          // got a perfect square so return its square root.
          return new Denested(sqp, f);
        }
      }
      // Otherwise, return the radicand from the previous invocation.
      return new Denested(sqrt(last), zeros(size));
    }
    IExpr R = null;
    List<IExpr[]> values = new ArrayList<IExpr[]>();
    List<IExpr> nested2 = new ArrayList<IExpr>();
    if (av0[0] != null) {
      values.add(new IExpr[] {av0[0], av0[1]});
      R = av0[2];
      nested2.add(av0[3]);
      nested2.add(R);
      av0[0] = null;
    } else {
      for (IExpr expr : nested) {
        IExpr[] v = sqrtMatch(expr);
        if (v != null) {
          values.add(v);
        }
      }
      for (IExpr[] v : values) {
        if (!v[2].isZero()) {
          // Since if b=0, r is not defined
          if (R != null) {
            if (!R.equals(v[2])) {
              av0[1] = null;
              return none;
            }
          } else {
            R = v[2];
          }
        }
      }
      if (R == null) {
        // return the radicand from the previous invocation
        return new Denested(sqrt(last), zeros(size));
      }
      for (IExpr[] v : values) {
        nested2.add(eval(F.Subtract(mexpand(F.Sqr(v[0])), mexpand(F.Times(R, F.Sqr(v[1]))))));
      }
      nested2.add(R);
    }
    Denested result = denester(nested2, av0, h + 1, maxDepthLevel);
    IExpr d = result.d;
    int[] f = result.f;
    if (f == null || d == null) {
      return none;
    }
    boolean any = false;
    for (int i = 0; i < size && i < f.length; i++) {
      if (f[i] != 0) {
        any = true;
        break;
      }
    }
    if (!any) {
      IExpr[] v = values.get(values.size() - 1);
      return new Denested(sqrt(eval(F.Plus(v[0], mexpand(F.Times(v[1], d))))), f);
    }
    IASTAppendable times = F.TimesAlloc(size);
    int firstOne = -1;
    for (int i = 0; i < size; i++) {
      if (f[i] != 0) {
        times.append(nested.get(i));
      }
    }
    for (int i = 0; i < f.length; i++) {
      if (f[i] == 1) {
        firstOne = i;
        break;
      }
    }
    IExpr[] v = sqrtMatch(eval(times));
    if (v == null) {
      av0[1] = null;
      return none;
    }
    if (firstOne >= 0 && firstOne < size - 1 && f[size - 1] != 0) {
      v[0] = eval(F.Negate(v[0]));
      v[1] = eval(F.Negate(v[1]));
    }
    if (f.length <= size || f[size] == 0) {
      // Solution denests with square roots
      IExpr vad = mexpand(F.Plus(v[0], d));
      double vadValue = numeric(vad);
      if (Double.isNaN(vadValue)) {
        av0[1] = null;
        return none;
      }
      if (vadValue <= 0) {
        // return the radicand from the previous invocation.
        return new Denested(sqrt(last), zeros(size));
      }
      if (!(sqrtDepth(vad) <= sqrtDepth(R) + 1 || mexpand(F.Sqr(vad)).isNumber())) {
        av0[1] = null;
        return none;
      }
      IExpr sqvad = sqrtdenest1(sqrt(vad), false);
      if (!(sqrtDepth(sqvad) <= sqrtDepth(R) + 1)) {
        av0[1] = null;
        return none;
      }
      IExpr sqvad1 = RadSimp.radsimp(eval(F.Power(sqvad, F.CN1)));
      // res = _mexpand(sqvad/sqrt(2) + (v[1]*sqrt(R)*sqvad1/sqrt(2)))
      IExpr res = mexpand(F.Plus(F.Times(sqvad, F.C1DSqrt2),
          F.Times(v[1], F.Sqrt(R), sqvad1, F.C1DSqrt2)));
      return new Denested(res, f);
    }
    // Solution requires a fourth root
    IExpr s2 = eval(F.Plus(mexpand(F.Times(v[1], R)), d));
    double s2Value = numeric(s2);
    if (Double.isNaN(s2Value)) {
      av0[1] = null;
      return none;
    }
    if (s2Value <= 0) {
      return new Denested(sqrt(last), zeros(size));
    }
    // FR, s = root(_mexpand(R), 4), sqrt(s2)
    IExpr FR = eval(F.Power(mexpand(R), F.C1D4));
    IExpr s = sqrt(s2);
    // return _mexpand(s/(sqrt(2)*FR) + v[0]*FR/(sqrt(2)*s)), f
    return new Denested(mexpand(F.Plus(F.Times(s, F.C1DSqrt2, F.Power(FR, F.CN1)),
        F.Times(v[0], FR, F.C1DSqrt2, F.Power(s, F.CN1)))), f);
  }

  /**
   * Denest rational combinations of radicals. Based on section 5 of [1].
   */
  private static IExpr sqrtRatcomb(List<IExpr> cs, List<IExpr> args) {
    // >>> z = sqrt(1+sqrt(3)) + sqrt(3+3*sqrt(3)) - sqrt(10+6*sqrt(3))
    // >>> sqrtdenest(z)
    // 0
    while (true) {
      // check if there exists a pair of sqrt that can be denested
      IExpr s = null;
      int i1 = -1;
      int i2 = -1;
      final int n = args.size();
      find: for (int i = 0; i < n - 1; i++) {
        for (int j = i + 1; j < n; j++) {
          IExpr s1 = args.get(i).base();
          IExpr s2 = args.get(j).base();
          IExpr p = mexpand(F.Times(s1, s2));
          IExpr sqrtP = sqrt(p);
          IExpr denested = sqrtdenest(sqrtP);
          if (!denested.equals(sqrtP)) {
            s = denested;
            i1 = i;
            i2 = j;
            break find;
          }
        }
      }
      if (s == null) {
        IASTAppendable plus = F.PlusAlloc(n);
        for (int i = 0; i < n; i++) {
          plus.append(F.Times(cs.get(i), args.get(i)));
        }
        return eval(plus);
      }
      IExpr c2 = cs.remove(i2);
      args.remove(i2);
      IExpr a1 = args.get(i1);
      // replace a2 by s/a1
      // cs[i1] += radsimp(c2 * s / a1.base)
      cs.set(i1, eval(F.Plus(cs.get(i1),
          RadSimp.radsimp(eval(F.Times(c2, s, F.Power(a1.base(), F.CN1)))))));
    }
  }
}
