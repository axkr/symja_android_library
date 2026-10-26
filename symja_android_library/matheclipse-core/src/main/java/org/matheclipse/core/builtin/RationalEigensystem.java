package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.List;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The exact eigenvalues and eigenvectors of a square matrix of rational numbers.
 *
 * <p>
 * The characteristic polynomial is factored over the rationals. The roots of a factor of degree
 * one or two are written with radicals, the roots of a factor of a higher degree are
 * <code>Root</code> objects (the real roots of a binomial are radicals), which keeps nested
 * radicals out of the result. The eigenvector of an irrational eigenvalue <code>t</code> with
 * minimal polynomial <code>p</code> is computed by an exact elimination in the field
 * <code>Q(t) = Q[t]/p(t)</code>; each of its entries, a polynomial in <code>t</code>, is then
 * written as a rational number, a radical or a <code>Root</code> object of its own minimal
 * polynomial.
 *
 * <p>
 * The eigenvalues are sorted by decreasing absolute value. Of eigenvalues with the same absolute
 * value the <code>Root</code> objects come first; then they are sorted by increasing real and
 * decreasing imaginary part.
 */
final class RationalEigensystem {

  /** The largest dimension of a matrix whose complete eigensystem is computed exactly here. */
  static final int MAX_DIMENSION = 5;

  /** The largest dimension of a matrix for which only some eigenvectors are asked for. */
  static final int MAX_DIMENSION_SELECTED = 8;

  /** The relative tolerance for eigenvalues of equal absolute value or real part. */
  private static final double TIE_TOLERANCE = 1.0e-10;

  /** An eigenvalue with its multiplicity and, for a <code>Root</code> object, its field. */
  private static final class Eigenvalue {
    final IExpr value;
    final Complex numeric;
    final int multiplicity;
    /** The monic minimal polynomial of an irrational eigenvalue, <code>null</code> otherwise. */
    final IRational[] minimalPolynomial;

    Eigenvalue(IExpr value, Complex numeric, int multiplicity, IRational[] minimalPolynomial) {
      this.value = value;
      this.numeric = numeric;
      this.multiplicity = multiplicity;
      this.minimalPolynomial = minimalPolynomial;
    }

    boolean isRoot() {
      return value.isAST(S.Root);
    }
  }

  private final IAST matrix;
  private final int n;
  private final EvalEngine engine;

  RationalEigensystem(IAST matrix, int n, EvalEngine engine) {
    this.matrix = matrix;
    this.n = n;
    this.engine = engine;
  }

  /**
   * @param maxValues the number of eigenvalues asked for, or a negative number for all
   * @param onlyEigenvalues <code>true</code> for <code>{eigenvalues}</code> only
   * @return <code>{eigenvalues, eigenvectors}</code>, <code>{eigenvalues}</code>, or
   *         {@link F#NIL} if the characteristic polynomial can't be split
   */
  IAST eigensystem(int maxValues, boolean onlyEigenvalues) {
    List<Eigenvalue> eigenvalues = eigenvalues();
    if (eigenvalues == null) {
      return F.NIL;
    }
    eigenvalues.sort(RationalEigensystem::compare);
    final int count = maxValues >= 0 && maxValues < n ? maxValues : n;
    IASTAppendable values = F.ListAlloc(n);
    for (Eigenvalue eigenvalue : eigenvalues) {
      for (int k = 0; k < eigenvalue.multiplicity && values.argSize() < count; k++) {
        values.append(eigenvalue.value);
      }
    }
    if (onlyEigenvalues) {
      return F.list(values);
    }
    IASTAppendable vectors = F.ListAlloc(n);
    for (Eigenvalue eigenvalue : eigenvalues) {
      if (vectors.argSize() >= count) {
        break;
      }
      IAST nullSpace = eigenvalue.minimalPolynomial != null ? algebraicNullSpace(eigenvalue)
          : rationalNullSpace(eigenvalue.value);
      if (nullSpace.isNIL()) {
        return F.NIL;
      }
      int appended = 0;
      for (int j = 1; j <= nullSpace.argSize() && appended < eigenvalue.multiplicity; j++) {
        vectors.append(nullSpace.get(j));
        appended++;
      }
      // a defective eigenvalue has fewer eigenvectors than its multiplicity
      while (appended < eigenvalue.multiplicity) {
        vectors.append(F.constantArray(F.C0, n));
        appended++;
      }
    }
    return F.list(values, vectors.copyUntil(count + 1));
  }

  /**
   * Decreasing absolute value; then <code>Root</code> objects first, increasing real part and
   * decreasing imaginary part.
   */
  private static int compare(Eigenvalue a, Eigenvalue b) {
    double absA = a.numeric.norm();
    double absB = b.numeric.norm();
    if (!isTie(absA, absB)) {
      return absA > absB ? -1 : 1;
    }
    if (a.isRoot() != b.isRoot()) {
      return a.isRoot() ? -1 : 1;
    }
    double reA = a.numeric.getReal();
    double reB = b.numeric.getReal();
    if (!isTie(reA, reB)) {
      return reA < reB ? -1 : 1;
    }
    double imA = a.numeric.getImaginary();
    double imB = b.numeric.getImaginary();
    if (!isTie(imA, imB)) {
      return imA > imB ? -1 : 1;
    }
    return 0;
  }

  private static boolean isTie(double a, double b) {
    return Math.abs(a - b) <= TIE_TOLERANCE * Math.max(1.0, Math.max(Math.abs(a), Math.abs(b)));
  }

  /** The distinct eigenvalues, or <code>null</code> if a factor has no roots. */
  private List<Eigenvalue> eigenvalues() {
    ISymbol x = F.Dummy("x");
    IExpr characteristicPolynomial =
        engine.evaluate(F.Expand(LinearAlgebra.characteristicPolynomial(n, matrix, x)));
    if (!characteristicPolynomial.isPolynomial(x)) {
      return null;
    }
    IExpr factorList = engine.evaluate(F.FactorList(characteristicPolynomial));
    if (!factorList.isListOfLists()) {
      return null;
    }
    List<Eigenvalue> result = new ArrayList<Eigenvalue>();
    for (IExpr pair : (IAST) factorList) {
      IExpr factor = pair.first();
      int multiplicity = pair.second().toIntDefault();
      if (factor.isFree(x) || multiplicity <= 0) {
        continue;
      }
      IRational[] coefficients = coefficients(factor, x);
      if (coefficients == null) {
        return null;
      }
      int degree = coefficients.length - 1;
      IAST roots = rootsOfFactor(factor, x, coefficients);
      if (roots.isNIL() || roots.argSize() != degree) {
        return null;
      }
      IRational[] monic = degree > 1 ? monic(coefficients) : null;
      for (IExpr root : roots) {
        Complex numeric = numeric(root);
        if (numeric == null) {
          return null;
        }
        result.add(new Eigenvalue(root, numeric, multiplicity, monic));
      }
    }
    int total = 0;
    for (Eigenvalue eigenvalue : result) {
      total += eigenvalue.multiplicity;
    }
    return total == n ? result : null;
  }

  /**
   * The roots of an irreducible factor with rational coefficients: radicals for a degree up to
   * two, <code>Root</code> objects for a higher degree, except for the real roots of a binomial
   * <code>c*x^d + a</code>, which are radicals.
   */
  private IAST rootsOfFactor(IExpr factor, ISymbol x, IRational[] coefficients) {
    int degree = coefficients.length - 1;
    if (degree <= 2) {
      return RootsFunctions.roots(factor, false, F.list(x), false, true, engine);
    }
    boolean binomial = true;
    for (int i = 1; i < degree; i++) {
      binomial &= coefficients[i].isZero();
    }
    // x^d == a for a binomial
    final IRational a = coefficients[0].negate().divideBy(coefficients[degree]);
    IExpr function = F.Function(F.subst(factor, x, F.Slot1));
    IASTAppendable roots = F.ListAlloc(degree);
    for (int k = 1; k <= degree; k++) {
      IExpr root = engine.evaluate(F.Root(function, k));
      if (binomial) {
        Complex c = numeric(root);
        if (c != null && F.isZero(c.getImaginary())) {
          IExpr radical = F.Power(a.abs(), F.QQ(1, degree));
          root = engine.evaluate(c.getReal() < 0.0 ? F.Negate(radical) : radical);
        }
      }
      roots.append(root);
    }
    return roots;
  }

  private Complex numeric(IExpr value) {
    try {
      Complex c = engine.evalN(value).evalfc();
      return c.isNaN() || c.isInfinite() ? null : c;
    } catch (RuntimeException rex) {
      return null;
    }
  }

  /** The rational coefficients of a polynomial, the constant first, or <code>null</code>. */
  private IRational[] coefficients(IExpr polynomial, ISymbol x) {
    IExpr list = engine.evaluate(F.CoefficientList(polynomial, x));
    if (!list.isList() || list.argSize() < 2) {
      return null;
    }
    IRational[] coefficients = new IRational[list.argSize()];
    for (int i = 0; i < coefficients.length; i++) {
      IExpr c = list.getAt(i + 1);
      if (!c.isRational()) {
        return null;
      }
      coefficients[i] = (IRational) c;
    }
    return coefficients;
  }

  private static IRational[] monic(IRational[] coefficients) {
    IRational lead = coefficients[coefficients.length - 1];
    IRational[] monic = new IRational[coefficients.length];
    for (int i = 0; i < coefficients.length; i++) {
      monic[i] = coefficients[i].divideBy(lead);
    }
    return monic;
  }

  /** <code>NullSpace(m - lambda*IdentityMatrix(n))</code> for a rational eigenvalue. */
  private IAST rationalNullSpace(IExpr lambda) {
    IExpr shifted =
        engine.evaluate(F.Subtract(matrix, F.Times(lambda, F.IdentityMatrix(n))));
    IExpr nullSpace = engine.evaluate(F.NullSpace(shifted));
    return nullSpace.isListOfLists() ? (IAST) nullSpace : F.NIL;
  }

  /**
   * The null space of <code>m - t*IdentityMatrix(n)</code> by Gauss-Jordan elimination in the
   * field <code>Q(t)</code>. A free variable of the reduced row echelon form is set to one, the
   * last free variable gives the first basis vector.
   */
  private IAST algebraicNullSpace(Eigenvalue eigenvalue) {
    AlgebraicField field = new AlgebraicField(eigenvalue.minimalPolynomial);
    IRational[][][] a = new IRational[n][n][];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        IRational[] entry = field.constant((IRational) matrix.getPart(i + 1, j + 1));
        if (i == j) {
          entry = field.subtract(entry, field.generator());
        }
        a[i][j] = entry;
      }
    }
    int[] pivotColumns = new int[n];
    int rank = 0;
    for (int column = 0; column < n && rank < n; column++) {
      int pivot = -1;
      for (int row = rank; row < n; row++) {
        if (!field.isZero(a[row][column])) {
          pivot = row;
          break;
        }
      }
      if (pivot < 0) {
        continue;
      }
      IRational[][] temp = a[pivot];
      a[pivot] = a[rank];
      a[rank] = temp;
      IRational[] inverse = field.inverse(a[rank][column]);
      for (int j = 0; j < n; j++) {
        a[rank][j] = field.multiply(a[rank][j], inverse);
      }
      for (int row = 0; row < n; row++) {
        if (row != rank && !field.isZero(a[row][column])) {
          IRational[] factor = a[row][column];
          for (int j = 0; j < n; j++) {
            a[row][j] = field.subtract(a[row][j], field.multiply(factor, a[rank][j]));
          }
        }
      }
      pivotColumns[rank++] = column;
    }
    if (rank == n) {
      return F.NIL;
    }
    boolean[] isPivot = new boolean[n];
    for (int r = 0; r < rank; r++) {
      isPivot[pivotColumns[r]] = true;
    }
    IASTAppendable result = F.ListAlloc(n - rank);
    for (int free = n - 1; free >= 0; free--) {
      if (isPivot[free]) {
        continue;
      }
      IASTAppendable vector = F.ListAlloc(n);
      for (int j = 0; j < n; j++) {
        vector.append(F.C0);
      }
      vector.set(free + 1, F.C1);
      for (int r = 0; r < rank; r++) {
        IExpr entry = reduce(field.negate(a[r][free]), eigenvalue, field);
        if (entry.isNIL()) {
          return F.NIL;
        }
        vector.set(pivotColumns[r] + 1, entry);
      }
      result.append(vector);
    }
    return result;
  }

  /**
   * Write the element <code>q(t)</code> of <code>Q(t)</code> as a rational number, a radical or a
   * <code>Root</code> object of its minimal polynomial.
   */
  private IExpr reduce(IRational[] element, Eigenvalue eigenvalue, AlgebraicField field) {
    int degree = field.degree(element);
    if (degree <= 0) {
      return element[0];
    }
    if (element[1].isOne() && degree == 1 && element[0].isZero()) {
      return eigenvalue.value;
    }
    ISymbol t = F.Dummy("t");
    ISymbol y = F.Dummy("y");
    IExpr q = field.toPolynomial(element, t);
    IExpr p = field.toPolynomial(eigenvalue.minimalPolynomial, t);
    // the minimal polynomial of q(t) is a factor of Resultant(p(t), y - q(t), t)
    IExpr resultant = engine.evaluate(F.Resultant(p, F.Subtract(y, q), t));
    IExpr factorList = engine.evaluate(F.FactorList(resultant));
    Complex z = numeric(F.subst(q, t, eigenvalue.value));
    if (z == null || !factorList.isListOfLists()) {
      return F.NIL;
    }
    IExpr best = F.NIL;
    double bestDistance = Double.MAX_VALUE;
    for (IExpr pair : (IAST) factorList) {
      IExpr factor = pair.first();
      if (factor.isFree(y)) {
        continue;
      }
      IRational[] coefficients = coefficients(factor, y);
      if (coefficients == null) {
        return F.NIL;
      }
      IAST candidates = rootsOfFactor(factor, y, coefficients);
      if (candidates.isNIL()) {
        return F.NIL;
      }
      for (IExpr candidate : candidates) {
        Complex c = numeric(candidate);
        if (c == null) {
          return F.NIL;
        }
        double distance = c.subtract(z).norm();
        if (distance < bestDistance) {
          bestDistance = distance;
          best = candidate;
        }
      }
    }
    return best;
  }

  /**
   * The field <code>Q[t]/p(t)</code> for an irreducible monic polynomial <code>p</code> of degree
   * <code>d</code>. An element is the array of its <code>d</code> rational coefficients, the
   * constant first.
   */
  private static final class AlgebraicField {
    private final IRational[] modulus;
    private final int d;

    AlgebraicField(IRational[] monicModulus) {
      this.modulus = monicModulus;
      this.d = monicModulus.length - 1;
    }

    IRational[] zero() {
      IRational[] result = new IRational[d];
      java.util.Arrays.fill(result, F.C0);
      return result;
    }

    IRational[] constant(IRational c) {
      IRational[] result = zero();
      result[0] = c;
      return result;
    }

    IRational[] generator() {
      IRational[] result = zero();
      result[1] = F.C1;
      return result;
    }

    boolean isZero(IRational[] a) {
      for (IRational c : a) {
        if (!c.isZero()) {
          return false;
        }
      }
      return true;
    }

    /** The degree of an element as a polynomial, <code>-1</code> for zero. */
    int degree(IRational[] a) {
      for (int i = a.length - 1; i >= 0; i--) {
        if (!a[i].isZero()) {
          return i;
        }
      }
      return -1;
    }

    IRational[] negate(IRational[] a) {
      IRational[] result = new IRational[d];
      for (int i = 0; i < d; i++) {
        result[i] = a[i].negate();
      }
      return result;
    }

    IRational[] subtract(IRational[] a, IRational[] b) {
      IRational[] result = new IRational[d];
      for (int i = 0; i < d; i++) {
        result[i] = a[i].subtract(b[i]);
      }
      return result;
    }

    IRational[] multiply(IRational[] a, IRational[] b) {
      IRational[] product = new IRational[2 * d - 1];
      java.util.Arrays.fill(product, F.C0);
      for (int i = 0; i < d; i++) {
        if (a[i].isZero()) {
          continue;
        }
        for (int j = 0; j < d; j++) {
          if (!b[j].isZero()) {
            product[i + j] = product[i + j].add(a[i].multiply(b[j]));
          }
        }
      }
      // t^d == -(p_0 + p_1*t + ... + p_(d-1)*t^(d-1))
      for (int k = product.length - 1; k >= d; k--) {
        IRational c = product[k];
        if (c.isZero()) {
          continue;
        }
        for (int i = 0; i < d; i++) {
          product[k - d + i] = product[k - d + i].subtract(c.multiply(modulus[i]));
        }
        product[k] = F.C0;
      }
      return java.util.Arrays.copyOf(product, d);
    }

    /** The inverse of a non-zero element by the extended Euclidean algorithm. */
    IRational[] inverse(IRational[] a) {
      // polynomials as arrays of arbitrary length, the constant first
      IRational[] r0 = modulus.clone();
      IRational[] r1 = trim(a.clone());
      IRational[] s0 = new IRational[] {F.C0};
      IRational[] s1 = new IRational[] {F.C1};
      while (!(r1.length == 1 && !r1[0].isZero()) && r1.length > 0) {
        IRational[][] qr = divide(r0, r1);
        IRational[] s2 = polySubtract(s0, polyMultiply(qr[0], s1));
        r0 = r1;
        r1 = qr[1];
        s0 = s1;
        s1 = s2;
        if (r1.length == 0) {
          throw new ArithmeticException("not invertible");
        }
      }
      IRational inverseConstant = r1[0].inverse();
      IRational[] result = zero();
      // s1 * a == r1 (mod p)
      for (int i = 0; i < s1.length && i < d; i++) {
        result[i] = s1[i].multiply(inverseConstant);
      }
      if (s1.length > d) {
        // reduce the remaining terms modulo p
        IRational[] reduced = divide(polyMultiplyConstant(s1, inverseConstant), modulus)[1];
        result = zero();
        for (int i = 0; i < reduced.length; i++) {
          result[i] = reduced[i];
        }
      }
      return result;
    }

    private static IRational[] trim(IRational[] a) {
      int length = a.length;
      while (length > 0 && a[length - 1].isZero()) {
        length--;
      }
      return java.util.Arrays.copyOf(a, length);
    }

    private static IRational[] polyMultiplyConstant(IRational[] a, IRational c) {
      IRational[] result = new IRational[a.length];
      for (int i = 0; i < a.length; i++) {
        result[i] = a[i].multiply(c);
      }
      return result;
    }

    private static IRational[] polySubtract(IRational[] a, IRational[] b) {
      IRational[] result = new IRational[Math.max(a.length, b.length)];
      for (int i = 0; i < result.length; i++) {
        IRational x = i < a.length ? a[i] : F.C0;
        IRational y = i < b.length ? b[i] : F.C0;
        result[i] = x.subtract(y);
      }
      return trim(result);
    }

    private static IRational[] polyMultiply(IRational[] a, IRational[] b) {
      if (a.length == 0 || b.length == 0) {
        return new IRational[0];
      }
      IRational[] result = new IRational[a.length + b.length - 1];
      java.util.Arrays.fill(result, F.C0);
      for (int i = 0; i < a.length; i++) {
        for (int j = 0; j < b.length; j++) {
          result[i + j] = result[i + j].add(a[i].multiply(b[j]));
        }
      }
      return trim(result);
    }

    /** <code>{quotient, remainder}</code> of the division of <code>a</code> by <code>b</code>. */
    private static IRational[][] divide(IRational[] a, IRational[] b) {
      IRational[] remainder = trim(a.clone());
      b = trim(b);
      int quotientLength = Math.max(0, remainder.length - b.length + 1);
      IRational[] quotient = new IRational[quotientLength];
      java.util.Arrays.fill(quotient, F.C0);
      IRational lead = b[b.length - 1];
      while (remainder.length >= b.length) {
        int shift = remainder.length - b.length;
        IRational c = remainder[remainder.length - 1].divideBy(lead);
        quotient[shift] = c;
        for (int i = 0; i < b.length; i++) {
          remainder[shift + i] = remainder[shift + i].subtract(c.multiply(b[i]));
        }
        remainder = trim(java.util.Arrays.copyOf(remainder, remainder.length - 1));
      }
      return new IRational[][] {trim(quotient), remainder};
    }

    /** The element as a polynomial expression in <code>t</code>. */
    IExpr toPolynomial(IRational[] a, ISymbol t) {
      IASTAppendable sum = F.PlusAlloc(a.length);
      for (int i = 0; i < a.length; i++) {
        if (!a[i].isZero()) {
          sum.append(F.Times(a[i], F.Power(t, F.ZZ(i))));
        }
      }
      return EvalEngine.get().evaluate(sum.oneIdentity0());
    }
  }
}
