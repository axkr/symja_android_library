/*
 * $Id$
 */

package edu.jas.ufd;


import java.math.BigInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;


/**
 * <p>
 * Greatest common divisor algorithms with primitive polynomial remainder sequence, which uses the
 * extended GCD for {@link BigRational} number as described here:
 * <ul>
 * <li><a href =
 * "https://math.stackexchange.com/questions/151081/gcd-of-rationals/151431#151431">math.stackexchange.com/questions/151081</a>
 * <ul>
 */

public class GreatestCommonDivisorRational<C extends GcdRingElem<C>>
    extends GreatestCommonDivisorAbstract<C> {

  private static final Logger logger = LogManager.getLogger(GreatestCommonDivisorRational.class);

  private static final boolean debug = logger.isDebugEnabled();

  /**
   * {@link java.math.BigInteger} number least common multiple.
   * 
   * @param i0
   * @param i1
   * @return
   */
  private static BigInteger lcm(final BigInteger i0, final BigInteger i1) {
    if (i0.signum() == 0 && i1.signum() == 0) {
      return BigInteger.ZERO;
    }
    BigInteger a = i0.abs();
    BigInteger b = i1.abs();
    BigInteger gcd = i0.gcd(b);
    BigInteger lcm = (a.multiply(b)).divide(gcd);
    return lcm;
  }

  public static BigRational gcdRational(BigRational a, BigRational b) {
    if (b.isZERO()) {
      return a;
    }
    if (a.isZERO()) {
      return b;
    }
    BigInteger p = a.num.gcd(b.num);
    BigInteger q = lcm(a.den, b.den);
    return BigRational.RNRED(p, q);
  }

  public BigRational lcmRational(BigRational a, BigRational b) {
    if (b.isZERO()) {
      return BigRational.ZERO;
    }
    if (a.isZERO()) {
      return BigRational.ZERO;
    }
    return BigRational.RNRED(lcm(a.num, b.num), a.den.gcd(b.den));
  }


  @Override
  public C gcd(C a, C b) {
    return (C) gcdRational((BigRational) a, (BigRational) b);
  }

  /**
   * Univariate GenPolynomial greatest common divisor. Uses pseudoRemainder for remainder.
   * 
   * @param P univariate GenPolynomial.
   * @param S univariate GenPolynomial.
   * @return gcd(P,S).
   */
  @Override
  public GenPolynomial<C> baseGcd(GenPolynomial<C> P, GenPolynomial<C> S) {
    if (S == null || S.isZERO()) {
      return P;
    }
    if (P == null || P.isZERO()) {
      return S;
    }
    if (P.ring.nvar > 1) {
      throw new IllegalArgumentException(this.getClass().getName() + " no univariate polynomial");
    }
    long e = P.degree(0);
    long f = S.degree(0);
    GenPolynomial<C> q;
    GenPolynomial<C> r;
    if (f > e) {
      r = P;
      q = S;
      long g = f;
      f = e;
      e = g;
    } else {
      q = P;
      r = S;
    }
    if (debug) {
      logger.debug("degrees: e = {}, f = {}", e, f);
    }
    r = r.abs();
    q = q.abs();
    C a = baseContent(r);
    C b = baseContent(q);
    C c = gcd(a, b); // indirection
    r = divide(r, a); // indirection
    q = divide(q, b); // indirection
    if (r.isONE()) {
      return r.multiply(c);
    }
    if (q.isONE()) {
      return q.multiply(c);
    }
    GenPolynomial<C> x;
    while (!r.isZERO()) {
      x = PolyUtil.<C>baseSparsePseudoRemainder(q, r);
      q = r;
      r = basePrimitivePart(x);
    }
    return (q.multiply(c)).abs();
  }


  /**
   * Univariate GenPolynomial recursive greatest common divisor. Uses pseudoRemainder for remainder.
   * 
   * @param P univariate recursive GenPolynomial.
   * @param S univariate recursive GenPolynomial.
   * @return gcd(P,S).
   */
  @Override
  public GenPolynomial<GenPolynomial<C>> recursiveUnivariateGcd(GenPolynomial<GenPolynomial<C>> P,
      GenPolynomial<GenPolynomial<C>> S) {
    if (S == null || S.isZERO()) {
      return P;
    }
    if (P == null || P.isZERO()) {
      return S;
    }
    if (P.ring.nvar > 1) {
      throw new IllegalArgumentException(this.getClass().getName() + " no univariate polynomial");
    }
    long e = P.degree(0);
    long f = S.degree(0);
    GenPolynomial<GenPolynomial<C>> q;
    GenPolynomial<GenPolynomial<C>> r;
    if (f > e) {
      r = P;
      q = S;
      long g = f;
      f = e;
      e = g;
    } else {
      q = P;
      r = S;
    }
    if (debug) {
      logger.debug("degrees: e = {}, f = {}", e, f);
    }
    r = r.abs();
    q = q.abs();
    GenPolynomial<C> a = recursiveContent(r);
    GenPolynomial<C> b = recursiveContent(q);

    GenPolynomial<C> c = gcd(a, b); // go to recursion
    // System.out.println("rgcd c = " + c);
    r = PolyUtil.<C>recursiveDivide(r, a);
    q = PolyUtil.<C>recursiveDivide(q, b);
    if (r.isONE()) {
      return r.multiply(c);
    }
    if (q.isONE()) {
      return q.multiply(c);
    }
    GenPolynomial<GenPolynomial<C>> x;
    while (!r.isZERO()) {
      x = PolyUtil.<C>recursiveSparsePseudoRemainder(q, r);
      if (logger.isDebugEnabled()) {
        logger.info("recursiveSparsePseudoRemainder.bits = {}", x.bitLength());
      }
      q = r;
      r = recursivePrimitivePart(x);
    }
    return q.abs().multiply(c); // .abs();
  }

}
