// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.itp;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public abstract class BSplineFunctionBase extends BSplineFunction {
  protected BSplineFunctionBase(BinaryAverage binaryAverage, int degree, IAST sequence) {
    super(binaryAverage, degree, sequence);
  }

  @Override // from ScalarTensorFunction
  public final IExpr apply(IExpr scalar) {
    scalar = requireValid(scalar).add(shift);
    return deBoor(scalar.floor().toIntDefault()).apply(scalar);
  }

  @Override // from BSplineFunction
  protected final IAST knots(int k) {
    if (-degree + 1 + k == degree + 1 + k) {
      return project(F.CEmptyList);
    }
    return project((IAST) S.Range.of(EvalEngine.get(), -degree + 1 + k, degree + k));
  }

  /** @param scalar
   * @return scalar guaranteed to be in the evaluation domain
   * @throws Exception if given scalar was outside permitted range */
  protected abstract IExpr requireValid(IExpr scalar);

  /** @param knots
   * @return */
  protected abstract IAST project(IAST knots);
}
