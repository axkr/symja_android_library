// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.itp;

import java.util.function.Function;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.ext.Integers;
import org.matheclipse.core.tensor.sca.Clip;
import org.matheclipse.core.tensor.sca.Clips;

/** function defined over the interval [0, control.length() - 1] */
public class BSplineFunctionString extends BSplineFunctionBase {
  /** the control point are stored by reference, i.e. modifications to
   * given tensor alter the behavior of this BSplineFunction instance.
   * 
   * @param degree of polynomial basis function, non-negative integer
   * @param sequence points with at least one element
   * @return
   * @throws Exception if degree is negative, or control does not have length at least one */
  public static Function<IExpr, IExpr> of(int degree, IAST sequence) {
    return new BSplineFunctionString(degree, sequence);
  }

  // ---
  /** index of last control point */
  private final int last;
  /** domain of this function */
  private final Clip domain;
  /** clip for knots */
  private final Clip clip;

  public BSplineFunctionString(int degree, IAST sequence) {
    super(LinearBinaryAverage.INSTANCE, degree, sequence);
    last = sequence.argSize() - 1;
    domain = Clips.positive(last);
    clip = Clips.interval( //
        domain.min().plus(shift), //
        domain.max().plus(shift));
  }

  @Override // from BSplineFunction
  protected int bound(int index) {
    return Integers.clip(1, last + 1).applyAsInt(index);
  }

  @Override // from BSplineFunctionBase
  protected IExpr requireValid(IExpr scalar) {
    return scalar;
    // return domain.requireInside(scalar);
  }

  @Override // from BSplineFunctionBase
  protected IAST project(IAST knots) {
    return knots.map(clip);
  }
}
