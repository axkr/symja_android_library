/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Greatest common divisor algorithms with subresultant polynomial remainder
 * sequence.
 * @author Heinz Kredel
 * @author Youssef Elbarbary
 */

public class GreatestCommonDivisorSubres<C extends GcdRingElem<C>> extends GreatestCommonDivisorAbstract<C> {


    private static final Logger logger = LogManager.getLogger(GreatestCommonDivisorSubres.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * GenPolynomial pseudo remainder. For univariate polynomials.
     * @param P GenPolynomial.
     * @param S nonzero GenPolynomial.
     * @return remainder with ldcf(S)<sup>m</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     * @deprecated(forRemoval=true) Use
     *                              {@link edu.jas.poly.PolyUtil#baseDensePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)}
     *                              instead
     */
    @Deprecated
    public GenPolynomial<C> basePseudoRemainder(GenPolynomial<C> P, GenPolynomial<C> S) {
        return PolyUtil.<C> baseDensePseudoRemainder(P, S);
    }


    /**
     * GenPolynomial pseudo remainder. For recursive polynomials.
     * @param P recursive GenPolynomial.
     * @param S nonzero recursive GenPolynomial.
     * @return remainder with ldcf(S)<sup>m</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     * @deprecated(forRemoval=true) Use
     *                              {@link edu.jas.poly.PolyUtil#recursiveDensePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)}
     *                              instead
     */
    @Deprecated
    public GenPolynomial<GenPolynomial<C>> recursivePseudoRemainder(GenPolynomial<GenPolynomial<C>> P,
                    GenPolynomial<GenPolynomial<C>> S) {
        return PolyUtil.<C> recursiveDensePseudoRemainder(P, S);
    }


    /**
     * Univariate GenPolynomial greatest common divisor. Uses pseudoRemainder for
     * remainder.
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
        C g = r.ring.getONECoefficient();
        C h = r.ring.getONECoefficient();
        GenPolynomial<C> x;
        C z;
        while (!r.isZERO()) {
            long delta = q.degree(0) - r.degree(0);
            //System.out.println("delta    = " + delta);
            x = PolyUtil.<C> baseDensePseudoRemainder(q, r);
            q = r;
            if (!x.isZERO()) {
                z = g.multiply(h.power(delta)); //power(P.ring.coFac, h, delta));
                //System.out.println("z  = " + z);
                r = x.divide(z);
                g = q.leadingBaseCoefficient();
                z = g.power(delta); //power(P.ring.coFac, g, delta);
                h = z.divide(h.power(delta - 1)); //power(P.ring.coFac, h, delta - 1));
                //System.out.println("g  = " + g);
                //System.out.println("h  = " + h);
            } else {
                r = x;
            }
        }
        q = basePrimitivePart(q);
        return (q.multiply(c)).abs();
    }


    /**
     * Univariate GenPolynomial recursive greatest common divisor. Uses
     * pseudoRemainder for remainder.
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
        //System.out.println("rgcd c = " + c);
        r = PolyUtil.<C> recursiveDivide(r, a);
        q = PolyUtil.<C> recursiveDivide(q, b);
        if (r.isONE()) {
            return r.multiply(c);
        }
        if (q.isONE()) {
            return q.multiply(c);
        }
        GenPolynomial<C> g = r.ring.getONECoefficient();
        GenPolynomial<C> h = r.ring.getONECoefficient();
        GenPolynomial<GenPolynomial<C>> x;
        GenPolynomial<C> z = null;
        while (!r.isZERO()) {
            long delta = q.degree(0) - r.degree(0);
            //System.out.println("rgcd delta = " + delta);
            x = PolyUtil.<C> recursiveDensePseudoRemainder(q, r);
            if (logger.isDebugEnabled()) {
                logger.info("recursiveDensePseudoRemainder.bits = {}", x.bitLength());
            }
            q = r;
            if (!x.isZERO()) {
                z = g.multiply(h.power(delta)); //power(P.ring.coFac, h, delta));
                r = PolyUtil.<C> recursiveDivide(x, z);
                g = q.leadingBaseCoefficient();
                z = g.power(delta); //power(P.ring.coFac, g, delta);
                h = PolyUtil.<C> basePseudoDivide(z, h.power(delta - 1)); // power(P.ring.coFac, h, delta - 1)
            } else {
                r = x;
            }
        }
        q = recursivePrimitivePart(q);
        return q.abs().multiply(c); //.abs();
    }


    /**
     * Univariate GenPolynomial resultant. Uses pseudoRemainder for remainder.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return res(P,S).
     */
    @Override
    public GenPolynomial<C> baseResultant(GenPolynomial<C> P, GenPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return S;
        }
        if (P == null || P.isZERO()) {
            return P;
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
        //r = r.abs();
        //q = q.abs();
        C a = baseContent(r);
        C b = baseContent(q);
        r = divide(r, a); // indirection
        q = divide(q, b); // indirection
        RingFactory<C> cofac = P.ring.coFac;
        C g = cofac.getONE();
        C h = cofac.getONE();
        C t = a.power(e); //power(cofac, a, e);
        t = t.multiply(b.power(f)); //power(cofac, b, f));
        long s = 1;
        GenPolynomial<C> x;
        C z;
        while (r.degree(0) > 0) {
            long delta = q.degree(0) - r.degree(0);
            //System.out.println("delta    = " + delta);
            if ((q.degree(0) % 2 != 0) && (r.degree(0) % 2 != 0)) {
                s = -s;
            }
            x = PolyUtil.<C> baseDensePseudoRemainder(q, r);
            //System.out.println("x  = " + x);
            q = r;
            if (x.degree(0) > 0) {
                z = g.multiply(h.power(delta)); //power(cofac, h, delta));
                //System.out.println("z  = " + z);
                r = x.divide(z);
                g = q.leadingBaseCoefficient();
                z = g.power(delta); //power(cofac, g, delta);
                h = z.divide(h.power(delta - 1));
            } else {
                r = x;
            }
        }
        z = r.leadingBaseCoefficient().power(q.degree(0)); //power(cofac, r.leadingBaseCoefficient(), q.degree(0));
        h = z.divide(h.power(q.degree() - 1)); //power(cofac, h, q.degree(0) - 1));
        z = h.multiply(t);
        if (s < 0) {
            z = z.negate();
        }
        x = P.ring.getONE().multiply(z);
        return x;
    }


    /**
     * Univariate GenPolynomial recursive resultant. Uses pseudoRemainder for
     * remainder.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return res(P,S).
     */
    @Override
    public GenPolynomial<GenPolynomial<C>> recursiveUnivariateResultant(GenPolynomial<GenPolynomial<C>> P,
                    GenPolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            return S;
        }
        if (P == null || P.isZERO()) {
            return P;
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
        r = r.abs();
        q = q.abs();
        GenPolynomial<C> a = recursiveContent(r);
        GenPolynomial<C> b = recursiveContent(q);
        r = PolyUtil.<C> recursiveDivide(r, a);
        q = PolyUtil.<C> recursiveDivide(q, b);
        RingFactory<GenPolynomial<C>> cofac = P.ring.coFac;
        GenPolynomial<C> g = cofac.getONE();
        GenPolynomial<C> h = cofac.getONE();
        GenPolynomial<GenPolynomial<C>> x;
        GenPolynomial<C> t;
        if (f == 0 && e == 0 && g.ring.nvar > 0) {
            // if coeffs are multivariate (and non constant)
            // otherwise it would be 1
            t = resultant(a, b);
            x = P.ring.getONE().multiply(t);
            return x;
        }
        t = a.power(e); //power(cofac, a, e);
        t = t.multiply(b.power(f)); //power(cofac, b, f));
        long s = 1;
        GenPolynomial<C> z;
        while (r.degree(0) > 0) {
            long delta = q.degree(0) - r.degree(0);
            //System.out.println("delta    = " + delta);
            if ((q.degree(0) % 2 != 0) && (r.degree(0) % 2 != 0)) {
                s = -s;
            }
            x = PolyUtil.<C> recursiveDensePseudoRemainder(q, r);
            //System.out.println("x  = " + x);
            q = r;
            if (x.degree(0) > 0) {
                z = g.multiply(h.power(delta)); //power(P.ring.coFac, h, delta));
                r = PolyUtil.<C> recursiveDivide(x, z);
                g = q.leadingBaseCoefficient();
                z = g.power(delta); //power(cofac, g, delta);
                h = PolyUtil.<C> basePseudoDivide(z, h.power(delta - 1)); //power(cofac, h, delta - 1));
            } else {
                r = x;
            }
        }
        z = r.leadingBaseCoefficient().power(q.degree(0)); //power(cofac, r.leadingBaseCoefficient(), q.degree(0));
        h = PolyUtil.<C> basePseudoDivide(z, h.power(q.degree() - 1)); //power(cofac, h, q.degree(0) - 1));
        z = h.multiply(t);
        if (s < 0) {
            z = z.negate();
        }
        x = P.ring.getONE().multiply(z);
        return x;
    }


    /**
     * Univariate GenPolynomial recursive Subresultant list. Uses
     * pseudoRemainder for remainder. <b>Author:</b> Youssef Elbarbary
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return subResList(P,S).
     */
    public List<GenPolynomial<GenPolynomial<C>>> recursiveUnivariateSubResultantList(
                    GenPolynomial<GenPolynomial<C>> P, GenPolynomial<GenPolynomial<C>> S) {
        List<GenPolynomial<GenPolynomial<C>>> myList = new ArrayList<GenPolynomial<GenPolynomial<C>>>();
        if (S == null || S.isZERO()) {
            myList.add(S);
            return myList;
        }
        if (P == null || P.isZERO()) {
            myList.add(P);
            return myList;
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
        r = r.abs();
        q = q.abs();
        GenPolynomial<C> a = recursiveContent(r);
        GenPolynomial<C> b = recursiveContent(q);
        r = PolyUtil.<C> recursiveDivide(r, a);
        q = PolyUtil.<C> recursiveDivide(q, b);
        RingFactory<GenPolynomial<C>> cofac = P.ring.coFac;
        GenPolynomial<C> g = cofac.getONE();
        GenPolynomial<C> h = cofac.getONE();
        GenPolynomial<GenPolynomial<C>> x;
        GenPolynomial<C> t;
        if (f == 0 && e == 0 && g.ring.nvar > 0) {
            // if coeffs are multivariate (and non constant)
            // otherwise it would be 1
            t = resultant(a, b);
            x = P.ring.getONE().multiply(t);
            myList.add(x);
            return myList;
        }
        t = a.power(e); // power(cofac, a, e);
        t = t.multiply(b.power(f)); // power(cofac, b, f));
        long s = 1;
        GenPolynomial<C> z;
        myList.add(P); // adding R0
        myList.add(S); // adding R1
        while (r.degree(0) > 0) {
            long delta = q.degree(0) - r.degree(0);
            if ((q.degree(0) % 2 != 0) && (r.degree(0) % 2 != 0)) {
                s = -s;
            }
            x = PolyUtil.<C> recursiveDensePseudoRemainder(q, r);
            q = r;
            if (x.degree(0) >= 0) { // fixed: this was changed from > to >=
                z = g.multiply(h.power(delta)); // power(P.ring.coFac, h, delta));
                r = PolyUtil.<C> recursiveDivide(x, z);
                myList.add(r);
                g = q.leadingBaseCoefficient();
                z = g.power(delta); // power(cofac, g, delta);
                h = PolyUtil.<C> basePseudoDivide(z, h.power(delta - 1)); // power(cofac, h, delta - 1));
            } else {
                r = x;
                myList.add(r);
            }
        }
        z = r.leadingBaseCoefficient().power(q.degree(0)); // power(cofac, r.leadingBaseCoefficient(), q.degree(0));
        h = PolyUtil.<C> basePseudoDivide(z, h.power(q.degree() - 1)); // power(cofac, h, q.degree(0) - 1));
        z = h.multiply(t);
        if (s < 0) {
            z = z.negate();
        }
        x = P.ring.getONE().multiply(z);
        myList.add(x);
        // Printing the Subresultant List
        //System.out.println("Liste von den SubResultanten(A - tD'):");
        //for (int i = 0; i < myList.size(); i++) { // just for printing the list
        //    System.out.println(myList.get(i));
        //}
        if (logger.isInfoEnabled()) {
            System.out.println("subResCoeffs: " + myList);
            //logger.info("subResCoeffs: {}", myList);
        }
        return myList;
    }


    /**
     * GenPolynomial base coefficient discriminant.
     * @param P GenPolynomial.
     * @return discriminant(P).
     */
    public GenPolynomial<C> baseDiscriminant(GenPolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " P not univariate");
        }
        C a = P.leadingBaseCoefficient();
        a = a.inverse();
        GenPolynomial<C> Pp = PolyUtil.<C> baseDerivative(P);
        GenPolynomial<C> res = baseResultant(P, Pp);
        GenPolynomial<C> disc = res.multiply(a);
        long n = P.degree(0);
        n = n * (n - 1);
        n = n / 2;
        if (n % 2L != 0L) {
            disc = disc.negate();
        }
        return disc;
    }

}
