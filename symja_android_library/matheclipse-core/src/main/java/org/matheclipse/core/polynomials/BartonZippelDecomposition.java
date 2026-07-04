package org.matheclipse.core.polynomials;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.RingElem;

public class BartonZippelDecomposition {

  /**
   * Attempts to find a functional decomposition of a univariate polynomial f(x) such that f(x) =
   * g(h(x)).
   *
   * @param f The univariate polynomial to decompose.
   * @return A list [g, h] if a non-trivial decomposition is found, otherwise null.
   */
  public static <C extends RingElem<C>> List<GenPolynomial<C>> decompose(GenPolynomial<C> f) {
    long n = f.degree(0);

    // Test possible degrees for the inner polynomial h(x)
    for (int s = 2; s <= n / 2; s++) {
      if (n % s == 0) {
        List<GenPolynomial<C>> decomp = decomposeDegreeS(f, s);
        if (decomp != null) {
          return decomp;
        }
      }
    }
    return null; // No decomposition found
  }

  private static <C extends RingElem<C>> List<GenPolynomial<C>> decomposeDegreeS(GenPolynomial<C> f,
      int s) {
    GenPolynomialRing<C> ring = f.ring;
    if (ring.nvar != 1) {
      throw new IllegalArgumentException("Only univariate polynomials are supported.");
    }

    long n = f.degree(0);
    int r = (int) (n / s);

    C leadingCoeff = f.leadingBaseCoefficient();
    C leadingCoeffInv = leadingCoeff.inverse();

    // Work with a monic f to simplify finding h(x): fMonic = f / leadingCoeff
    GenPolynomial<C> fMonic = f.multiply(leadingCoeffInv);

    // h(x) starts as exactly x^s
    GenPolynomial<C> h = ring.univariate(0, s);

    C rElem = ring.coFac.fromInteger(r);
    C rInv = rElem.inverse();

    // Iteratively compute coefficients of h(x) from degree s-1 down to 1
    for (int k = 1; k < s; k++) {
      // Compute discrepancy: E = fMonic - h^r
      GenPolynomial<C> hPowR = power(h, r);
      GenPolynomial<C> E = fMonic.subtract(hPowR);

      long targetDeg = n - k;
      C eCoeff = getCoefficient(E, targetDeg);

      if (!eCoeff.isZERO()) {
        // Determine next coefficient: c_{s-k} = eCoeff / r
        C c_sk = eCoeff.multiply(rInv);
        GenPolynomial<C> term = ring.getONE().multiply(c_sk).multiply(ring.univariate(0, s - k));
        h = h.sum(term);
      }
    }

    // Candidate h(x) found. Determine g(x) by converting f(x) to base h(x).
    List<C> gCoeffs = new ArrayList<>();
    GenPolynomial<C> currentF = f;

    while (!currentF.isZERO()) {
      GenPolynomial<C>[] qr = currentF.quotientRemainder(h);
      GenPolynomial<C> q = qr[0];
      GenPolynomial<C> rem = qr[1];

      if (rem.degree(0) > 0) {
        // If the remainder is not a constant, this candidate h(x) is invalid.
        return null;
      }

      gCoeffs.add(rem.isZERO() ? ring.coFac.getZERO() : rem.leadingBaseCoefficient());
      currentF = q;
    }

    // Construct outer polynomial g(x) using the collected remainders as coefficients
    GenPolynomial<C> g = ring.getZERO();
    for (int i = 0; i < gCoeffs.size(); i++) {
      C coeff = gCoeffs.get(i);
      if (!coeff.isZERO()) {
        g = g.sum(ring.getONE().multiply(coeff).multiply(ring.univariate(0, i)));
      }
    }

    List<GenPolynomial<C>> result = new ArrayList<>();
    result.add(g);
    result.add(h);
    return result;
  }

  private static <C extends RingElem<C>> GenPolynomial<C> power(GenPolynomial<C> poly,
      int exponent) {
    GenPolynomial<C> result = poly.ring.getONE();
    GenPolynomial<C> base = poly;
    int e = exponent;
    while (e > 0) {
      if ((e % 2) != 0) {
        result = result.multiply(base);
      }
      base = base.multiply(base);
      e /= 2;
    }
    return result;
  }

  private static <C extends RingElem<C>> C getCoefficient(GenPolynomial<C> poly, long degree) {
    for (Map.Entry<ExpVector, C> term : poly.getMap().entrySet()) {
      // Index 0 fetches the exponent of the single variable in a univariate polynomial
      if (term.getKey().getVal(0) == degree) {
        return term.getValue();
      }
    }
    return poly.ring.coFac.getZERO();
  }
}