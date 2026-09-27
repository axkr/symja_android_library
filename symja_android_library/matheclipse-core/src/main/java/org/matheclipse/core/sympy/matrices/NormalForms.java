package org.matheclipse.core.sympy.matrices;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;

/**
 * Functions returning normal forms of integer matrices. Ported from
 * <a href="https://github.com/sympy/sympy/blob/master/sympy/polys/matrices/normalforms.py">sympy/polys/matrices/normalforms.py</a>
 */
public class NormalForms {

  private static final class Decomposition {
    List<BigInteger> invs;
    BigInteger[][] s;
    BigInteger[][] t;
  }

  private NormalForms() {}

  /**
   * Return the Smith Normal Form of the integer matrix <code>m</code>.
   *
   * @param m a matrix with <code>rows &gt; 0</code> and <code>columns &gt; 0</code>
   */
  public static BigInteger[][] smithNormalForm(BigInteger[][] m) {
    // >>> m = DomainMatrix([[ZZ(12), ZZ(6), ZZ(4)],
    // ... [ZZ(3), ZZ(9), ZZ(6)],
    // ... [ZZ(2), ZZ(16), ZZ(14)]], (3, 3), ZZ)
    // >>> print(smith_normal_form(m).to_Matrix())
    // Matrix([[1, 0, 0], [0, 10, 0], [0, 0, 30]])
    final int rows = m.length;
    final int cols = rows == 0 ? 0 : m[0].length;
    return diag(invariantFactors(m), rows, cols);
  }

  /**
   * Checks that the matrix is in Smith Normal Form
   */
  public static boolean isSmithNormalForm(BigInteger[][] m) {
    final int rows = m.length;
    final int cols = rows == 0 ? 0 : m[0].length;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        if (i != j && m[i][j].signum() != 0) {
          return false;
        }
      }
    }
    final int upper = Math.min(rows, cols);
    for (int i = 1; i < upper; i++) {
      if (m[i - 1][i - 1].signum() == 0) {
        if (m[i][i].signum() != 0) {
          return false;
        }
      } else if (m[i][i].mod(m[i - 1][i - 1].abs()).signum() != 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Return the abelian invariants for the integer matrix <code>m</code> (as in the Smith-Normal
   * form).
   */
  public static BigInteger[] invariantFactors(BigInteger[][] m) {
    final int rows = m.length;
    final int cols = rows == 0 ? 0 : m[0].length;
    List<BigInteger> invs = smithNormalDecomp(copy(m), rows, cols, false).invs;
    return invs.toArray(new BigInteger[invs.size()]);
  }

  /**
   * Return the Smith-Normal form decomposition of the integer matrix <code>m</code>.
   *
   * @return the matrices <code>{a, s, t}</code> with <code>a == s.m.t</code>, where
   *         <code>a</code> is the Smith normal form and <code>s, t</code> are unimodular
   */
  public static BigInteger[][][] smithNormalDecomp(BigInteger[][] m) {
    // >>> a, s, t = smith_normal_decomp(m)
    // >>> assert a == s * m * t
    final int rows = m.length;
    final int cols = rows == 0 ? 0 : m[0].length;
    Decomposition d = smithNormalDecomp(copy(m), rows, cols, true);
    BigInteger[] invs = d.invs.toArray(new BigInteger[d.invs.size()]);
    return new BigInteger[][][] {diag(invs, rows, cols), d.s, d.t};
  }

  /**
   * See {@link #smithNormalDecomp(BigInteger[][])}.
   *
   * @return the list of matrices <code>{a, s, t}</code>
   */
  public static IAST smithNormalDecompList(BigInteger[][] m) {
    BigInteger[][][] ast = smithNormalDecomp(m);
    return F.List(toList(ast[0]), toList(ast[1]), toList(ast[2]));
  }

  public static IAST toList(BigInteger[][] m) {
    IASTAppendable result = F.ListAlloc(m.length);
    for (int i = 0; i < m.length; i++) {
      IASTAppendable row = F.ListAlloc(m[i].length);
      for (int j = 0; j < m[i].length; j++) {
        row.append(F.ZZ(m[i][j]));
      }
      result.append(row);
    }
    return result;
  }

  private static BigInteger[][] copy(BigInteger[][] m) {
    BigInteger[][] result = new BigInteger[m.length][];
    for (int i = 0; i < m.length; i++) {
      result[i] = m[i].clone();
    }
    return result;
  }

  private static BigInteger[][] diag(BigInteger[] invs, int rows, int cols) {
    BigInteger[][] result = new BigInteger[rows][cols];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        result[i][j] = (i == j && i < invs.length) ? invs[i] : BigInteger.ZERO;
      }
    }
    return result;
  }

  private static BigInteger[][] eye(int n) {
    BigInteger[][] result = new BigInteger[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        result[i][j] = (i == j) ? BigInteger.ONE : BigInteger.ZERO;
      }
    }
    return result;
  }

  private static BigInteger[][] multiply(BigInteger[][] a, BigInteger[][] b) {
    final int n = a.length;
    final int k = b.length;
    final int c = k == 0 ? 0 : b[0].length;
    BigInteger[][] result = new BigInteger[n][c];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < c; j++) {
        BigInteger sum = BigInteger.ZERO;
        for (int l = 0; l < k; l++) {
          if (a[i][l].signum() != 0 && b[l][j].signum() != 0) {
            sum = sum.add(a[i][l].multiply(b[l][j]));
          }
        }
        result[i][j] = sum;
      }
    }
    return result;
  }

  /**
   * Extended Euclidean algorithm.
   *
   * @return <code>{x, y, g}</code> with <code>x*a + y*b == g</code> and <code>g &gt;= 0</code>
   */
  private static BigInteger[] gcdex(BigInteger a, BigInteger b) {
    BigInteger oldR = a;
    BigInteger r = b;
    BigInteger oldX = BigInteger.ONE;
    BigInteger x = BigInteger.ZERO;
    BigInteger oldY = BigInteger.ZERO;
    BigInteger y = BigInteger.ONE;
    while (r.signum() != 0) {
      BigInteger q = oldR.divide(r);
      BigInteger tmp = oldR.subtract(q.multiply(r));
      oldR = r;
      r = tmp;
      tmp = oldX.subtract(q.multiply(x));
      oldX = x;
      x = tmp;
      tmp = oldY.subtract(q.multiply(y));
      oldY = y;
      y = tmp;
    }
    if (oldR.signum() < 0) {
      return new BigInteger[] {oldX.negate(), oldY.negate(), oldR.negate()};
    }
    return new BigInteger[] {oldX, oldY, oldR};
  }

  private static void addColumns(BigInteger[][] m, int i, int j, BigInteger a, BigInteger b,
      BigInteger c, BigInteger d) {
    // replace m[:, i] by a*m[:, i] + b*m[:, j]
    // and m[:, j] by c*m[:, i] + d*m[:, j]
    for (int k = 0; k < m.length; k++) {
      BigInteger e = m[k][i];
      m[k][i] = a.multiply(e).add(b.multiply(m[k][j]));
      m[k][j] = c.multiply(e).add(d.multiply(m[k][j]));
    }
  }

  private static void addRows(BigInteger[][] m, int i, int j, BigInteger a, BigInteger b,
      BigInteger c, BigInteger d) {
    // replace m[i, :] by a*m[i, :] + b*m[j, :]
    // and m[j, :] by c*m[i, :] + d*m[j, :]
    for (int k = 0; k < m[0].length; k++) {
      BigInteger e = m[i][k];
      m[i][k] = a.multiply(e).add(b.multiply(m[j][k]));
      m[j][k] = c.multiply(e).add(d.multiply(m[j][k]));
    }
  }

  private static boolean firstRowOrColumnNonZero(BigInteger[][] m, int rows, int cols) {
    for (int i = 1; i < cols; i++) {
      if (m[0][i].signum() != 0) {
        return true;
      }
    }
    for (int i = 1; i < rows; i++) {
      if (m[i][0].signum() != 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * Return the abelian invariants for a matrix <code>m</code> (as in the Smith-Normal form). If
   * <code>full</code> is <code>true</code> then invertible matrices <code>s, t</code> such that
   * the product <code>s.m.t</code> is the Smith Normal Form are also returned.
   *
   * @param m the matrix is modified
   */
  private static Decomposition smithNormalDecomp(BigInteger[][] m, final int rows, final int cols,
      final boolean full) {
    final BigInteger ONE = BigInteger.ONE;
    final BigInteger ZERO = BigInteger.ZERO;
    final BigInteger MINUS_ONE = ONE.negate();
    Decomposition result = new Decomposition();
    result.invs = new ArrayList<BigInteger>();
    if (rows == 0 || cols == 0) {
      if (full) {
        result.s = eye(rows);
        result.t = eye(cols);
      }
      return result;
    }
    BigInteger[][] s = full ? eye(rows) : null;
    BigInteger[][] t = full ? eye(cols) : null;

    // permute the rows and columns until m[0,0] is non-zero if possible
    int ind = -1;
    for (int i = 0; i < rows; i++) {
      if (m[i][0].signum() != 0) {
        ind = i;
        break;
      }
    }
    if (ind > 0) {
      BigInteger[] tmp = m[0];
      m[0] = m[ind];
      m[ind] = tmp;
      if (full) {
        tmp = s[0];
        s[0] = s[ind];
        s[ind] = tmp;
      }
    } else {
      ind = -1;
      for (int j = 0; j < cols; j++) {
        if (m[0][j].signum() != 0) {
          ind = j;
          break;
        }
      }
      if (ind > 0) {
        for (int i = 0; i < rows; i++) {
          BigInteger tmp = m[i][0];
          m[i][0] = m[i][ind];
          m[i][ind] = tmp;
        }
        if (full) {
          for (int i = 0; i < cols; i++) {
            BigInteger tmp = t[i][0];
            t[i][0] = t[i][ind];
            t[i][ind] = tmp;
          }
        }
      }
    }

    // make the first row and column except m[0,0] zero
    while (firstRowOrColumnNonZero(m, rows, cols)) {
      // clear_column: make m[1:, 0] zero by row and column operations
      BigInteger pivot = m[0][0];
      for (int j = 1; j < rows; j++) {
        if (m[j][0].signum() == 0) {
          continue;
        }
        if (pivot.signum() != 0 && m[j][0].remainder(pivot).signum() == 0) {
          BigInteger d = m[j][0].divide(pivot);
          addRows(m, 0, j, ONE, ZERO, d.negate(), ONE);
          if (full) {
            addRows(s, 0, j, ONE, ZERO, d.negate(), ONE);
          }
        } else {
          BigInteger[] abg = gcdex(pivot, m[j][0]);
          BigInteger g = abg[2];
          BigInteger d0 = m[j][0].divide(g);
          BigInteger dj = pivot.divide(g);
          addRows(m, 0, j, abg[0], abg[1], d0, dj.negate());
          if (full) {
            addRows(s, 0, j, abg[0], abg[1], d0, dj.negate());
          }
          pivot = g;
        }
      }
      // clear_row: make m[0, 1:] zero by row and column operations
      pivot = m[0][0];
      for (int j = 1; j < cols; j++) {
        if (m[0][j].signum() == 0) {
          continue;
        }
        if (pivot.signum() != 0 && m[0][j].remainder(pivot).signum() == 0) {
          BigInteger d = m[0][j].divide(pivot);
          addColumns(m, 0, j, ONE, ZERO, d.negate(), ONE);
          if (full) {
            addColumns(t, 0, j, ONE, ZERO, d.negate(), ONE);
          }
        } else {
          BigInteger[] abg = gcdex(pivot, m[0][j]);
          BigInteger g = abg[2];
          BigInteger d0 = m[0][j].divide(g);
          BigInteger dj = pivot.divide(g);
          addColumns(m, 0, j, abg[0], abg[1], d0, dj.negate());
          if (full) {
            addColumns(t, 0, j, abg[0], abg[1], d0, dj.negate());
          }
          pivot = g;
        }
      }
    }

    if (m[0][0].signum() < 0) {
      // c = domain.canonical_unit(m[0][0])
      m[0][0] = m[0][0].negate();
      if (full) {
        for (int i = 0; i < s[0].length; i++) {
          s[0][i] = s[0][i].negate();
        }
      }
    }

    List<BigInteger> invs;
    if (rows == 1 || cols == 1) {
      invs = new ArrayList<BigInteger>();
    } else {
      // lower_right = [r[1:] for r in m[1:]]
      BigInteger[][] lowerRight = new BigInteger[rows - 1][cols - 1];
      for (int i = 1; i < rows; i++) {
        System.arraycopy(m[i], 1, lowerRight[i - 1], 0, cols - 1);
      }
      Decomposition ret = smithNormalDecomp(lowerRight, rows - 1, cols - 1, full);
      invs = ret.invs;
      if (full) {
        // s2 = [[1] + [0]*(rows-1)] + [[0] + row for row in s_small]
        // t2 = [[1] + [0]*(cols-1)] + [[0] + row for row in t_small]
        BigInteger[][] s2 = eye(rows);
        for (int i = 1; i < rows; i++) {
          System.arraycopy(ret.s[i - 1], 0, s2[i], 1, rows - 1);
        }
        BigInteger[][] t2 = eye(cols);
        for (int i = 1; i < cols; i++) {
          System.arraycopy(ret.t[i - 1], 0, t2[i], 1, cols - 1);
        }
        s = multiply(s2, s);
        t = multiply(t, t2);
      }
    }

    List<BigInteger> list = new ArrayList<BigInteger>(invs.size() + 1);
    if (m[0][0].signum() != 0) {
      list.add(m[0][0]);
      list.addAll(invs);
      // in case m[0] doesn't divide the invariants of the rest of the matrix
      for (int i = 0; i < list.size() - 1; i++) {
        BigInteger a = list.get(i);
        BigInteger b = list.get(i + 1);
        if (b.signum() != 0 && b.remainder(a).signum() != 0) {
          BigInteger[] xyd = gcdex(a, b);
          BigInteger d = xyd[2];
          BigInteger alpha = a.divide(d);
          if (full) {
            BigInteger beta = b.divide(d);
            addRows(s, i, i + 1, ONE, ZERO, xyd[0], ONE);
            addColumns(t, i, i + 1, ONE, xyd[1], ZERO, ONE);
            addRows(s, i, i + 1, ONE, alpha.negate(), ZERO, ONE);
            addColumns(t, i, i + 1, ONE, ZERO, beta.negate(), ONE);
            addRows(s, i, i + 1, ZERO, ONE, MINUS_ONE, ZERO);
          }
          list.set(i + 1, b.multiply(alpha));
          list.set(i, d);
        } else {
          break;
        }
      }
    } else {
      if (full) {
        if (rows > 1) {
          // s = s[1:] + [s[0]]
          BigInteger[] first = s[0];
          System.arraycopy(s, 1, s, 0, rows - 1);
          s[rows - 1] = first;
        }
        if (cols > 1) {
          // t = [row[1:] + [row[0]] for row in t]
          for (int i = 0; i < cols; i++) {
            BigInteger first = t[i][0];
            System.arraycopy(t[i], 1, t[i], 0, cols - 1);
            t[i][cols - 1] = first;
          }
        }
      }
      list.addAll(invs);
      list.add(m[0][0]);
    }
    result.invs = list;
    result.s = s;
    result.t = t;
    return result;
  }
}
