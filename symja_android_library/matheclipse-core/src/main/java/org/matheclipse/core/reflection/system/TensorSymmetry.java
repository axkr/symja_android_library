package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.ArraySymbolExpr;
import org.matheclipse.core.expression.data.MatrixSymbolExpr;
import org.matheclipse.core.expression.data.VectorSymbolExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implementation of the TensorSymmetry function.
 * <p>
 * Returns the symmetry group or specification of a tensor.
 * </p>
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Symbolic Tensors: Returns stored symmetry for MatrixSymbol/ArraySymbol.</li>
 * <li>Explicit Matrices: Inspects data (e.g., <code>{{a,b},{b,c}}</code>) to detect
 * Symmetric/Antisymmetric patterns.</li>
 * </ul>
 */
public class TensorSymmetry extends AbstractEvaluator {

  public TensorSymmetry() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    if (ast.argSize() != 1) {
      return F.NIL;
    }

    IExpr arg = ast.arg1();

    // 1. VectorSymbol (Rank 1) -> Identity
    if (arg instanceof VectorSymbolExpr) {
      return F.Cycles(F.List());
    }

    // 2. MatrixSymbol
    if (arg instanceof MatrixSymbolExpr) {
      MatrixSymbolExpr m = (MatrixSymbolExpr) arg;
      IExpr sym = m.getSymmetry();
      return sym.isNone() ? F.Cycles(F.List()) : sym;
    }

    // 3. ArraySymbol
    if (arg instanceof ArraySymbolExpr) {
      ArraySymbolExpr a = (ArraySymbolExpr) arg;
      IExpr sym = a.getSymmetry();
      return sym.isNone() ? F.Cycles(F.List()) : sym;
    }

    // 4. Explicit Lists (Data inspection)
    if (arg.isList()) {
      return getExplicitSymmetry((IAST) arg, engine);
    }

    return F.NIL;
  }

  /**
   * Inspects an explicit list to determine geometric symmetries. Currently supports detection of
   * Symmetric and Antisymmetric matrices (Rank 2).
   */
  private IExpr getExplicitSymmetry(IAST list, EvalEngine engine) {
    // Quick check for Matrix (Rank 2)
    if (list.isMatrix(false) == null) {
      // For vectors or higher ranks, return Identity (Cycles[{}])
      // as standard fallback unless we implement general tensor isomorphism.
      return F.Cycles(F.List());
    }

    int rows = list.argSize();
    // Assuming square matrix for symmetry
    // Note: isMatrix() ensures it is rectangular, we check squareness here.
    IAST row1 = (IAST) list.arg1();
    if (row1.argSize() != rows) {
      // Not square -> No symmetry possible for {1,2}
      return F.Cycles(F.List());
    }

    boolean isSymmetric = true;
    boolean isAntisymmetric = true;

    // Iterate over upper triangle (excluding diagonal for antisymmetric check logic below)
    for (int i = 1; i <= rows; i++) {
      IAST rowI = (IAST) list.get(i);

      // Diagonal check for Antisymmetry (must be 0)
      if (isAntisymmetric) {
        IExpr diag = rowI.get(i);
        if (!diag.isZero()) {
          isAntisymmetric = false;
        }
      }

      for (int j = i + 1; j <= rows; j++) {
        if (!isSymmetric && !isAntisymmetric) {
          break;
        }

        IAST rowJ = (IAST) list.get(j);
        IExpr valIJ = rowI.get(j); // Row i, Col j
        IExpr valJI = rowJ.get(i); // Row j, Col i

        // Check Symmetric: M_ij == M_ji
        if (isSymmetric) {
          if (!valIJ.equals(valJI)) {
            // Fallback to engine evaluation for symbolic equivalence (e.g. x == x)
            if (!engine.evalTrue(F.Equal(valIJ, valJI))) {
              isSymmetric = false;
            }
          }
        }

        // Check Antisymmetric: M_ij == -M_ji
        if (isAntisymmetric) {
          // Check if valIJ == -valJI
          IExpr negValJI = F.Times(F.CN1, valJI);
          // Use simplify/evalTrue to check symbolic negation
          if (!engine.evalTrue(F.Equal(valIJ, negValJI))) {
            isAntisymmetric = false;
          }
        }
      }
    }

    if (isSymmetric) {
      return F.Symmetric(F.List(F.C1, F.C2));
    }
    if (isAntisymmetric) {
      return F.Antisymmetric(F.List(F.C1, F.C2));
    }

    // Default: No symmetry found
    return F.Cycles(F.List());
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.PROTECTED);
  }
}
