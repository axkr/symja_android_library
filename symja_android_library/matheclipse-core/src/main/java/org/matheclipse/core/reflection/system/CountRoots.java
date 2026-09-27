package org.matheclipse.core.reflection.system;

import java.util.List;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;
import edu.jas.root.Interval;
import edu.jas.root.RealRootsSturm;

/**
 * <pre>
 * <code>CountRoots(polynomial, x)
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the number of real roots of the <code>polynomial</code> in <code>x</code>. Multiple roots
 * are counted with their multiplicity.
 * </p>
 * </blockquote>
 *
 * <pre>
 * <code>CountRoots(polynomial, {x, a, b})
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the number of roots in the closed real interval between <code>a</code> and
 * <code>b</code>.
 * </p>
 * </blockquote>
 */
public class CountRoots extends AbstractFunctionEvaluator {

  public CountRoots() {}

  static BigRational toBigRational(IExpr expr) {
    if (expr.isRational()) {
      IRational r = (IRational) expr;
      return new BigRational(new edu.jas.arith.BigInteger(r.toBigNumerator()),
          new edu.jas.arith.BigInteger(r.toBigDenominator()));
    }
    return null;
  }

  /**
   * Get the isolating intervals of the real roots of the square free polynomial.
   *
   * @return <code>null</code> if the expression isn't a polynomial with rational coefficients
   */
  static List<Interval<BigRational>> realRoots(IExpr polynomial, IExpr x,
      GenPolynomial<BigRational>[] jasPolynomial) {
    try {
      JASConvert<BigRational> jas = new JASConvert<BigRational>(F.List(x), BigRational.ZERO);
      GenPolynomial<BigRational> poly = jas.expr2JAS(polynomial, false);
      if (poly == null || poly.isZERO()) {
        return null;
      }
      jasPolynomial[0] = poly;
      return new RealRootsSturm<BigRational>().realRoots(poly);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return null;
    }
  }

  private static boolean isRoot(GenPolynomial<BigRational> poly, BigRational value) {
    return value != null && PolyUtil.<BigRational>evaluateMain(poly.ring.coFac, poly, value)
        .isZERO();
  }

  /**
   * Count the roots of the square free polynomial in the closed interval
   * <code>[lower, upper]</code>.
   *
   * @param lower <code>null</code> for minus infinity
   * @param upper <code>null</code> for infinity
   */
  private static long countRoots(GenPolynomial<BigRational> poly,
      List<Interval<BigRational>> roots, BigRational lower, BigRational upper) {
    if (lower == null && upper == null) {
      return roots.size();
    }
    RealRootsSturm<BigRational> sturm = new RealRootsSturm<BigRational>();
    final boolean lowerIsRoot = isRoot(poly, lower);
    final boolean upperIsRoot = isRoot(poly, upper);
    long count = 0;
    for (Interval<BigRational> interval : roots) {
      Interval<BigRational> iv = interval;
      for (int i = 0; i < 100000; i++) {
        final boolean lowerInside = lower != null && iv.contains(lower);
        final boolean upperInside = upper != null && iv.contains(upper);
        if (iv.left.compareTo(iv.right) == 0) {
          // an exact rational root
          if ((lower == null || iv.left.compareTo(lower) >= 0)
              && (upper == null || iv.left.compareTo(upper) <= 0)) {
            count++;
          }
          break;
        }
        if (!lowerInside && !upperInside) {
          if ((lower == null || iv.left.compareTo(lower) > 0)
              && (upper == null || iv.right.compareTo(upper) < 0)) {
            count++;
          }
          break;
        }
        if (lowerInside && lowerIsRoot && !upperInside) {
          // the interval isolates exactly one root, therefore the root is the interval end
          count++;
          break;
        }
        if (upperInside && upperIsRoot && !lowerInside) {
          count++;
          break;
        }
        if (lowerInside && upperInside && (lowerIsRoot || upperIsRoot)) {
          count++;
          break;
        }
        iv = sturm.halfInterval(iv, poly);
      }
    }
    return count;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr polynomial = ast.arg1();
    IExpr x = ast.arg2();
    BigRational lower = null;
    BigRational upper = null;
    if (x.isList3()) {
      IAST list = (IAST) x;
      x = list.arg1();
      IExpr a = list.arg2();
      IExpr b = list.arg3();
      if (!a.isNegativeInfinity()) {
        lower = toBigRational(a);
        if (lower == null) {
          return F.NIL;
        }
      }
      if (!b.isInfinity()) {
        upper = toBigRational(b);
        if (upper == null) {
          return F.NIL;
        }
      }
      if (lower != null && upper != null && lower.compareTo(upper) > 0) {
        BigRational t = lower;
        lower = upper;
        upper = t;
      }
    }
    if (!x.isVariable()) {
      // `1` is not a valid variable.
      return Errors.printMessage(S.CountRoots, "ivar", F.List(x), engine);
    }
    if (polynomial.isEqual()) {
      polynomial = engine.evaluate(F.Subtract(polynomial.first(), polynomial.second()));
    }
    if (polynomial.isFree(x) || !polynomial.isPolynomial(F.List(x))) {
      return F.NIL;
    }
    IExpr factors = engine.evaluate(F.unaryAST1(S.FactorSquareFreeList, polynomial));
    if (!factors.isListOfLists()) {
      return F.NIL;
    }
    long count = 0;
    IAST list = (IAST) factors;
    for (int i = 1; i < list.size(); i++) {
      IAST factor = (IAST) list.get(i);
      if (!factor.isList2() || factor.arg1().isFree(x)) {
        continue;
      }
      int multiplicity = factor.arg2().toIntDefault();
      if (multiplicity <= 0) {
        return F.NIL;
      }
      @SuppressWarnings("unchecked")
      GenPolynomial<BigRational>[] poly = new GenPolynomial[1];
      List<Interval<BigRational>> roots = realRoots(factor.arg1(), x, poly);
      if (roots == null) {
        return F.NIL;
      }
      count += multiplicity * countRoots(poly[0], roots, lower, upper);
    }
    return F.ZZ(count);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
