package org.matheclipse.core.expression.data;

import org.hipparchus.FieldElement;
import org.hipparchus.complex.Complex;
import org.hipparchus.linear.FieldDecompositionSolver;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.ParserConfig;

public class LinearSolveFunctionExpr<T extends FieldElement<T>>
    extends DataExpr<FieldDecompositionSolver<T>> {

  private static final long serialVersionUID = 1010655513699079968L;

  public static LinearSolveFunctionExpr<Complex> createComplex(
      final FieldDecompositionSolver<Complex> solver) {
    return new LinearSolveFunctionExpr<Complex>(solver, true, ParserConfig.MACHINE_PRECISION);
  }

  public static LinearSolveFunctionExpr<IExpr> createIExpr(
      final FieldDecompositionSolver<IExpr> solver, long precision) {
    return new LinearSolveFunctionExpr<IExpr>(solver, false, precision);
  }

  private final boolean complexNumeric;

  private final long precision;

  protected LinearSolveFunctionExpr(
      final FieldDecompositionSolver<T> solver, boolean complexNumeric, long precision) {
    super(S.LinearSolveFunction, solver);
    this.complexNumeric = complexNumeric;
    this.precision = precision;
  }

  @Override
  public IExpr copy() {
    return new LinearSolveFunctionExpr(fData, complexNumeric, ParserConfig.MACHINE_PRECISION);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof LinearSolveFunctionExpr) {
      return fData.equals(((LinearSolveFunctionExpr) obj).fData);
    }
    return false;
  }

  public IExpr evaluate(IAST ast, EvalEngine engine) {
    return F.NIL;
  }

  public int getColumnDimension() {
    return fData.getColumnDimension();
  }

  public int getRowDimension() {
    return fData.getRowDimension();
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 463 : 463 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return LINEARSOLVEUNCTONID;
  }

  @Override
  public boolean isComplexNumeric() {
    return complexNumeric;
  }
   
  public long getNumericPrecision() {
    return precision;
  }

  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    return F.NIL;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("LinearSolveFunction(Matrix dimensions: {");
    buf.append(getRowDimension());
    buf.append(",");
    buf.append(getColumnDimension());
    buf.append("})");
    return buf.toString();
  }
}
