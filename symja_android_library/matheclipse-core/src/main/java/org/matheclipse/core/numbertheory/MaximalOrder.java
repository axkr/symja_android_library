package org.matheclipse.core.numbertheory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.reflection.system.HermiteDecomposition;

/**
 * The ring of integers <code>O_K</code> of <code>K = Q(phi)</code>, for an algebraic integer
 * <code>phi</code> with monic minimal polynomial <code>f</code>, computed with the Round 2 algorithm
 * of Pohst and Zassenhaus (H. Cohen, <i>A Course in Computational Algebraic Number Theory</i>,
 * Algorithm 6.1.8).
 *
 * <p>
 * The algorithm starts from the equation order <code>Z[phi]</code> and enlarges it at every prime
 * <code>p</code> whose square divides the discriminant of <code>f</code>: the <code>p</code>-radical
 * <code>I</code> of the order is the kernel of a power of the Frobenius map modulo <code>p</code>,
 * the ring of multipliers <code>{x : x*I &sube; I}</code> is <code>1/p</code> times the kernel of
 * the multiplication map into <code>End(I/p*I)</code>, and when it equals the order the order is
 * <code>p</code>-maximal. Both kernels are computed over <code>GF(p)</code> in coordinates of the
 * current order, whose structure constants are integers, so every step is exact.
 *
 * <p>
 * A basis is kept as a lower triangular integer matrix <code>B</code> and a denominator
 * <code>d</code>: the <code>i</code>-th basis element is <code>sum(B[i][j]/d*phi^j)</code>, which
 * has degree <code>i</code> in <code>phi</code>; the first one is <code>1</code>.
 */
public final class MaximalOrder {

  private final int n;

  /** The monic minimal polynomial of phi, ascending; f[n] == 1. */
  private final BigInteger[] f;

  private BigInteger[][] basis;

  private BigInteger denominator;

  /**
   * @param f the monic irreducible integer minimal polynomial of phi, coefficients ascending
   * @param discriminant the discriminant of <code>f</code>
   */
  public MaximalOrder(BigInteger[] f, IInteger discriminant) {
    this.n = f.length - 1;
    this.f = f;
    this.basis = new BigInteger[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        basis[i][j] = i == j ? BigInteger.ONE : BigInteger.ZERO;
      }
    }
    this.denominator = BigInteger.ONE;
    IAST factors = discriminant.abs().factorInteger();
    for (int i = 1; i < factors.size(); i++) {
      IInteger p = (IInteger) factors.get(i).first();
      int exponent = factors.get(i).second().toIntDefault();
      if (exponent >= 2) {
        pMaximize(p.toBigNumerator());
      }
    }
  }

  /** The basis matrix; the basis elements are its rows divided by {@link #denominator()}. */
  public BigInteger[][] basis() {
    return basis;
  }

  public BigInteger denominator() {
    return denominator;
  }

  /** The index <code>[O_K : Z[phi]]</code>. */
  public BigInteger index() {
    BigInteger diagonal = BigInteger.ONE;
    for (int i = 0; i < n; i++) {
      diagonal = diagonal.multiply(basis[i][i]);
    }
    return denominator.pow(n).divide(diagonal.abs());
  }

  private void pMaximize(BigInteger p) {
    while (true) {
      if (Thread.currentThread().isInterrupted()) {
        throw new IllegalStateException("interrupted");
      }
      BigInteger[][][] table = multiplicationTable();
      // the p-radical: the kernel of x -> x^(p^k) on O/pO with p^k >= n
      int k = 1;
      BigInteger q = p;
      while (q.compareTo(BigInteger.valueOf(n)) < 0) {
        q = q.multiply(p);
        k++;
      }
      BigInteger[][] frobenius = new BigInteger[n][];
      for (int i = 0; i < n; i++) {
        BigInteger[] x = unit(i);
        for (int step = 0; step < k; step++) {
          x = powerModP(x, p, table, p);
        }
        frobenius[i] = x;
      }
      List<BigInteger[]> radicalGenerators = leftKernelModP(frobenius, p);
      if (radicalGenerators.isEmpty()) {
        // the radical is p*O, so the order is p-maximal
        return;
      }
      for (int i = 0; i < n; i++) {
        radicalGenerators.add(scaledUnit(i, p));
      }
      BigInteger[][] radical = hnfLower(radicalGenerators);
      // the multipliers of the radical: x*beta_k in coordinates of the radical, modulo p
      BigInteger[][] multiplication = new BigInteger[n][n * n];
      for (int i = 0; i < n; i++) {
        for (int kk = 0; kk < n; kk++) {
          BigInteger[] product = multiply(unit(i), radical[kk], table);
          BigInteger[] coordinates = solveLower(radical, product);
          for (int l = 0; l < n; l++) {
            multiplication[i][kk * n + l] = coordinates[l].mod(p);
          }
        }
      }
      List<BigInteger[]> multiplierGenerators = leftKernelModP(multiplication, p);
      for (int i = 0; i < n; i++) {
        multiplierGenerators.add(scaledUnit(i, p));
      }
      BigInteger[][] h = hnfLower(multiplierGenerators);
      // the new order is 1/p * h in coordinates of the old one
      BigInteger[][] newBasis = new BigInteger[n][n];
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          BigInteger sum = BigInteger.ZERO;
          for (int l = j; l <= i; l++) {
            sum = sum.add(h[i][l].multiply(basis[l][j]));
          }
          newBasis[i][j] = sum;
        }
      }
      BigInteger newDenominator = denominator.multiply(p);
      BigInteger gcd = newDenominator;
      for (BigInteger[] row : newBasis) {
        for (BigInteger entry : row) {
          gcd = gcd.gcd(entry);
        }
      }
      if (!gcd.equals(BigInteger.ONE)) {
        for (BigInteger[] row : newBasis) {
          for (int j = 0; j < n; j++) {
            row[j] = row[j].divide(gcd);
          }
        }
        newDenominator = newDenominator.divide(gcd);
      }
      newBasis = hnfLower(toList(newBasis));
      if (sameVolume(newBasis, newDenominator, basis, denominator)) {
        return;
      }
      basis = newBasis;
      denominator = newDenominator;
    }
  }

  /** Do the lattices <code>b1/d1</code> and <code>b2/d2</code> have the same covolume? */
  private boolean sameVolume(BigInteger[][] b1, BigInteger d1, BigInteger[][] b2, BigInteger d2) {
    BigInteger det1 = BigInteger.ONE;
    BigInteger det2 = BigInteger.ONE;
    for (int i = 0; i < n; i++) {
      det1 = det1.multiply(b1[i][i]);
      det2 = det2.multiply(b2[i][i]);
    }
    return det1.abs().multiply(d2.pow(n)).equals(det2.abs().multiply(d1.pow(n)));
  }

  /** <code>T[i][j]</code>: the coordinates of <code>omega_i*omega_j</code> in the basis. */
  private BigInteger[][][] multiplicationTable() {
    BigInteger[][][] table = new BigInteger[n][n][];
    for (int i = 0; i < n; i++) {
      for (int j = i; j < n; j++) {
        BigInteger[] product = multiplyModF(basis[i], basis[j]);
        // omega_i*omega_j = product/d^2 = c*B/d, so c*B = product/d
        for (int l = 0; l < n; l++) {
          BigInteger[] qr = product[l].divideAndRemainder(denominator);
          if (qr[1].signum() != 0) {
            throw new ArithmeticException("not an order");
          }
          product[l] = qr[0];
        }
        table[i][j] = solveLower(basis, product);
        table[j][i] = table[i][j];
      }
    }
    return table;
  }

  /** The product of two polynomials in phi, reduced modulo the monic <code>f</code>. */
  private BigInteger[] multiplyModF(BigInteger[] a, BigInteger[] b) {
    BigInteger[] product = new BigInteger[2 * n - 1];
    for (int i = 0; i < product.length; i++) {
      product[i] = BigInteger.ZERO;
    }
    for (int i = 0; i < n; i++) {
      if (a[i].signum() == 0) {
        continue;
      }
      for (int j = 0; j < n; j++) {
        product[i + j] = product[i + j].add(a[i].multiply(b[j]));
      }
    }
    for (int i = product.length - 1; i >= n; i--) {
      BigInteger c = product[i];
      if (c.signum() != 0) {
        for (int j = 0; j <= n; j++) {
          product[i - n + j] = product[i - n + j].subtract(c.multiply(f[j]));
        }
      }
    }
    BigInteger[] result = new BigInteger[n];
    System.arraycopy(product, 0, result, 0, n);
    return result;
  }

  /** The product of two elements in coordinates of the order. */
  private BigInteger[] multiply(BigInteger[] a, BigInteger[] b, BigInteger[][][] table) {
    BigInteger[] result = new BigInteger[n];
    for (int l = 0; l < n; l++) {
      result[l] = BigInteger.ZERO;
    }
    for (int i = 0; i < n; i++) {
      if (a[i].signum() == 0) {
        continue;
      }
      for (int j = 0; j < n; j++) {
        if (b[j].signum() == 0) {
          continue;
        }
        BigInteger ab = a[i].multiply(b[j]);
        BigInteger[] t = table[i][j];
        for (int l = 0; l < n; l++) {
          result[l] = result[l].add(ab.multiply(t[l]));
        }
      }
    }
    return result;
  }

  private BigInteger[] powerModP(BigInteger[] x, BigInteger exponent, BigInteger[][][] table,
      BigInteger p) {
    BigInteger[] result = unit(0);
    BigInteger[] square = mod(x, p);
    BigInteger e = exponent;
    while (e.signum() > 0) {
      if (e.testBit(0)) {
        result = mod(multiply(result, square, table), p);
      }
      e = e.shiftRight(1);
      if (e.signum() > 0) {
        square = mod(multiply(square, square, table), p);
      }
    }
    return result;
  }

  /** Solve <code>c*L = w</code> for a lower triangular <code>L</code>; the solution is exact. */
  private BigInteger[] solveLower(BigInteger[][] lower, BigInteger[] w) {
    BigInteger[] c = new BigInteger[n];
    for (int j = n - 1; j >= 0; j--) {
      BigInteger s = w[j];
      for (int i = j + 1; i < n; i++) {
        s = s.subtract(c[i].multiply(lower[i][j]));
      }
      BigInteger[] qr = s.divideAndRemainder(lower[j][j]);
      if (qr[1].signum() != 0) {
        throw new ArithmeticException("not in the lattice");
      }
      c[j] = qr[0];
    }
    return c;
  }

  /**
   * A basis of <code>{a : a*A == 0 (mod p)}</code> for an <code>m x c</code> matrix
   * <code>A</code>, with entries in <code>[0, p)</code>.
   */
  private static List<BigInteger[]> leftKernelModP(BigInteger[][] a, BigInteger p) {
    int m = a.length;
    int c = a[0].length;
    // Gauss-Jordan on the transpose: columns of A^T are the unknowns a_1..a_m
    BigInteger[][] t = new BigInteger[c][m];
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < c; j++) {
        t[j][i] = a[i][j].mod(p);
      }
    }
    int[] pivotColumn = new int[c];
    boolean[] isPivot = new boolean[m];
    int row = 0;
    for (int col = 0; col < m && row < c; col++) {
      int r = row;
      while (r < c && t[r][col].signum() == 0) {
        r++;
      }
      if (r == c) {
        continue;
      }
      BigInteger[] tmp = t[r];
      t[r] = t[row];
      t[row] = tmp;
      BigInteger inverse = t[row][col].modInverse(p);
      for (int j = 0; j < m; j++) {
        t[row][j] = t[row][j].multiply(inverse).mod(p);
      }
      for (int i = 0; i < c; i++) {
        if (i != row && t[i][col].signum() != 0) {
          BigInteger factor = t[i][col];
          for (int j = 0; j < m; j++) {
            t[i][j] = t[i][j].subtract(factor.multiply(t[row][j])).mod(p);
          }
        }
      }
      pivotColumn[row] = col;
      isPivot[col] = true;
      row++;
    }
    List<BigInteger[]> kernel = new ArrayList<BigInteger[]>();
    for (int free = 0; free < m; free++) {
      if (isPivot[free]) {
        continue;
      }
      BigInteger[] v = new BigInteger[m];
      for (int j = 0; j < m; j++) {
        v[j] = BigInteger.ZERO;
      }
      v[free] = BigInteger.ONE;
      for (int r = 0; r < row; r++) {
        v[pivotColumn[r]] = t[r][free].negate().mod(p);
      }
      kernel.add(v);
    }
    return kernel;
  }

  /**
   * The lower triangular Hermite normal form of the lattice spanned by the rows: row
   * <code>i</code> has its last non-zero entry, which is positive, in column <code>i</code>.
   */
  private BigInteger[][] hnfLower(List<BigInteger[]> rows) {
    BigInteger[][] reversed = new BigInteger[rows.size()][n];
    for (int i = 0; i < rows.size(); i++) {
      BigInteger[] row = rows.get(i);
      for (int j = 0; j < n; j++) {
        reversed[i][j] = row[n - 1 - j];
      }
    }
    BigInteger[][] h = HermiteDecomposition.hermiteNormalForm(reversed);
    BigInteger[][] result = new BigInteger[n][n];
    for (int i = 0; i < n; i++) {
      BigInteger[] row = h[n - 1 - i];
      for (int j = 0; j < n; j++) {
        result[i][j] = row[n - 1 - j];
      }
      if (result[i][i].signum() < 0) {
        for (int j = 0; j < n; j++) {
          result[i][j] = result[i][j].negate();
        }
      }
      if (result[i][i].signum() == 0) {
        throw new ArithmeticException("lattice not of full rank");
      }
    }
    return result;
  }

  private static List<BigInteger[]> toList(BigInteger[][] matrix) {
    List<BigInteger[]> list = new ArrayList<BigInteger[]>(matrix.length);
    for (BigInteger[] row : matrix) {
      list.add(row);
    }
    return list;
  }

  private BigInteger[] unit(int i) {
    return scaledUnit(i, BigInteger.ONE);
  }

  private BigInteger[] scaledUnit(int i, BigInteger scale) {
    BigInteger[] v = new BigInteger[n];
    for (int j = 0; j < n; j++) {
      v[j] = j == i ? scale : BigInteger.ZERO;
    }
    return v;
  }

  private static BigInteger[] mod(BigInteger[] x, BigInteger p) {
    BigInteger[] result = new BigInteger[x.length];
    for (int i = 0; i < x.length; i++) {
      result[i] = x[i].mod(p);
    }
    return result;
  }

  /** The integer coefficients of a monic polynomial given with rational ones. */
  static BigInteger[] integerCoefficients(org.matheclipse.core.interfaces.IRational[] monic) {
    BigInteger[] result = new BigInteger[monic.length];
    for (int i = 0; i < monic.length; i++) {
      if (!monic[i].isInteger()) {
        return null;
      }
      result[i] = ((IInteger) monic[i]).toBigNumerator();
    }
    return result;
  }
}
