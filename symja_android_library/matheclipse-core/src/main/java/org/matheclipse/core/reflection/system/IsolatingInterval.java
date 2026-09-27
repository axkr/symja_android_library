package org.matheclipse.core.reflection.system;

import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.root.Interval;
import edu.jas.root.RealRootsSturm;

/**
 * <pre>
 * <code>IsolatingInterval(a)
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives a rational isolating interval for the real algebraic number <code>a</code>.
 * </p>
 * </blockquote>
 *
 * <pre>
 * <code>IsolatingInterval(a, dx)
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives an isolating interval of width at most <code>dx</code>.
 * </p>
 * </blockquote>
 */
public class IsolatingInterval extends AbstractFunctionEvaluator {

  public IsolatingInterval() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr a = ast.arg1();
    if (a.isRational()) {
      return F.List(a, a);
    }
    BigRational eps = null;
    if (ast.isAST2()) {
      eps = CountRoots.toBigRational(ast.arg2());
      if (eps == null || eps.signum() <= 0) {
        return F.NIL;
      }
    }
    if (!a.isNumericFunction(true)) {
      return F.NIL;
    }
    try {
      IExpr numeric = engine.evalN(a);
      if (!numeric.isReal()) {
        // only real algebraic numbers are supported
        return F.NIL;
      }
      final double value = numeric.evalf();
      ISymbol x = F.Dummy();
      IExpr polynomial = engine.evaluate(F.binaryAST2(S.MinimalPolynomial, a, x));
      if (!polynomial.isPolynomial(F.List(x)) || polynomial.isFree(x)) {
        return F.NIL;
      }
      @SuppressWarnings("unchecked")
      GenPolynomial<BigRational>[] poly = new GenPolynomial[1];
      List<Interval<BigRational>> roots = CountRoots.realRoots(polynomial, x, poly);
      if (roots == null || roots.isEmpty()) {
        return F.NIL;
      }
      RealRootsSturm<BigRational> sturm = new RealRootsSturm<BigRational>();
      // refine the intervals until exactly one interval contains the numeric value
      for (int i = 0; i < 200; i++) {
        Interval<BigRational> found = null;
        int count = 0;
        for (Interval<BigRational> interval : roots) {
          double left = interval.left.doubleValue();
          double right = interval.right.doubleValue();
          double tolerance = 1e-12 * (1.0 + Math.abs(value));
          if (left - tolerance <= value && value <= right + tolerance) {
            found = interval;
            count++;
          }
        }
        if (count == 0) {
          return F.NIL;
        }
        if (count == 1) {
          if (eps != null && found.left.compareTo(found.right) != 0) {
            found = sturm.refineInterval(found, poly[0], eps);
          }
          return F.List(F.QQ(found.left.numerator(), found.left.denominator()),
              F.QQ(found.right.numerator(), found.right.denominator()));
        }
        for (int j = 0; j < roots.size(); j++) {
          Interval<BigRational> interval = roots.get(j);
          if (interval.left.compareTo(interval.right) != 0) {
            roots.set(j, sturm.halfInterval(interval, poly[0]));
          }
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
