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
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import edu.jas.arith.BigRational;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;
import edu.jas.root.ComplexRootsSturm;
import edu.jas.root.Interval;
import edu.jas.root.InvalidBoundaryException;
import edu.jas.root.RealRootsSturm;
import edu.jas.root.Rectangle;

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

  /**
   * The number of roots of <code>polynomial</code> in the closed rectangle with the opposite
   * corners <code>a</code> and <code>b</code>, counted with multiplicity - WMA counts the roots on
   * the edges and corners, and a rectangle of width or height 0 is a segment.
   *
   * <p>
   * The roots on the four lines through the edges are found exactly: on the line
   * <code>Im(z)==c</code>, <code>p(t+I*c)</code> splits into two real polynomials whose common real
   * roots are those points. They are divided out, which leaves a polynomial without a root on the
   * boundary, and JAS counts its roots inside with Sturm sequences.
   */
  private static IExpr countInRectangle(IExpr polynomial, IExpr x, IExpr a, IExpr b,
      EvalEngine engine) {
    if (polynomial.isEqual()) {
      polynomial = engine.evaluate(F.Subtract(polynomial.first(), polynomial.second()));
    }
    if (polynomial.isFree(x) || !polynomial.isPolynomial(F.List(x))) {
      return F.NIL;
    }
    BigRational re0 = toBigRational(a.re());
    BigRational im0 = toBigRational(a.im());
    BigRational re1 = toBigRational(b.re());
    BigRational im1 = toBigRational(b.im());
    if (re0 == null || im0 == null || re1 == null || im1 == null) {
      return F.NIL;
    }
    if (re0.compareTo(re1) > 0) {
      BigRational t = re0;
      re0 = re1;
      re1 = t;
    }
    if (im0.compareTo(im1) > 0) {
      BigRational t = im0;
      im0 = im1;
      im1 = t;
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
      long roots = countSquareFreeInRectangle(factor.arg1(), x, re0, im0, re1, im1, engine);
      if (roots < 0) {
        return F.NIL;
      }
      count += multiplicity * roots;
    }
    return F.ZZ(count);
  }

  private static IRational toExpr(BigRational r) {
    return F.QQ(r.numerator(), r.denominator());
  }

  /**
   * The roots of the square free <code>f</code> in the closed rectangle, or <code>-1</code> if they
   * can't be counted.
   */
  private static long countSquareFreeInRectangle(IExpr f, IExpr x, BigRational re0,
      BigRational im0, BigRational re1, BigRational im1, EvalEngine engine) {
    final boolean point = re0.compareTo(re1) == 0 && im0.compareTo(im1) == 0;
    if (point) {
      return isRootAt(f, x, F.complex(toExpr(re0), toExpr(im0)), engine) ? 1 : 0;
    }
    final ISymbol t = F.Dummy("t");
    long boundary = 0;
    // the bottom and top edges Im(z)==c, t the real part
    long[] edge = new long[1];
    if (!lineRoots(f, x, t, F.Plus(t, F.Times(F.CI, toExpr(im0))),
        re0, re1, edge, engine)) {
      return -1;
    }
    boundary += edge[0];
    if (im0.compareTo(im1) == 0) {
      // a horizontal segment
      return boundary;
    }
    final boolean segment = re0.compareTo(re1) == 0;
    if (!segment) {
      if (!lineRoots(f, x, t, F.Plus(t, F.Times(F.CI, toExpr(im1))),
          re0, re1, edge, engine)) {
        return -1;
      }
      boundary += edge[0];
    }
    // the left and right edges Re(z)==c, t the imaginary part
    if (!lineRoots(f, x, t, F.Plus(toExpr(re0), F.Times(F.CI, t)),
        im0, im1, edge, engine)) {
      return -1;
    }
    if (segment) {
      // a vertical segment - the bottom "edge" was its lower end point
      long end = isRootAt(f, x, F.complex(toExpr(re0), toExpr(im0)), engine) ? 1 : 0;
      return edge[0] + boundary - end;
    }
    boundary += edge[0];
    if (!lineRoots(f, x, t, F.Plus(toExpr(re1), F.Times(F.CI, t)),
        im0, im1, edge, engine)) {
      return -1;
    }
    boundary += edge[0];
    // a root in a corner lies on two edges
    IExpr[] corners = {F.complex(toExpr(re0), toExpr(im0)), F.complex(toExpr(re1), toExpr(im0)),
        F.complex(toExpr(re0), toExpr(im1)), F.complex(toExpr(re1), toExpr(im1))};
    for (IExpr corner : corners) {
      if (isRootAt(f, x, corner, engine)) {
        boundary--;
      }
    }
    long inside = countInside(f, x, boundary, re0, im0, re1, im1, engine);
    if (inside < 0) {
      return -1;
    }
    return inside + boundary;
  }

  private static boolean isRootAt(IExpr f, IExpr x, IExpr z, EvalEngine engine) {
    return engine.evaluate(F.Expand(F.subst(f, x, z))).isZero();
  }

  /**
   * The roots of <code>f</code> on the line <code>x == line(t)</code>, t real. The count of those with
   * <code>lower <= t <= upper</code> is stored in <code>count[0]</code>.
   *
   * @return <code>false</code> if they can't be determined
   */
  private static boolean lineRoots(IExpr f, IExpr x, ISymbol t, IExpr line, BigRational lower,
      BigRational upper, long[] count, EvalEngine engine) {
    count[0] = 0;
    IExpr onLine = engine.evaluate(F.Expand(F.subst(f, x, line)));
    IExpr coefficients = engine.evaluate(F.CoefficientList(onLine, t));
    if (!coefficients.isList()) {
      return false;
    }
    IASTAppendable realPart = F.PlusAlloc(coefficients.argSize());
    IASTAppendable imaginaryPart = F.PlusAlloc(coefficients.argSize());
    for (int k = 1; k <= coefficients.argSize(); k++) {
      IExpr c = coefficients.getAt(k);
      if (!c.isNumber() || !c.isExactNumber()) {
        return false;
      }
      realPart.append(F.Times(c.re(), F.Power(t, F.ZZ(k - 1))));
      imaginaryPart.append(F.Times(c.im(), F.Power(t, F.ZZ(k - 1))));
    }
    IExpr common =
        engine.evaluate(F.PolynomialGCD(realPart.oneIdentity0(), imaginaryPart.oneIdentity0()));
    if (common.isAST(S.PolynomialGCD)) {
      return false;
    }
    if (common.isFree(t)) {
      return true;
    }
    @SuppressWarnings("unchecked")
    GenPolynomial<BigRational>[] poly = new GenPolynomial[1];
    List<Interval<BigRational>> roots = realRoots(common, t, poly);
    if (roots == null) {
      return false;
    }
    count[0] = countRoots(poly[0], roots, lower, upper);
    return true;
  }

  /**
   * The roots of <code>f</code> strictly inside the rectangle. JAS isolates every root of
   * <code>f</code> in a rectangle of its own; those are refined until each is inside or outside
   * the closed rectangle, except the ones around the <code>boundary</code> roots, which were counted
   * exactly and can never be decided.
   *
   * @return the count, or <code>-1</code> if the refinement didn't settle
   */
  private static long countInside(IExpr f, IExpr x, long boundary, BigRational re0,
      BigRational im0, BigRational re1, BigRational im1, EvalEngine engine) {
    try {
      ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(BigRational.ONE);
      JASConvert<Complex<BigRational>> jas = new JASConvert<Complex<BigRational>>(F.List(x), cfac);
      GenPolynomial<Complex<BigRational>> poly =
          jas.numericExpr2JAS(engine.evaluate(F.Expand(f)));
      if (poly == null || poly.degree(0) <= 0) {
        return poly == null ? -1 : 0;
      }
      ComplexRootsSturm<BigRational> sturm = new ComplexRootsSturm<BigRational>(cfac);
      List<Rectangle<BigRational>> roots = sturm.complexRoots(poly);
      BigRational length = re1.subtract(re0).sum(im1.subtract(im0)).divide(new BigRational(64));
      for (int round = 0; round < 64; round++) {
        long inside = 0;
        List<Rectangle<BigRational>> undecided = new java.util.ArrayList<Rectangle<BigRational>>();
        for (Rectangle<BigRational> root : roots) {
          BigRational left = root.getSW().getRe();
          BigRational bottom = root.getSW().getIm();
          BigRational right = root.getNE().getRe();
          BigRational top = root.getNE().getIm();
          if (left.compareTo(re0) > 0 && right.compareTo(re1) < 0 && bottom.compareTo(im0) > 0
              && top.compareTo(im1) < 0) {
            inside++;
          } else if (right.compareTo(re0) < 0 || left.compareTo(re1) > 0
              || top.compareTo(im0) < 0 || bottom.compareTo(im1) > 0) {
            // outside
          } else {
            undecided.add(root);
          }
        }
        if (undecided.size() == boundary) {
          return inside;
        }
        if (undecided.size() < boundary) {
          // can't happen: a root on the boundary is never decided
          return -1;
        }
        length = length.divide(new BigRational(16));
        List<Rectangle<BigRational>> refined = new java.util.ArrayList<Rectangle<BigRational>>();
        for (Rectangle<BigRational> root : roots) {
          refined.add(undecided.contains(root) ? sturm.complexRootRefinement(root, poly, length)
              : root);
        }
        roots = refined;
      }
      return -1;
    } catch (InvalidBoundaryException | RuntimeException ex) {
      Errors.rethrowsInterruptException(ex);
      return -1;
    }
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
      if ((a.isComplex() || b.isComplex()) && (a.isComplex() || a.isRational())
          && (b.isComplex() || b.isRational())) {
        // the closed rectangle with the corners a and b in the complex plane
        if (!x.isVariable()) {
          // `1` is not a valid variable.
          return Errors.printMessage(S.CountRoots, "ivar", F.List(x), engine);
        }
        return countInRectangle(polynomial, x, a, b, engine);
      }
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
