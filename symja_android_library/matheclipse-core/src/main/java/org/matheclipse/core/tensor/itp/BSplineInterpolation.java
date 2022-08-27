// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.itp;

import java.io.Serializable;
import java.util.function.Function;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * BSplineInterpolation defines a parametric curve that interpolates the given control points at
 * integer values.
 * 
 * <p>
 * The input to {@link #get(IAST)} is required to be non-empty.
 */
public class BSplineInterpolation extends AbstractInterpolation implements Serializable {

  /**
   * @param degree of b-spline basis functions: 1 for linear, 2 for quadratic, etc.
   * @param control points with at least one element
   * @return
   */
  public static Interpolation of(int degree, IExpr control) {
    return new BSplineInterpolation(degree, control, false);
  }

  /**
   * @param degree of b-spline basis functions: 1 for linear, 2 for quadratic, etc.
   * @param n number of control points
   * @return
   */
  public static IAST matrix(int degree, int n) {

    // IASTAppendable matrix = F.ListAlloc(n);
    // for (int i = 0; i < n; i++) {
    // IASTAppendable vector = F.mapRange(0, n, x -> F.C0);
    // vector.set(i + 1, F.C1);
    // matrix.append(vector);
    // }
    // return matrix;

    IAST domain1 = (IAST) S.Range.of(EvalEngine.get(), 0, n - 1);
    IAST domain2 = (IAST) S.Range.of(EvalEngine.get(), 0, n - 1);
    IAST result = (IAST) S.Transpose.of(EvalEngine.get(), domain2 //
        .map(index -> {
          IASTAppendable unitVector = F.mapRange(1, n + 1, x -> F.C0);
          unitVector.set(index.toIntDefault() + 1, F.C1);
          return domain1.map(BSplineFunctionString.of(degree, unitVector));
        }));
    return result;
  }

  /**
   * @param degree
   * @param control
   * @return control points that define a limit that interpolates the points in the given tensor
   */
  public static IAST solve(int degree, IExpr control) {
    return (IAST) S.LinearSolve.of(EvalEngine.get(), matrix(degree, control.argSize()), control);
  }

  // ---
  private final int degree;
  private final Function<IExpr, IExpr> scalarTensorFunction;

  public BSplineInterpolation(int degree, IExpr controlMatrix, boolean isMatrix) {
    this.degree = degree;
    if (isMatrix) {
      // TODO improve performance by using real matrix directly
      RealMatrix realMatrix = controlMatrix.toRealMatrix();
      controlMatrix = new ASTRealMatrix(realMatrix, false);
    }
    scalarTensorFunction = BSplineFunctionString.of(degree, solve(degree, controlMatrix));
  }

  @Override // from Interpolation
  public IExpr get(IAST index) {
    IExpr interp = at(index.get(1));
    // TODO TENSOR IMPL can be improved by truncating data to a neighborhood
    return index.argSize() == 1 //
        ? interp
        : of(degree, interp).get(index.copyFrom(2));// F.ListAlloc(index.stream().skip(1)));
  }

  @Override // from Interpolation
  public IExpr at(IExpr index) {
    return scalarTensorFunction.apply(index);
  }
}
