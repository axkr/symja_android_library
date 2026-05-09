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


/**
 * <p>
 * Greatest common divisor algorithms with primitive polynomial remainder sequence, which uses the
 * extended GCD for {@link BigRational} number as described here:
 * <ul>
 * <li><a href =
 * "https://math.stackexchange.com/questions/151081/gcd-of-rationals/151431#151431">math.stackexchange.com/questions/151081</a>
 * <ul>
 */

public class GreatestCommonDivisorRational extends GreatestCommonDivisorAbstract<BigRational> {

  private static final Logger logger = LogManager.getLogger(GreatestCommonDivisorRational.class);

  private static final boolean debug = logger.isDebugEnabled();

  public static BigRational gcdRational(BigRational a, BigRational b) {
    if (b.isZERO()) {
      return a.abs();
    }
    if (a.isZERO()) {
      return b.abs();
    }
    BigInteger p = a.num.gcd(b.num);
    BigInteger q = BigRational.lcm(a.den, b.den);
    return BigRational.RNRED(p, q);
  }

  public static BigRational lcmRational(BigRational a, BigRational b) {
    if (b.isZERO()) {
      return BigRational.ZERO;
    }
    if (a.isZERO()) {
      return BigRational.ZERO;
    }
    return BigRational.RNRED(BigRational.lcm(a.num, b.num), a.den.gcd(b.den));
  }


  @Override
  public BigRational gcd(BigRational a, BigRational b) {
    return gcdRational(a, b);
  }

  /**
   * Univariate GenPolynomial greatest common divisor. Uses pseudoRemainder for remainder.
   * 
   * @param P univariate GenPolynomial.
   * @param S univariate GenPolynomial.
   * @return gcd(P,S).
   */
  @Override
  public GenPolynomial<BigRational> baseGcd(GenPolynomial<BigRational> P,
      GenPolynomial<BigRational> S) {
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
    GenPolynomial<BigRational> q;
    GenPolynomial<BigRational> r;
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
    BigRational a = baseContent(r);
    BigRational b = baseContent(q);
    BigRational c = gcd(a, b); // indirection
    r = divide(r, a); // indirection
    q = divide(q, b); // indirection
    if (r.isONE()) {
      return r.multiply(c);
    }
    if (q.isONE()) {
      return q.multiply(c);
    }
    GenPolynomial<BigRational> x;
    while (!r.isZERO()) {
      x = PolyUtil.<BigRational>baseSparsePseudoRemainder(q, r);
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
  public GenPolynomial<GenPolynomial<BigRational>> recursiveUnivariateGcd(
      GenPolynomial<GenPolynomial<BigRational>> P, GenPolynomial<GenPolynomial<BigRational>> S) {
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
    GenPolynomial<GenPolynomial<BigRational>> q;
    GenPolynomial<GenPolynomial<BigRational>> r;
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
    GenPolynomial<BigRational> a = recursiveContent(r);
    GenPolynomial<BigRational> b = recursiveContent(q);

    GenPolynomial<BigRational> c = gcd(a, b); // go to recursion
    // System.out.println("rgcd c = " + c);
    r = PolyUtil.<BigRational>recursiveDivide(r, a);
    q = PolyUtil.<BigRational>recursiveDivide(q, b);
    if (r.isONE()) {
      return r.multiply(c);
    }
    if (q.isONE()) {
      return q.multiply(c);
    }
    GenPolynomial<GenPolynomial<BigRational>> x;
    while (!r.isZERO()) {
      x = PolyUtil.<BigRational>recursiveSparsePseudoRemainder(q, r);
      if (logger.isDebugEnabled()) {
        logger.info("recursiveSparsePseudoRemainder.bits = {}", x.bitLength());
      }
      q = r;
      r = recursivePrimitivePart(x);
    }
    return q.abs().multiply(c); // .abs();
  }

}
