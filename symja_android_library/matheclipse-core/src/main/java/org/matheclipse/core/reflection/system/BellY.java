package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.PolynomialDegreeLimitExceeded;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class BellY extends AbstractFunctionEvaluator {

  // Wrapper for standard evaluations to preserve the original signature
  public static IExpr bellY(int n, int k, IAST symbols, IAST originalAST, EvalEngine engine) {
    return bellY(n, k, symbols, originalAST, engine, new IExpr[n + 1][k + 1], true);
  }

  /**
   * Calculate the partial Bell polynomial recursively with a Dynamic Programming cache. The result
   * is fully expanded.
   */
  public static IExpr bellY(int n, int k, IAST symbols, IAST originalAST, EvalEngine engine,
      IExpr[][] cache) {
    return bellY(n, k, symbols, originalAST, engine, cache, true);
  }

  /**
   * Calculate the partial Bell polynomial recursively with a Dynamic Programming cache.
   * 
   * @param n
   * @param k
   * @param symbols
   * @param originalAST
   * @param engine
   * @param cache the dynamic programming cache (may be <code>null</code>)
   * @param expand if <code>true</code> the intermediate products are expanded; if
   *        <code>false</code> the (factored) structure of the symbols is preserved
   * 
   */
  public static IExpr bellY(int n, int k, IAST symbols, IAST originalAST, EvalEngine engine,
      IExpr[][] cache, boolean expand) {
    if (n == 0 && k == 0) {
      return F.C1;
    }
    if (n == 0 || k == 0 || n < k) {
      return F.C0;
    }
  
    // DP Cache hit
    if (cache != null && cache[n][k] != null) {
      return cache[n][k];
    }
  
    final int recursionLimit = engine.getRecursionLimit();
    try {
      if (recursionLimit > 0) {
        int counter = engine.incRecursionCounter();
        if (counter > recursionLimit) {
          RecursionLimitExceeded.throwIt(counter, originalAST);
        }
      }
  
      IExpr s = F.C0;
      int max = n - k + 2;
  
      int iterationLimit = engine.getIterationLimit();
      if (iterationLimit >= 0 && iterationLimit <= max) {
        IterationLimitExceeded.throwIt(max, originalAST);
      }
  
      for (int m = 1; m < max; m++) {
        if ((m < symbols.size()) && !symbols.get(m).isZero()) {
          IExpr subBell = bellY(n - m, k - 1, symbols, originalAST, engine, cache, expand);
  
          if (!subBell.isZero()) {
            // Exact arbitrary-precision Binomial Coefficient
            IExpr binom = engine.evaluate(F.Binomial(F.ZZ(n - 1), F.ZZ(m - 1)));
            IExpr product = F.Times(binom, subBell, symbols.get(m));
            IExpr term = engine.evaluate(expand ? F.Expand(product) : product);
            s = engine.evaluate(F.Plus(s, term));
          }
        }
      }
  
      // DP Cache save
      if (cache != null) {
        cache[n][k] = s;
      }
      return s;
  
    } finally {
      if (recursionLimit > 0) {
        engine.decRecursionCounter();
      }
    }
  }

  /**
   * Computes the generalized (partial) Bell polynomial of a matrix as an iterated Faà di Bruno
   * composition. The columns of <code>matrix</code> are folded from the innermost (rightmost)
   * column to the outermost (leftmost) column:
   *
   * <pre>
   * g_p[i]   = matrix[[i, p]]
   * g_j[i]   = Sum_{k=1..i} matrix[[k, j]] * BellY(i, k, {g_{j+1}[1], ..., g_{j+1}[i-k+1]})
   * result   = g_1[n]
   * </pre>
   *
   * @param matrix a rectangular <code>n x cols</code> matrix
   * @param n the number of rows (the derivative order)
   * @param cols the number of columns
   * @param originalAST the original AST for error messages
   * @param engine the evaluation engine
   * @return the scalar generalized Bell polynomial <code>g_1[n]</code>
   */
  public static IExpr bellYGeneralized(IAST matrix, int n, int cols, IAST originalAST,
      EvalEngine engine) {
    // g[i] holds the i-th "derivative" of the current (innermost) composition, i = 1..n
    IExpr[] g = new IExpr[n + 1];
    for (int i = 1; i <= n; i++) {
      g[i] = matrix.getPart(i, cols);
    }

    for (int j = cols - 1; j >= 1; j--) {
      IExpr[] gNext = new IExpr[n + 1];
      for (int i = 1; i <= n; i++) {
        IExpr sum = F.C0;
        for (int k = 1; k <= i; k++) {
          IExpr fk = matrix.getPart(k, j);
          if (!fk.isZero()) {
            int varsSize = i - k + 1;
            IASTAppendable vars = F.ListAlloc(varsSize);
            for (int t = 1; t <= varsSize; t++) {
              vars.append(g[t]);
            }
            // Keep the factored structure of the inner composition (no expansion)
            IExpr bell = bellY(i, k, vars, originalAST, engine, new IExpr[i + 1][k + 1], false);
            if (!bell.isZero()) {
              IExpr term = engine.evaluate(F.Times(fk, bell));
              sum = engine.evaluate(F.Plus(sum, term));
            }
          }
        }
        gNext[i] = sum;
      }
      g = gNext;
    }
    return g[n];
  }

  /**
   * @return <code>true</code> if at least one element of the list is itself a list (i.e. the list
   *         represents a matrix, possibly non-rectangular).
   */
  private static boolean isMatrixForm(IAST list) {
    for (int i = 1; i < list.size(); i++) {
      if (list.get(i).isList()) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return the maximum row length (number of columns) of the given list-of-lists.
   */
  private static int numberOfColumns(IAST matrix) {
    int cols = 0;
    for (int r = 1; r < matrix.size(); r++) {
      if (matrix.get(r).isList()) {
        cols = Math.max(cols, ((IAST) matrix.get(r)).argSize());
      }
    }
    return cols;
  }

  /**
   * Pad a (possibly ragged or non-square) matrix to a <code>rows x cols</code> rectangular matrix,
   * filling missing entries with <code>0</code>.
   */
  private static IAST padMatrix(IAST matrix, int rows, int cols) {
    IASTAppendable result = F.ListAlloc(rows);
    for (int r = 1; r <= rows; r++) {
      IExpr rowExpr = (r < matrix.size()) ? matrix.get(r) : F.NIL;
      IAST rowList = rowExpr.isList() ? (IAST) rowExpr : F.NIL;
      IASTAppendable newRow = F.ListAlloc(cols);
      for (int c = 1; c <= cols; c++) {
        IExpr value = (rowList.isPresent() && c < rowList.size()) ? rowList.get(c) : F.C0;
        newRow.append(value);
      }
      result.append(newRow);
    }
    return result;
  }

  /**
   * Build the matrix <code>ms</code> by prepending <code>UnitVector(n, k)</code> as the first
   * column to the matrix <code>m</code>. The matrix <code>m</code> is zero-padded to <code>n</code>
   * rows and <code>cols</code> columns first.
   */
  private static IAST prependUnitVectorColumn(IAST matrix, int n, int k, int cols) {
    IAST padded = padMatrix(matrix, n, cols);
    IASTAppendable ms = F.ListAlloc(n);
    for (int r = 1; r <= n; r++) {
      IASTAppendable newRow = F.ListAlloc(cols + 1);
      newRow.append(r == k ? F.C1 : F.C0);
      newRow.appendArgs((IAST) padded.get(r));
      ms.append(newRow);
    }
    return ms;
  }

  /**
   * Evaluate the generalized Bell polynomial <code>BellY(m)</code> of a matrix <code>m</code>. The
   * matrix is zero-padded to a rectangular matrix and the iterated Faà di Bruno composition of its
   * columns is returned.
   */
  public static IExpr bellYMatrixForm(IAST matrix, IAST originalAST, EvalEngine engine) {
    int rows = matrix.argSize();
    int cols = numberOfColumns(matrix);
    if (rows <= 0 || cols <= 0) {
      return F.NIL;
    }
    if (rows > Config.MAX_POLYNOMIAL_DEGREE) {
      PolynomialDegreeLimitExceeded.throwIt(rows);
    }
    IAST rectangular = padMatrix(matrix, rows, cols);
    return bellYGeneralized(rectangular, rows, cols, originalAST, engine);
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (ast.isAST1()) {
      // BellY(m) - the generalized Bell polynomial of a matrix m.
      if (arg1.isList() && isMatrixForm((IAST) arg1)) {
        return bellYMatrixForm((IAST) arg1, ast, engine);
      }
      return F.NIL;
    }
    if (ast.isAST2()) {
      return F.NIL;
    }
    if (arg1.isInteger() && ast.arg2().isInteger()) {
      int n = arg1.toIntDefault();
      int k = ast.arg2().toIntDefault();
      if (n < 0 || k < 0) {
        return F.NIL;
      }

      IExpr arg3 = ast.arg3();
      if (arg3.isList() && isMatrixForm((IAST) arg3)) {
        // BellY(n, k, m) is equivalent to BellY(ms), where ms is formed by prepending
        // UnitVector(n, k) to m as a first column. Non-rectangular matrices are zero-padded.
        if (n > Config.MAX_POLYNOMIAL_DEGREE) {
          PolynomialDegreeLimitExceeded.throwIt(n);
        }
        if (n == 0 && k == 0) {
          return F.C1;
        }
        if (n == 0 || k == 0 || n < k) {
          return F.C0;
        }
        IAST matrix = (IAST) arg3;
        int cols = numberOfColumns(matrix);
        IAST ms = prependUnitVectorColumn(matrix, n, k, cols);
        return bellYGeneralized(ms, n, cols + 1, ast, engine);
      }

      IExpr listOfVariables = arg3.normal(false);
      if (!listOfVariables.isList()) {
        return F.NIL;
      }

      if (n == 0 && k == 0) {
        return F.C1;
      }
      if (n == 0 || k == 0) {
        return F.C0;
      }
      if (n < k) {
        return F.C0;
      }
      if (n > Config.MAX_POLYNOMIAL_DEGREE) {
        PolynomialDegreeLimitExceeded.throwIt(n);
      }
      int max = n - k + 2;
      if (max >= 0) {
        IExpr[][] cache = new IExpr[n + 1][k + 1];
        return bellY(n, k, (IAST) listOfVariables, ast, engine, cache);
      }
      return F.NIL;
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
  }

  @Override
  public int status() {
    return ImplementationStatus.FULL_SUPPORT;
  }
}