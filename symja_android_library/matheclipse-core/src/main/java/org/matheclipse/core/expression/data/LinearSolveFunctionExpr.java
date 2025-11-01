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

/**
 * Wrapper expression that holds a linear system solver.
 *
 * <p>
 * This class extends {@link DataExpr} parameterized with {@link FieldDecompositionSolver} and
 * stores whether the solver works on complex numeric values as well as an associated numeric
 * precision for non-complex (IExpr) usage.
 *
 * <p>
 * Instances are used to represent a prepared linear solve function (matrix decomposition / solver)
 * which can be queried for dimensions and optionally evaluated by higher-level code.
 *
 * @param <T> the field element type handled by the solver (e.g. {@link Complex} or {@link IExpr})
 */
public class LinearSolveFunctionExpr<T extends FieldElement<T>>
    extends DataExpr<FieldDecompositionSolver<T>> {

  private static final long serialVersionUID = 1010655513699079968L;

  /**
   * Create a {@code LinearSolveFunctionExpr} specialized for complex numeric matrices.
   *
   * @param solver the Hipparchus {@link FieldDecompositionSolver} for {@link Complex} elements
   * @return a new {@code LinearSolveFunctionExpr} configured for complex numeric use
   */
  public static LinearSolveFunctionExpr<Complex> createComplex(
      final FieldDecompositionSolver<Complex> solver) {
    return new LinearSolveFunctionExpr<Complex>(solver, true, ParserConfig.MACHINE_PRECISION);
  }

  /**
   * Create a {@code LinearSolveFunctionExpr} for generic {@link IExpr}-based numeric computation.
   *
   * @param solver the Hipparchus {@link FieldDecompositionSolver} for {@link IExpr} elements
   * @param precision numeric precision to associate with this solver (used for {@code IExpr})
   * @return a new {@code LinearSolveFunctionExpr} configured for {@link IExpr} numeric use
   */
  public static LinearSolveFunctionExpr<IExpr> createIExpr(
      final FieldDecompositionSolver<IExpr> solver, long precision) {
    return new LinearSolveFunctionExpr<IExpr>(solver, false, precision);
  }

  /**
   * Flag indicating whether the solver uses complex numeric values.
   */
  private final boolean complexNumeric;

  /**
   * Numeric precision associated with the solver when used with {@link IExpr} numbers.
   */
  private final long precision;

  /**
   * Protected constructor storing the underlying solver, numeric mode and precision.
   *
   * @param solver the underlying {@link FieldDecompositionSolver} instance
   * @param complexNumeric {@code true} when the solver uses complex numeric values
   * @param precision numeric precision for {@link IExpr} mode
   */
  protected LinearSolveFunctionExpr(final FieldDecompositionSolver<T> solver,
      boolean complexNumeric, long precision) {
    super(S.LinearSolveFunction, solver);
    this.complexNumeric = complexNumeric;
    this.precision = precision;
  }

  /**
   * Create a shallow copy of this expression. The returned instance references the same solver
   * object but uses the machine precision for complex variants.
   *
   * @return a new {@code LinearSolveFunctionExpr} referencing the same solver
   */
  @Override
  public IExpr copy() {
    return new LinearSolveFunctionExpr(fData, complexNumeric, ParserConfig.MACHINE_PRECISION);
  }

  /**
   * Equality is based on the wrapped solver instance.
   *
   * @param obj the object to compare with
   * @return {@code true} if the other object is the same instance or wraps an equal solver
   */
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


  /**
   * Evaluate the solver expression with the given AST and evaluation engine.
   *
   * <p>
   * Currently this method is a placeholder and returns {@link F#NIL}.
   *
   * @param ast the AST representing the call or arguments
   * @param engine the current evaluation engine
   * @return evaluation result or {@link F#NIL} if not implemented
   */
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    return F.NIL;
  }

  /**
   * Get the number of columns of the underlying matrix (solver).
   *
   * @return column dimension of the matrix handled by the solver
   */
  public int getColumnDimension() {
    return fData.getColumnDimension();
  }

  /**
   * Get the number of rows of the underlying matrix (solver).
   *
   * @return row dimension of the matrix handled by the solver
   */
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

  /**
   * Indicates whether this solver is intended for complex numeric computation.
   *
   * @return {@code true} if complex numeric mode is enabled
   */
  @Override
  public boolean isComplexNumeric() {
    return complexNumeric;
  }

  /**
   * Returns the numeric precision associated with this solver (used for {@link IExpr} mode).
   *
   * @return numeric precision value
   */
  public long getNumericPrecision() {
    return precision;
  }

  /**
   * Normalize this expression to an AST form. Currently returns {@link F#NIL} for this wrapper.
   *
   * @param nilIfUnevaluated if {@code true} return {@link F#NIL} when not evaluated
   * @return normalized AST or {@link F#NIL}
   */
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
