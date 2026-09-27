package org.matheclipse.core.sympy.simplify;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;

/**
 * Simplification of expressions with square roots. Ported from
 * <a href="https://github.com/sympy/sympy/blob/master/sympy/simplify/radsimp.py">sympy/simplify/radsimp.py</a>
 *
 * <p>
 * Only the functions for sums of numeric surds (terms whose squares are positive rationals) are
 * ported.
 */
public class RadSimp {

  private RadSimp() {}

  static IExpr mexpand(IExpr expr) {
    return EvalEngine.get().evaluate(F.ExpandAll(expr));
  }

  /**
   * Split a term <code>c*Sqrt(n)</code>, whose square is a positive rational, into the rational
   * coefficient <code>c</code> and the square free positive integer <code>n</code>.
   *
   * @return <code>null</code> if the square of the term isn't a positive rational number
   */
  static IExpr[] surdTerm(IExpr term) {
    EvalEngine engine = EvalEngine.get();
    if (term.isRational()) {
      return term.isZero() ? null : new IExpr[] {term, F.C1};
    }
    IExpr square = mexpand(F.Sqr(term));
    if (!square.isRational() || !square.isPositive()) {
      return null;
    }
    double value;
    try {
      value = term.evalf();
    } catch (RuntimeException rex) {
      return null;
    }
    if (Double.isNaN(value) || value == 0.0) {
      return null;
    }
    // term^2 = p/q => term = sign*Sqrt(p*q)/q
    IInteger p = ((IRational) square).numerator();
    IInteger q = ((IRational) square).denominator();
    IExpr root = engine.evaluate(F.Sqrt(p.multiply(q)));
    // root == k * Sqrt(m) with m square free
    IExpr k = F.C1;
    IExpr m = F.C1;
    if (root.isInteger()) {
      k = root;
    } else if (root.isSqrt() && root.base().isInteger()) {
      m = root.base();
    } else if (root.isTimes() && root.argSize() == 2 && root.first().isInteger()
        && root.second().isSqrt() && root.second().base().isInteger()) {
      k = root.first();
      m = root.second().base();
    } else {
      return null;
    }
    IExpr coefficient = engine.evaluate(F.Divide(k, q));
    if (value < 0.0) {
      coefficient = coefficient.negate();
    }
    return new IExpr[] {coefficient, m};
  }

  /**
   * Test if the expression is a sum of terms whose squares are positive rationals.
   */
  static boolean isSurdSum(IExpr expr) {
    if (expr.isPlus()) {
      IAST plus = (IAST) expr;
      for (int i = 1; i < plus.size(); i++) {
        if (surdTerm(plus.get(i)) == null) {
          return false;
        }
      }
      return true;
    }
    return surdTerm(expr) != null;
  }

  /**
   * Split the list of integers <code>a</code> into a list of integers, <code>a1</code> having
   * <code>g = gcd(a1)</code>, and a list <code>a2</code> whose elements are not divisible by
   * <code>g</code>.
   *
   * @return the gcd <code>g</code>
   */
  static IInteger splitGcd(List<IInteger> a, List<IInteger> a1, List<IInteger> a2) {
    // >>> from sympy.simplify.radsimp import _split_gcd
    // >>> _split_gcd(55, 35, 22, 14, 77, 10)
    // (5, [55, 35, 10], [22, 14, 77])
    IInteger g = a.get(0);
    a1.add(g);
    for (int i = 1; i < a.size(); i++) {
      IInteger x = a.get(i);
      IInteger g1 = g.gcd(x);
      if (g1.isOne()) {
        a2.add(x);
      } else {
        g = g1;
        a1.add(x);
      }
    }
    return g;
  }

  /**
   * Split an expression with terms whose squares are positive rationals into a sum of terms whose
   * surds squared have gcd equal to <code>g</code> and a sum of terms with surds squared prime
   * with <code>g</code>.
   *
   * @return <code>{g, a, b}</code> with <code>expr == Sqrt(g)*a + b</code> or <code>null</code>
   *         if the expression is not a sum of surds
   */
  public static IExpr[] splitSurds(IExpr expr) {
    // >>> from sympy import sqrt
    // >>> from sympy.simplify.radsimp import split_surds
    // >>> split_surds(3*sqrt(3) + sqrt(5)/7 + sqrt(6) + sqrt(10) + sqrt(15))
    // (3, sqrt(2) + sqrt(5) + 3, sqrt(5)/7 + sqrt(10))
    IAST args = expr.isPlus() ? (IAST) expr : F.Plus(expr);
    List<IExpr[]> coeffMuls = new ArrayList<IExpr[]>(args.argSize());
    List<IInteger> surds = new ArrayList<IInteger>(args.argSize());
    for (int i = 1; i < args.size(); i++) {
      IExpr[] term = surdTerm(args.get(i));
      if (term == null) {
        return null;
      }
      coeffMuls.add(term);
      if (!term[1].isOne() && !surds.contains(term[1])) {
        surds.add((IInteger) term[1]);
      }
    }
    if (surds.isEmpty()) {
      return new IExpr[] {F.C1, F.C0, expr};
    }
    surds.sort((x, y) -> x.compareTo(y));
    List<IInteger> b1 = new ArrayList<IInteger>();
    List<IInteger> b2 = new ArrayList<IInteger>();
    IInteger g = splitGcd(surds, b1, b2);
    IInteger g2 = g;
    if (b2.isEmpty() && b1.size() >= 2) {
      // only a common factor has been factored; split again
      List<IInteger> b1n = new ArrayList<IInteger>();
      for (IInteger x : b1) {
        IInteger quotient = x.quotient(g);
        if (!quotient.isOne()) {
          b1n.add(quotient);
        }
      }
      if (!b1n.isEmpty()) {
        List<IInteger> b1nSplit = new ArrayList<IInteger>();
        b2 = new ArrayList<IInteger>();
        IInteger g1 = splitGcd(b1n, b1nSplit, b2);
        g2 = g.multiply(g1);
        b1 = new ArrayList<IInteger>();
        for (IInteger x : b1nSplit) {
          b1.add(x.multiply(g));
        }
      }
    }
    EvalEngine engine = EvalEngine.get();
    IASTAppendable a1v = F.PlusAlloc(coeffMuls.size());
    IASTAppendable a2v = F.PlusAlloc(coeffMuls.size());
    for (IExpr[] term : coeffMuls) {
      IExpr c = term[0];
      IExpr s = term[1];
      if (!s.isOne() && b1.contains(s) && ((IInteger) s).mod(g2).isZero()) {
        // a1v.append(c*sqrt(s1/g2))
        a1v.append(F.Times(c, F.Sqrt(((IInteger) s).quotient(g2))));
      } else {
        a2v.append(F.Times(c, F.Sqrt(s)));
      }
    }
    return new IExpr[] {g2, engine.evaluate(a1v), engine.evaluate(a2v)};
  }

  /**
   * Rationalize <code>num/den</code> by removing square roots in the denominator;
   * <code>num</code> and <code>den</code> are sum of terms whose squares are positive rationals.
   *
   * @return <code>{num, den}</code>
   */
  public static IExpr[] radRationalize(IExpr num, IExpr den) {
    // >>> rad_rationalize(sqrt(3), 1 + sqrt(2)/3)
    // (-sqrt(3) + sqrt(6)/3, -7/9)
    int counter = 0;
    while (den.isPlus() && counter++ < 64) {
      IExpr[] split = splitSurds(den);
      if (split == null) {
        break;
      }
      IExpr a = F.Times(split[1], F.Sqrt(split[0]));
      IExpr b = split[2];
      // num = _mexpand((a - b)*num)
      // den = _mexpand(a**2 - b**2)
      num = mexpand(F.Times(F.Subtract(a, b), num));
      den = mexpand(F.Subtract(F.Sqr(a), F.Sqr(b)));
    }
    return new IExpr[] {num, den};
  }

  /**
   * Rationalize the denominator by removing square roots.
   *
   * <p>
   * <b>Note:</b> only denominators which are sums of terms whose squares are positive rationals
   * are rationalized.
   */
  public static IExpr radsimp(IExpr expr) {
    // >>> radsimp(1/(2 + sqrt(2)))
    // (2 - sqrt(2))/2
    EvalEngine engine = EvalEngine.get();
    IExpr together = engine.evaluate(F.Together(expr));
    IExpr num = engine.evaluate(F.Numerator(together));
    IExpr den = engine.evaluate(F.Denominator(together));
    if (den.isOne()) {
      return expr;
    }
    den = mexpand(den);
    if (den.isRational()) {
      return mexpand(F.Times(num, F.Power(den, F.CN1)));
    }
    if (!isSurdSum(den)) {
      return expr;
    }
    IExpr[] rationalized = radRationalize(num, den);
    num = rationalized[0];
    den = rationalized[1];
    IExpr[] term = surdTerm(den);
    if (term != null && !term[1].isOne()) {
      // c*Sqrt(m) in the denominator
      num = mexpand(F.Times(num, F.Sqrt(term[1])));
      den = engine.evaluate(F.Times(term[0], term[1]));
    }
    if (!den.isRational()) {
      return expr;
    }
    return mexpand(F.Times(num, F.Power(den, F.CN1)));
  }

  /** Test if <code>head</code> of the expression is one of the radical free operations. */
  static boolean isPlusTimes(IExpr expr) {
    return expr.isPlus() || expr.isTimes() || expr.isAST(S.Plus) || expr.isAST(S.Times);
  }
}
