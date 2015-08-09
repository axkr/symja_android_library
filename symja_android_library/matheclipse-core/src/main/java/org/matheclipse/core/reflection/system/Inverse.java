package org.matheclipse.core.reflection.system;

import org.apache.commons.math4.linear.DecompositionSolver;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.linear.LUDecomposition;
import org.matheclipse.commons.math.linear.FieldDecompositionSolver;
import org.matheclipse.commons.math.linear.FieldLUDecomposition;
import org.matheclipse.commons.math.linear.FieldMatrix;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Matrix;

/**
 * Invert a matrix
 * 
 * See <a href="http://en.wikipedia.org/wiki/Invertible_matrix">Invertible
 * matrix</a>
 */
public class Inverse extends AbstractMatrix1Matrix {

  public Inverse() {
    super();
  }

  @Override
  public FieldMatrix matrixEval(FieldMatrix matrix) {
    final FieldLUDecomposition lu = new FieldLUDecomposition(
        matrix);
    FieldDecompositionSolver solver = lu.getSolver();
    if (!solver.isNonSingular()) {
      return null;
    }
    return solver.getInverse();
  }

  @Override
  public RealMatrix realMatrixEval(RealMatrix matrix) {
    final LUDecomposition lu = new LUDecomposition(matrix);
    DecompositionSolver solver = lu.getSolver();
    if (!solver.isNonSingular()) {
      return null;
    }
    return solver.getInverse();
  }
}