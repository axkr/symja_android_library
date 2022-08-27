// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.nrm;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Hypot computes <code>sqrt(<i>a</i><sup>2</sup>&nbsp;+<i>b</i><sup>2</sup>)</code> for a and b as
 * {@link RealScalar}s without intermediate overflow or underflow.
 * 
 * <p>
 * Hypot also operates on vectors.
 * 
 * <p>
 * Hypot is inspired by {@link Math#hypot(double, double)}
 */
public class Hypot {

  /**
   * @param a
   * @param b
   * @return Sqrt[ |a|^2 + |b|^2 ]
   */
  public static IExpr of(IExpr a, IExpr b) {
    IExpr ax = S.Abs.of(a);
    IExpr ay = S.Abs.of(b);
    if (ax.isZero() || ay.isZero()) {
      return ax.add(ay);
    }
    final IExpr max;
    IExpr r1 = (ax.lessThan(ay).isTrue()) //
        ? ax.divide(max = ay)
        : ay.divide(max = ax);
    // valid at this point: 0 < max
    IExpr r2 = r1.multiply(r1);
    return F.eval(F.Sqrt(r2.one().add(r2)).multiply(max));
  }

  /**
   * @param a without unit
   * @return Sqrt[ |a|^2 + 1 ]
   * @throws Exception if a has a unit
   */
  public static IExpr withOne(IExpr a) {
    IExpr ax = S.Abs.of(a);
    IExpr one = ax.one();
    if (ax.lessThan(one).isTrue()) { // (Scalars.lessThan(ax, one)) {
      return F.eval(F.Sqrt(ax.multiply(ax).add(one)));
    }
    IExpr r1 = ax.reciprocal(); // in the unit interval [0, 1]
    return F.eval(F.Sqrt(r1.multiply(r1).add(one)).multiply(ax));
  }

  /**
   * function computes the 2-Norm of a vector without intermediate overflow or underflow
   * 
   * <p>
   * the empty vector Hypot[{}] results in an error, since Mathematica::Norm[{}] == Norm[{}] is
   * undefined also.
   * 
   * <p>
   * The disadvantage of the implementation is that a numerical output is returned even in cases
   * where a rational number is the exact result.
   * 
   * @param vector
   * @return 2-norm of vector
   * @throws Exception if vector is empty, or vector contains NaN
   */
  public static IExpr ofVector(IAST vector) {
    // same issue as in Pivots
    IAST abs = vector.map(x -> x.abs());
    IExpr max = abs.stream() //
        .map(IExpr.class::cast) //
        .reduce((x, y) -> S.Max.of(x, y)) //
        .orElseThrow();
    if (max.isZero()) {
      return max;
    }
    abs = (IAST) abs.divide(max);
    return F.eval(max.multiply(F.Sqrt(F.Dot(abs, abs))));
    // Sqrt.FUNCTION.apply((IExpr) abs.dot(abs)));
  }
}
