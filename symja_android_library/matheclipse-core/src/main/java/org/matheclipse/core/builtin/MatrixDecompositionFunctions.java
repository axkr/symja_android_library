package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Canonical forms and matrix equations: <code>LDLDecomposition</code>, <code>JordanReduce</code>,
 * <code>FrobeniusReduce</code>, <code>DrazinInverse</code>, <code>PfaffianDet</code>,
 * <code>DiagonalizableMatrixQ</code>, <code>LyapunovSolve</code> and
 * <code>DiscreteLyapunovSolve</code>.
 */
public final class MatrixDecompositionFunctions {

  private static class Initializer {

    private static void init() {
      S.LDLDecomposition.setEvaluator(new LDLDecomposition());
      S.JordanReduce.setEvaluator(new JordanReduce());
      S.FrobeniusReduce.setEvaluator(new FrobeniusReduce());
      S.DrazinInverse.setEvaluator(new DrazinInverse());
      S.PfaffianDet.setEvaluator(new PfaffianDet());
      S.DiagonalizableMatrixQ.setEvaluator(new DiagonalizableMatrixQ());
      S.LyapunovSolve.setEvaluator(new LyapunovSolve(false));
      S.DiscreteLyapunovSolve.setEvaluator(new LyapunovSolve(true));
    }
  }

  // ---------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------

  /** The entries of a square matrix, or <code>null</code>. */
  private static IExpr[][] squareMatrix(IExpr expr) {
    int[] dims = expr.isMatrix(false);
    if (dims == null || dims[0] != dims[1] || dims[0] == 0) {
      return null;
    }
    IAST matrix = (IAST) expr.normal(false);
    int n = dims[0];
    IExpr[][] a = new IExpr[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        a[i][j] = matrix.getPart(i + 1, j + 1);
      }
    }
    return a;
  }

  private static IAST toList(IExpr[][] a) {
    IASTAppendable rows = F.ListAlloc(a.length);
    for (IExpr[] row : a) {
      rows.append(F.List(row));
    }
    return rows;
  }

  private static IExpr simplify(IExpr expr, EvalEngine engine) {
    IExpr value = engine.evaluate(expr);
    if (value.isNumber() || value.isSymbol()) {
      return value;
    }
    return engine.evaluate(F.Together(value));
  }

  /** The largest dimension of a symbolic matrix whose invariant factors are computed. */
  private static final int MAX_SYMBOLIC = 4;

  /** Whether the matrix entries are numbers. */
  private static boolean isNumeric(IExpr[][] a) {
    for (IExpr[] row : a) {
      for (IExpr x : row) {
        if (!x.isNumericFunction(true)) {
          return false;
        }
      }
    }
    return true;
  }

  /** Whether the matrix entries contain no inexact number. */
  private static boolean isExact(IExpr[][] a) {
    for (IExpr[] row : a) {
      for (IExpr x : row) {
        if (!x.isFree(e -> e.isInexactNumber(), false)) {
          return false;
        }
      }
    }
    return true;
  }

  private static int rank(IExpr matrix, EvalEngine engine) {
    return engine.evaluate(F.MatrixRank(matrix)).toIntDefault();
  }

  // ---------------------------------------------------------------------------------------------
  // invariant factors (Smith form of x*I - A over Q[x])
  // ---------------------------------------------------------------------------------------------

  /**
   * The invariant factors <code>d1 | d2 | ... | dn</code> of the exact numeric square matrix
   * <code>a</code> as monic polynomials in <code>x</code>, the units <code>1</code> included; or
   * <code>null</code>.
   */
  private static List<IExpr> invariantFactors(IExpr[][] a, ISymbol x, EvalEngine engine) {
    final int n = a.length;
    IExpr[][] m = new IExpr[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        IExpr entry = F.Negate(a[i][j]);
        m[i][j] = expand(i == j ? F.Plus(x, entry) : entry, engine);
      }
    }
    List<IExpr> factors = new ArrayList<IExpr>();
    for (int t = 0; t < n; t++) {
      int guard = 0;
      while (true) {
        engine.checkTimeLimit();
        if (++guard > 10_000) {
          return null;
        }
        // the non-zero entry of the least degree as pivot
        int pi = -1;
        int pj = -1;
        int least = Integer.MAX_VALUE;
        for (int i = t; i < n; i++) {
          for (int j = t; j < n; j++) {
            if (!m[i][j].isZero()) {
              int d = degree(m[i][j], x, engine);
              if (d < least) {
                least = d;
                pi = i;
                pj = j;
              }
            }
          }
        }
        if (pi < 0) {
          return null;
        }
        swapRows(m, t, pi);
        swapColumns(m, t, pj);
        boolean clean = true;
        for (int i = t + 1; i < n; i++) {
          if (!m[i][t].isZero()) {
            IExpr[] qr = quotientRemainder(m[i][t], m[t][t], x, engine);
            if (qr == null) {
              return null;
            }
            for (int j = t; j < n; j++) {
              m[i][j] = expand(F.Subtract(m[i][j], F.Times(qr[0], m[t][j])), engine);
            }
            clean &= qr[1].isZero();
          }
        }
        for (int j = t + 1; j < n; j++) {
          if (!m[t][j].isZero()) {
            IExpr[] qr = quotientRemainder(m[t][j], m[t][t], x, engine);
            if (qr == null) {
              return null;
            }
            for (int i = t; i < n; i++) {
              m[i][j] = expand(F.Subtract(m[i][j], F.Times(qr[0], m[i][t])), engine);
            }
            clean &= qr[1].isZero();
          }
        }
        if (!clean) {
          continue;
        }
        int bad = -1;
        for (int i = t + 1; i < n && bad < 0; i++) {
          for (int j = t + 1; j < n; j++) {
            if (!m[i][j].isZero()) {
              IExpr[] qr = quotientRemainder(m[i][j], m[t][t], x, engine);
              if (qr == null) {
                return null;
              }
              if (!qr[1].isZero()) {
                bad = i;
                break;
              }
            }
          }
        }
        if (bad < 0) {
          break;
        }
        for (int j = t; j < n; j++) {
          m[t][j] = expand(F.Plus(m[t][j], m[bad][j]), engine);
        }
      }
      IExpr lc = engine.evaluate(F.Coefficient(m[t][t], x, F.ZZ(degree(m[t][t], x, engine))));
      factors.add(expand(F.Divide(m[t][t], lc), engine));
    }
    return factors;
  }

  /**
   * The expanded expression; with symbolic coefficients, which can be quotients, the expression
   * over a common denominator, so that a zero entry is recognized.
   */
  private static IExpr expand(IExpr expr, EvalEngine engine) {
    IExpr expanded = engine.evaluate(F.Expand(expr));
    if (expanded.isFree(e -> e.isPower() && e.exponent().isNegativeResult(), true)) {
      return expanded;
    }
    return engine.evaluate(F.Together(expanded));
  }

  private static int degree(IExpr p, ISymbol x, EvalEngine engine) {
    return engine.evaluate(F.Exponent(p, x)).toIntDefault();
  }

  private static IExpr[] quotientRemainder(IExpr p, IExpr q, ISymbol x, EvalEngine engine) {
    IExpr qr = engine.evaluate(F.PolynomialQuotientRemainder(p, q, x));
    if (!qr.isList2()) {
      return null;
    }
    return new IExpr[] {expand(qr.first(), engine), expand(qr.second(), engine)};
  }

  private static void swapRows(IExpr[][] m, int a, int b) {
    if (a != b) {
      IExpr[] row = m[a];
      m[a] = m[b];
      m[b] = row;
    }
  }

  private static void swapColumns(IExpr[][] m, int a, int b) {
    if (a != b) {
      for (IExpr[] row : m) {
        IExpr temp = row[a];
        row[a] = row[b];
        row[b] = temp;
      }
    }
  }

  // ---------------------------------------------------------------------------------------------
  // evaluators
  // ---------------------------------------------------------------------------------------------

  /**
   * <code>LDLDecomposition(m)</code> - <code>{l, d}</code> with a unit lower triangular
   * <code>l</code> and a diagonal <code>d</code> such that
   * <code>m == l . d . ConjugateTranspose(l)</code> for a Hermitian matrix <code>m</code>. A matrix
   * which isn't known to be Hermitian, like a symmetric matrix with symbolic entries, gives the
   * message <code>herm</code>.
   */
  private static final class LDLDecomposition extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr[][] a = squareMatrix(ast.arg1());
      if (a == null) {
        return F.NIL;
      }
      if (!engine.evaluate(F.unaryAST1(S.HermitianMatrixQ, ast.arg1())).isTrue()) {
        // The matrix `1` is not hermitian or real and symmetric.
        return Errors.printMessage(S.LDLDecomposition, "herm", F.list(ast.arg1()), engine);
      }
      final int n = a.length;
      IExpr[][] l = new IExpr[n][n];
      IExpr[] d = new IExpr[n];
      for (int j = 0; j < n; j++) {
        engine.checkTimeLimit();
        IASTAppendable dj = F.PlusAlloc(j + 1);
        dj.append(a[j][j]);
        for (int k = 0; k < j; k++) {
          dj.append(F.Times(F.CN1, l[j][k], F.Conjugate(l[j][k]), d[k]));
        }
        d[j] = simplify(dj.oneIdentity0(), engine);
        for (int i = 0; i < n; i++) {
          if (i < j) {
            l[i][j] = F.C0;
          } else if (i == j) {
            l[i][j] = F.C1;
          } else {
            IASTAppendable lij = F.PlusAlloc(j + 1);
            lij.append(a[i][j]);
            for (int k = 0; k < j; k++) {
              lij.append(
                  F.Times(F.CN1, l[i][k], F.Conjugate(l[j][k]), d[k]));
            }
            IExpr numerator = simplify(lij.oneIdentity0(), engine);
            if (d[j].isZero()) {
              if (!numerator.isZero()) {
                // no LDL decomposition without pivoting
                return F.NIL;
              }
              l[i][j] = F.C0;
            } else {
              l[i][j] = simplify(F.Divide(numerator, d[j]), engine);
            }
          }
        }
      }
      return F.list(toList(l), engine.evaluate(F.DiagonalMatrix(F.List(d))));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>JordanReduce(m)</code> - the Jordan canonical form of the square matrix <code>m</code>:
   * the eigenvalues on the diagonal and a <code>1</code> above the diagonal inside each block.
   * Distinct eigenvalues keep the order of <code>Eigenvalues</code>; otherwise the eigenvalues are
   * sorted by real and imaginary part, the blocks of one eigenvalue by size.
   */
  private static final class JordanReduce extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr[][] a = squareMatrix(ast.arg1());
      if (a == null) {
        return F.NIL;
      }
      final int n = a.length;
      IExpr eigenvalues = engine.evaluate(F.Eigenvalues(ast.arg1()));
      if (!eigenvalues.isList() || eigenvalues.argSize() != n
          || !eigenvalues.isFree(x -> x.isAST(S.Root) || x.isAST(S.Eigenvalues), true)) {
        return F.NIL;
      }
      // distinct eigenvalues with their algebraic multiplicities, first seen first
      List<IExpr> distinct = new ArrayList<IExpr>();
      List<Integer> multiplicity = new ArrayList<Integer>();
      for (IExpr lambda : (IAST) eigenvalues) {
        int index = distinct.indexOf(lambda);
        if (index < 0) {
          distinct.add(lambda);
          multiplicity.add(1);
        } else {
          multiplicity.set(index, multiplicity.get(index) + 1);
        }
      }
      List<IExpr> blockValues = new ArrayList<IExpr>();
      List<Integer> blockSizes = new ArrayList<Integer>();
      if (distinct.size() == n) {
        for (IExpr lambda : distinct) {
          blockValues.add(lambda);
          blockSizes.add(1);
        }
      } else {
        Integer[] order = new Integer[distinct.size()];
        double[][] keys = new double[distinct.size()][];
        for (int i = 0; i < order.length; i++) {
          order[i] = i;
          try {
            org.hipparchus.complex.Complex c = distinct.get(i).evalfc();
            keys[i] = new double[] {c.getReal(), c.getImaginary()};
          } catch (RuntimeException rex) {
            if (distinct.size() > 1) {
              return F.NIL;
            }
            keys[i] = new double[] {0.0, 0.0};
          }
        }
        java.util.Arrays.sort(order, (p, q) -> {
          int c = Double.compare(keys[p][0], keys[q][0]);
          return c != 0 ? c : Double.compare(keys[p][1], keys[q][1]);
        });
        for (int index : order) {
          IExpr lambda = distinct.get(index);
          int mult = multiplicity.get(index);
          if (mult == 1) {
            blockValues.add(lambda);
            blockSizes.add(1);
            continue;
          }
          // r(k) = rank((m - lambda I)^k); r(k-1) - r(k) blocks have a size >= k
          IExpr shifted = engine.evaluate(
              F.Subtract(ast.arg1(), F.Times(lambda, F.IdentityMatrix(F.ZZ(n)))));
          List<Integer> ranks = new ArrayList<Integer>();
          ranks.add(n);
          IExpr power = shifted;
          while (true) {
            engine.checkTimeLimit();
            int r = rank(power, engine);
            if (r < 0) {
              return F.NIL;
            }
            ranks.add(r);
            if (r <= n - mult || ranks.size() > mult + 1) {
              break;
            }
            power = engine.evaluate(F.Dot(power, shifted));
          }
          if (ranks.get(ranks.size() - 1) != n - mult) {
            return F.NIL;
          }
          int kmax = ranks.size() - 1;
          for (int k = 1; k <= kmax; k++) {
            int atLeastK = ranks.get(k - 1) - ranks.get(k);
            int atLeastK1 = k + 1 <= kmax ? ranks.get(k) - ranks.get(k + 1) : 0;
            for (int c = 0; c < atLeastK - atLeastK1; c++) {
              blockValues.add(lambda);
              blockSizes.add(k);
            }
          }
        }
      }
      IExpr[][] j = new IExpr[n][n];
      for (IExpr[] row : j) {
        java.util.Arrays.fill(row, F.C0);
      }
      int offset = 0;
      for (int b = 0; b < blockValues.size(); b++) {
        int size = blockSizes.get(b);
        for (int i = 0; i < size; i++) {
          j[offset + i][offset + i] = blockValues.get(b);
          if (i + 1 < size) {
            j[offset + i][offset + i + 1] = F.C1;
          }
        }
        offset += size;
      }
      return offset == n ? toList(j) : F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>FrobeniusReduce(m)</code> - the rational canonical form of the exact square matrix
   * <code>m</code> (for symbolic entries the generic form, which assumes that no leading coefficient
   * vanishes): the companion matrices of the non-constant invariant factors
   * <code>d1 | d2 | ...</code> on the diagonal. The companion matrix of
   * <code>x^k + c(k-1)*x^(k-1) + ... + c0</code> has ones below the diagonal and
   * <code>-c0, ..., -c(k-1)</code> in the last column.
   */
  private static final class FrobeniusReduce extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr[][] a = squareMatrix(ast.arg1());
      if (a == null || !isExact(a) || (a.length > MAX_SYMBOLIC && !isNumeric(a))) {
        return F.NIL;
      }
      ISymbol x = F.Dummy("x");
      List<IExpr> factors = invariantFactors(a, x, engine);
      if (factors == null) {
        return F.NIL;
      }
      final int n = a.length;
      IExpr[][] result = new IExpr[n][n];
      for (IExpr[] row : result) {
        java.util.Arrays.fill(row, F.C0);
      }
      int offset = 0;
      for (IExpr factor : factors) {
        int k = degree(factor, x, engine);
        if (k <= 0) {
          continue;
        }
        IExpr coefficients = engine.evaluate(F.CoefficientList(factor, x));
        for (int i = 0; i < k; i++) {
          if (i > 0) {
            result[offset + i][offset + i - 1] = F.C1;
          }
          result[offset + i][offset + k - 1] =
              engine.evaluate(F.Together(F.Negate(((IAST) coefficients).get(i + 1))));
        }
        offset += k;
      }
      return offset == n ? toList(result) : F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>DrazinInverse(m)</code> - <code>m^k . PseudoInverse(m^(2k+1)) . m^k</code> with the
   * index <code>k</code> of <code>m</code>, the least <code>k</code> with
   * <code>MatrixRank(m^k) == MatrixRank(m^(k+1))</code>.
   */
  private static final class DrazinInverse extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr m = ast.arg1();
      IExpr[][] a = squareMatrix(m);
      if (a == null) {
        return F.NIL;
      }
      final int n = a.length;
      int previous = n;
      IExpr power = F.IdentityMatrix(F.ZZ(n));
      int k = 0;
      while (true) {
        engine.checkTimeLimit();
        IExpr next = engine.evaluate(F.Dot(power, m));
        int r = rank(next, engine);
        if (r < 0) {
          return F.NIL;
        }
        if (r == previous) {
          break;
        }
        previous = r;
        power = next;
        k++;
        if (k > n) {
          return F.NIL;
        }
      }
      if (k == 0) {
        return engine.evaluate(F.Inverse(m));
      }
      IExpr mk = engine.evaluate(F.MatrixPower(m, F.ZZ(k)));
      IExpr pinv =
          engine.evaluate(F.PseudoInverse(engine.evaluate(F.MatrixPower(m, F.ZZ(2 * k + 1)))));
      if (pinv.isMatrix() == null) {
        return F.NIL;
      }
      IExpr result = engine.evaluate(F.Dot(mk, pinv, mk));
      return result.isList() ? ((IAST) result).map(row -> row.isList()
          ? ((IAST) row).map(x -> simplify(x, engine), 1) : row, 1) : F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>PfaffianDet(m)</code> - the Pfaffian of the antisymmetric matrix <code>m</code>, whose
   * square is <code>Det(m)</code>; <code>0</code> for an odd dimension.
   */
  private static final class PfaffianDet extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr[][] a = squareMatrix(ast.arg1());
      if (a == null || !engine.evaluate(F.unaryAST1(S.AntisymmetricMatrixQ, ast.arg1())).isTrue()) {
        return F.NIL;
      }
      final int n = a.length;
      if (n % 2 == 1) {
        return F.C0;
      }
      IExpr pf = F.C1;
      for (int k = 0; k < n; k += 2) {
        engine.checkTimeLimit();
        int p = -1;
        for (int j = k + 1; j < n; j++) {
          if (!a[k][j].isZero()) {
            p = j;
            break;
          }
        }
        if (p < 0) {
          return F.C0;
        }
        if (p != k + 1) {
          swapRows(a, p, k + 1);
          swapColumns(a, p, k + 1);
          pf = F.Negate(pf);
        }
        IExpr pivot = a[k][k + 1];
        pf = F.Times(pf, pivot);
        // the Schur complement of the leading 2x2 block
        for (int i = k + 2; i < n; i++) {
          for (int j = k + 2; j < n; j++) {
            a[i][j] = simplify(F.Plus(a[i][j],
                F.Divide(F.Subtract(F.Times(a[k + 1][i], a[k][j]), F.Times(a[k][i], a[k + 1][j])),
                    pivot)),
                engine);
          }
        }
      }
      return simplify(pf, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>DiagonalizableMatrixQ(m)</code> - <code>True</code> if the square matrix <code>m</code>
   * is diagonalizable over the complex numbers: its minimal polynomial, the last invariant factor,
   * has no multiple root. Inexact matrices are rationalized first; for symbolic entries the answer
   * is the one for generic values of the symbols.
   */
  private static final class DiagonalizableMatrixQ extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr m = ast.arg1();
      if (!m.isFree(x -> x.isInexactNumber(), false)) {
        m = engine.evaluate(F.Rationalize(m, F.C0));
      }
      IExpr[][] a = squareMatrix(m);
      if (a == null) {
        return S.False;
      }
      if (!isExact(a) || (a.length > MAX_SYMBOLIC && !isNumeric(a))) {
        return F.NIL;
      }
      ISymbol x = F.Dummy("x");
      List<IExpr> factors = invariantFactors(a, x, engine);
      if (factors == null) {
        return F.NIL;
      }
      // the numerator: with symbolic denominators the factor is a quotient, and the GCD of two
      // quotients would only be the GCD of their denominators
      IExpr minimal =
          engine.evaluate(F.Numerator(F.Together(factors.get(factors.size() - 1))));
      // for symbolic entries the GCD in all variables gives the generic answer
      IExpr gcd = engine.evaluate(F.PolynomialGCD(minimal, F.D(minimal, x)));
      return F.booleSymbol(degree(gcd, x, engine) == 0);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>LyapunovSolve(a, c)</code> - the solution <code>x</code> of
   * <code>a.x + x.ConjugateTranspose(a) == c</code>; <code>LyapunovSolve(a, b, c)</code> of the
   * Sylvester equation <code>a.x + x.b == c</code>. <code>DiscreteLyapunovSolve(a, c)</code> solves
   * <code>a.x.ConjugateTranspose(a) - x == c</code>, <code>DiscreteLyapunovSolve(a, b, c)</code>
   * <code>a.x.b - x == c</code>. The equation is written as a linear system for the columns of
   * <code>x</code>.
   */
  private static final class LyapunovSolve extends AbstractFunctionEvaluator {
    final boolean discrete;

    LyapunovSolve(boolean discrete) {
      this.discrete = discrete;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.isAST3() ? ast.arg2() : engine.evaluate(F.ConjugateTranspose(a));
      IExpr c = ast.last();
      int[] da = a.isMatrix(false);
      int[] db = b.isMatrix(false);
      int[] dc = c.isMatrix(false);
      if (da == null || db == null || dc == null || da[0] != da[1] || db[0] != db[1]
          || dc[0] != da[0] || dc[1] != db[0]) {
        return F.NIL;
      }
      int rows = dc[0];
      int columns = dc[1];
      IExpr ia = F.IdentityMatrix(F.ZZ(rows));
      IExpr ib = F.IdentityMatrix(F.ZZ(columns));
      IExpr bt = F.Transpose(b);
      IExpr kronecker;
      if (discrete) {
        // vec(a.x.b) - vec(x) = (Transpose(b) (x) a - I) vec(x)
        kronecker = F.Subtract(F.binaryAST2(S.KroneckerProduct, bt, a),
            F.IdentityMatrix(F.ZZ(rows * columns)));
      } else {
        // vec(a.x + x.b) = (I (x) a + Transpose(b) (x) I) vec(x)
        kronecker = F.Plus(F.binaryAST2(S.KroneckerProduct, ib, a),
            F.binaryAST2(S.KroneckerProduct, bt, ia));
      }
      IExpr system = engine.evaluate(kronecker);
      IExpr vec = engine.evaluate(F.Flatten(F.Transpose(c)));
      if (system.isMatrix() == null || !vec.isList()) {
        return F.NIL;
      }
      IExpr det = engine.evaluate(F.Det(system));
      IExpr solution;
      if (det.isZero()) {
        // no unique solution: a particular one if the equation is consistent
        solution = engine.evaluate(F.Quiet(F.LinearSolve(system, vec)));
        if (!solution.isList()) {
          return Errors.printMessage(ast.topHead(), "nosol", F.CEmptyList, engine);
        }
      } else {
        solution = engine.evaluate(F.LinearSolve(system, vec));
      }
      if (!solution.isList() || solution.argSize() != rows * columns) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(rows);
      for (int i = 0; i < rows; i++) {
        IASTAppendable row = F.ListAlloc(columns);
        for (int j = 0; j < columns; j++) {
          row.append(simplify(((IAST) solution).get(j * rows + i + 1), engine));
        }
        result.append(row);
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private MatrixDecompositionFunctions() {}
}
