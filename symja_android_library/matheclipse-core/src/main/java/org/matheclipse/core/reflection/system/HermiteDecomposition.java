package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;


/**
 * Implementation of the HermiteDecomposition using BigInteger arithmetic.
 * <p>
 * Syntax: HermiteDecomposition(Matrix) Returns: {H, U} where H = U.A and H is in Hermite Normal
 * Form.
 * </p>
 */
public class HermiteDecomposition extends AbstractFunctionEvaluator {

  public HermiteDecomposition() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (!arg1.isList()) {
      return F.NIL;
    }

    try {
      // try to create a BigInteger matrixx
      BigInteger[][] H = Convert.list2BigIntegerMatrix(arg1);

      if (H == null || H.length == 0) {
        return F.NIL;
      }

      int rows = H.length;

      // Initialize the transformation matrix U as an identity matrix
      BigInteger[][] U = createIdentity(rows);

      computeHNF(H, U);

      IExpr uExpr = matrix2Expr(U);
      IExpr hExpr = matrix2Expr(H);
      return F.List(uExpr, hExpr);

    } catch (RuntimeException rex) {
      return Errors.printMessage(S.HermiteDecomposition, rex);
    }
  }

  /**
   * Computes the Row Hermite Normal Form (HNF). Modifies H and U in-place.
   */
  private static void computeHNF(BigInteger[][] H, BigInteger[][] U) {
    int rows = H.length;
    int cols = H[0].length;

    int pivotRow = 0;
    int pivotCol = 0;

    // We iterate through the columns and try to set pivots
    while (pivotRow < rows && pivotCol < cols) {

      // Find a non-zero element in the current column (from pivotRow downwards)
      int targetRow = -1;
      for (int i = pivotRow; i < rows; i++) {
        if (H[i][pivotCol].signum() != 0) {
          targetRow = i;
          break;
        }
      }

      if (targetRow == -1) {
        // Column consists only of zeros (in the relevant area) -> next column
        pivotCol++;
        continue;
      }

      // Swap the found row to the pivot position
      swapRows(H, U, pivotRow, targetRow);

      // Eliminate all entries below the pivot (GCD creation)
      for (int i = pivotRow + 1; i < rows; i++) {
        while (H[i][pivotCol].signum() != 0) {
          // Euclidean step between pivot row and current row
          // We want to eliminate H[i][col].
          // If H[i][col] is not divisible by H[pivotRow][col], it reduces the problem, until one is
          // 0.

          BigInteger hPivot = H[pivotRow][pivotCol];
          BigInteger hCurrent = H[i][pivotCol];

          // q = hCurrent / hPivot
          BigInteger q = hCurrent.divide(hPivot);

          // Row_i = Row_i - q * Row_pivot
          addToRow(H, U, i, pivotRow, q.negate());

          // If after subtraction H[i][col] == 0, we are done with this row.
          // If not (because division was not exact, which happens with BigInteger divide),
          // then the remainder is now in H[i][col]. We swap and continue.
          // BUT: BigInteger.divide is integer division (truncating).
          // The "remainder" is implicit.

          if (H[i][pivotCol].signum() == 0) {
            break;
          } else {
            // Swap to use the smaller value (remainder) as the new pivot (Euclid)
            swapRows(H, U, pivotRow, i);
          }
        }
      }

      // Make pivot positive
      if (H[pivotRow][pivotCol].signum() < 0) {
        scaleRow(H, U, pivotRow, BigInteger.ONE.negate());
      }

      // Reduce entries ABOVE the pivot (Modulo)
      // Condition: 0 <= H[k][pivotCol] < H[pivotRow][pivotCol]
      BigInteger pivotVal = H[pivotRow][pivotCol];
      for (int k = 0; k < pivotRow; k++) {
        BigInteger val = H[k][pivotCol];

        // We calculate q such that val - q*pivotVal is in the range [0, pivotVal).
        // q = floor(val / pivotVal)
        // With Java BigInteger divide:
        // If positive: 7 / 3 = 2. Remainder 1. -> Fits.
        // If negative: -7 / 3 = -2. Remainder -1. -> Does not fit, we need a positive remainder.

        BigInteger[] qr = val.divideAndRemainder(pivotVal);
        BigInteger q = qr[0];
        BigInteger r = qr[1];

        // Correction for negative remainder (mathematical modulo vs Java remainder)
        if (r.signum() < 0) {
          // r is e.g. -1, pivot 3. We want remainder 2.
          // r + pivot = 2.
          // For this, q must be decreased by 1 (further into the negative).
          // val = q*p + r = (q-1)*p + (r+p)
          q = q.subtract(BigInteger.ONE);
        }

        // Reduction: Row_k = Row_k - q * Row_pivot
        addToRow(H, U, k, pivotRow, q.negate());
      }

      // Pivot successfully processed
      pivotRow++;
      pivotCol++;
    }

    // Zero rows are now automatically at the bottom due to the swap process.
  }

  private static void swapRows(BigInteger[][] H, BigInteger[][] U, int i, int j) {
    if (i == j)
      return;
    BigInteger[] tempH = H[i];
    H[i] = H[j];
    H[j] = tempH;
    BigInteger[] tempU = U[i];
    U[i] = U[j];
    U[j] = tempU;
  }

  private static void addToRow(BigInteger[][] H, BigInteger[][] U, int targetRow, int sourceRow,
      BigInteger factor) {
    if (factor.equals(BigInteger.ZERO))
      return;

    // H[target] += factor * H[source]
    for (int c = 0; c < H[0].length; c++) {
      if (!H[sourceRow][c].equals(BigInteger.ZERO)) {
        H[targetRow][c] = H[targetRow][c].add(H[sourceRow][c].multiply(factor));
      }
    }
    // U[target] += factor * U[source]
    for (int c = 0; c < U[0].length; c++) {
      if (!U[sourceRow][c].equals(BigInteger.ZERO)) {
        U[targetRow][c] = U[targetRow][c].add(U[sourceRow][c].multiply(factor));
      }
    }
  }

  private static void scaleRow(BigInteger[][] H, BigInteger[][] U, int row, BigInteger factor) {
    for (int c = 0; c < H[0].length; c++)
      H[row][c] = H[row][c].multiply(factor);
    for (int c = 0; c < U[0].length; c++)
      U[row][c] = U[row][c].multiply(factor);
  }

  /**
   * Initialize the transformation matrix a square identity matrix
   * 
   * @param n
   * @return
   */
  private static BigInteger[][] createIdentity(int n) {
    BigInteger[][] res = new BigInteger[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        res[i][j] = (i == j) ? BigInteger.ONE : BigInteger.ZERO;
      }
    }
    return res;
  }

  private static IExpr matrix2Expr(BigInteger[][] mat) {
    IASTAppendable rows = F.ListAlloc(mat.length);
    for (BigInteger[] row : mat) {
      IASTAppendable r = F.ListAlloc(row.length);
      for (BigInteger val : row) {
        r.append(F.ZZ(val));
      }
      rows.append(r);
    }
    return rows;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
