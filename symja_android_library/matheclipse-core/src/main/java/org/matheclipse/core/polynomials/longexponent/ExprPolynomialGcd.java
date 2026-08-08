package org.matheclipse.core.polynomials.longexponent;

import java.util.Map;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTAppendable;

/**
 * Greatest common divisor of multivariate {@link ExprPolynomial}s by a recursive primitive
 * polynomial remainder sequence.
 *
 * <p>
 * {@link ExprPolynomial#gcd(ExprPolynomial)} is the Euclidean algorithm, which needs the leading
 * coefficient of the divisor to be invertible and therefore only works in one variable. For more
 * than one variable this class splits off the last variable of {@link ExprPolynomialRing#vars} and
 * views both arguments as univariate polynomials in it, whose coefficients are polynomials in the
 * remaining variables:
 *
 * <pre>
 * gcd(a, b) == gcd(content(a), content(b)) * primitivePRS(primitivePart(a), primitivePart(b))
 * </pre>
 *
 * The contents are greatest common divisors in one variable less and are computed by recursion. The
 * primitive parts are reduced by pseudo remainders, which multiply by the leading coefficient
 * instead of dividing by it, so that no invertible leading coefficient is required. Taking the
 * primitive part of every pseudo remainder keeps the coefficients from growing over the sequence.
 *
 * <p>
 * The coefficient domain is a field here - {@link org.matheclipse.core.interfaces.IExpr#isUnit()} is
 * true for every expression - so the result is normalized to a leading coefficient of <code>1</code>
 * exactly as the univariate {@link ExprPolynomial#gcd(ExprPolynomial)} does. A common numeric or
 * symbolic factor of all coefficients, <code>Sqrt(2)</code> for example, is therefore not part of
 * the result.
 */
class ExprPolynomialGcd {

  private ExprPolynomialGcd() {}

  /**
   * Greatest common divisor of two polynomials in more than one variable.
   *
   * @param A polynomial
   * @param S polynomial in the same ring as <code>A</code>
   * @return <code>gcd(A, S)</code> with a leading coefficient of <code>1</code>
   * @throws ArithmeticException if an intermediate division is not exact, i.e. if the structural
   *         arithmetic of the coefficient domain cannot cancel the coefficients again
   */
  public static ExprPolynomial gcd(ExprPolynomial A, ExprPolynomial S) {
    return recursiveGcd(A, S).monic();
  }

  private static ExprPolynomial recursiveGcd(ExprPolynomial A, ExprPolynomial S) {
    if (A == null || A.isZERO()) {
      return S;
    }
    if (S == null || S.isZERO()) {
      return A;
    }
    ExprPolynomialRing ring = A.ring;
    if (ring.nvar != S.ring.nvar) {
      throw new IllegalArgumentException(
          "different number of variables " + ring.nvar + ", " + S.ring.nvar);
    }
    if (ring.nvar <= 1) {
      // the Euclidean algorithm needs an invertible leading coefficient, which is available in one
      // variable because the coefficient domain is a field
      return A.gcd(S);
    }
    ExprPolynomialRing subRing = subRing(ring);
    ExprPolynomial[] a = coefficients(A, subRing);
    ExprPolynomial[] b = coefficients(S, subRing);
    ExprPolynomial contentA = content(a, subRing);
    ExprPolynomial contentB = content(b, subRing);
    ExprPolynomial[] gcd = primitiveRemainderSequence(primitivePart(a, contentA),
        primitivePart(b, contentB), subRing);
    return distribute(multiply(gcd, recursiveGcd(contentA, contentB)), ring);
  }

  /**
   * The polynomial ring without its last variable.
   *
   * <p>
   * The last variable of {@link ExprPolynomialRing#vars} is the one at index <code>0</code> of every
   * {@link ExpVectorLong}, which is the variable {@link ExprPolynomial#contract(ExprPolynomialRing)}
   * splits off.
   */
  private static ExprPolynomialRing subRing(ExprPolynomialRing ring) {
    IASTAppendable variables = F.ListAlloc(ring.nvar - 1);
    for (int i = 1; i < ring.nvar; i++) {
      variables.append(ring.vars.get(i));
    }
    return new ExprPolynomialRing(ring.coFac, variables, ring.nvar - 1, subTermOrder(ring.tord),
        ring.numericFunction);
  }

  /**
   * A term order for one variable less. Term orders with a split index or with exponent weights are
   * defined for the number of variables of the ring they belong to, so only the plain order
   * indicator is carried over - which order is used does not change the result, every admissible one
   * gives the same greatest common divisor.
   */
  private static ExprTermOrder subTermOrder(ExprTermOrder termOrder) {
    int evord = termOrder.getEvord();
    if (evord < ExprTermOrder.MIN_EVORD || evord > ExprTermOrder.MAX_EVORD) {
      // a weighted term order reports no order indicator at all
      return ExprTermOrderByName.Lexicographic;
    }
    return new ExprTermOrder(evord);
  }

  /**
   * View <code>A</code> as a univariate polynomial in the last variable of its ring.
   *
   * @return the coefficients over <code>subRing</code>, indexed by their degree in the last
   *         variable. The entry of the highest index is not zero.
   */
  private static ExprPolynomial[] coefficients(ExprPolynomial A, ExprPolynomialRing subRing) {
    Map<ExpVectorLong, ExprPolynomial> contracted = A.contract(subRing);
    long degree = 0L;
    for (ExpVectorLong exponent : contracted.keySet()) {
      long e = exponent.getVal(0);
      if (e > degree) {
        degree = e;
      }
    }
    if (degree >= Integer.MAX_VALUE) {
      throw new ArithmeticException("degree too large: " + degree);
    }
    ExprPolynomial[] result = new ExprPolynomial[(int) degree + 1];
    ExprPolynomial zero = subRing.getZero();
    for (int i = 0; i < result.length; i++) {
      result[i] = zero;
    }
    for (Map.Entry<ExpVectorLong, ExprPolynomial> entry : contracted.entrySet()) {
      result[(int) entry.getKey().getVal(0)] = entry.getValue();
    }
    return result;
  }

  /** The inverse of {@link #coefficients(ExprPolynomial, ExprPolynomialRing)}. */
  private static ExprPolynomial distribute(ExprPolynomial[] a, ExprPolynomialRing ring) {
    ExprPolynomial result = ring.getZero().copy();
    for (int i = 0; i < a.length; i++) {
      if (a[i].isZERO()) {
        continue;
      }
      for (ExprMonomial monomial : a[i]) {
        long[] exponents = new long[ring.nvar];
        exponents[0] = i;
        long[] rest = monomial.exponent().getVal();
        System.arraycopy(rest, 0, exponents, 1, rest.length);
        result.doAddTo(monomial.coefficient(), new ExpVectorLong(exponents));
      }
    }
    return result;
  }

  /** The index of the highest non zero coefficient, <code>-1</code> for the zero polynomial. */
  private static int degree(ExprPolynomial[] a) {
    for (int i = a.length - 1; i >= 0; i--) {
      if (!a[i].isZERO()) {
        return i;
      }
    }
    return -1;
  }

  /** The greatest common divisor of all coefficients, zero if all of them are zero. */
  private static ExprPolynomial content(ExprPolynomial[] a, ExprPolynomialRing subRing) {
    ExprPolynomial result = subRing.getZero();
    for (int i = a.length - 1; i >= 0; i--) {
      if (a[i].isZERO()) {
        continue;
      }
      result = recursiveGcd(result, a[i]);
      if (result.isONE()) {
        break;
      }
    }
    return result;
  }

  private static ExprPolynomial[] primitivePart(ExprPolynomial[] a, ExprPolynomial content) {
    if (content.isZERO() || content.isONE()) {
      return a;
    }
    ExprPolynomial[] result = new ExprPolynomial[a.length];
    for (int i = 0; i < a.length; i++) {
      result[i] = divideExact(a[i], content);
    }
    return result;
  }

  private static ExprPolynomial[] multiply(ExprPolynomial[] a, ExprPolynomial factor) {
    if (factor.isONE()) {
      return a;
    }
    ExprPolynomial[] result = new ExprPolynomial[a.length];
    for (int i = 0; i < a.length; i++) {
      result[i] = a[i].multiply(factor);
    }
    return result;
  }

  private static ExprPolynomial divideExact(ExprPolynomial a, ExprPolynomial b) {
    if (a.isZERO()) {
      return a;
    }
    ExprPolynomial[] quotientRemainder = a.quotientRemainder(b);
    if (quotientRemainder == null || !quotientRemainder[1].isZERO()) {
      throw new ArithmeticException("no exact division: (" + a + ") / (" + b + ")");
    }
    return quotientRemainder[0];
  }

  /**
   * The greatest common divisor of two primitive polynomials by a primitive polynomial remainder
   * sequence.
   */
  private static ExprPolynomial[] primitiveRemainderSequence(ExprPolynomial[] a, ExprPolynomial[] b,
      ExprPolynomialRing subRing) {
    if (degree(a) < degree(b)) {
      ExprPolynomial[] swap = a;
      a = b;
      b = swap;
    }
    while (true) {
      int degreeB = degree(b);
      if (degreeB < 0) {
        return a;
      }
      if (degreeB == 0) {
        // b is a non zero and primitive element of the coefficient ring, hence a unit
        return new ExprPolynomial[] {subRing.getOne()};
      }
      ExprPolynomial[] remainder = pseudoRemainder(a, b, subRing);
      a = b;
      b = primitivePart(remainder, content(remainder, subRing));
    }
  }

  /**
   * The pseudo remainder of <code>a</code> and <code>b</code>: every reduction step multiplies by
   * the leading coefficient of <code>b</code> rather than dividing by it, so the coefficient ring
   * does not have to be a field.
   */
  private static ExprPolynomial[] pseudoRemainder(ExprPolynomial[] a, ExprPolynomial[] b,
      ExprPolynomialRing subRing) {
    int degreeB = degree(b);
    ExprPolynomial leadingB = b[degreeB];
    ExprPolynomial[] r = a.clone();
    for (int degreeR = degree(r); degreeR >= degreeB; degreeR = degree(r)) {
      ExprPolynomial leadingR = r[degreeR];
      int shift = degreeR - degreeB;
      for (int i = 0; i <= degreeR; i++) {
        r[i] = r[i].multiply(leadingB);
      }
      for (int i = 0; i < degreeB; i++) {
        r[i + shift] = r[i + shift].subtract(leadingR.multiply(b[i]));
      }
      // the leading terms cancel by construction - assigning zero rather than subtracting it keeps
      // the degree strictly decreasing even if the coefficient arithmetic does not cancel
      r[degreeR] = subRing.getZero();
    }
    return r;
  }
}
