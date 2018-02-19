/*
 * $Id$
 */

package edu.jas.fd;


import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * (Non-unique) factorization domain greatest common divisor common algorithms
 * with monic polynomial remainder sequence. Fake implementation always returns
 * 1 for any gcds.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GreatestCommonDivisorFake<C extends GcdRingElem<C>> extends GreatestCommonDivisorAbstract<C> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorFake.class);


    //private static final boolean debug = true; //logger.isDebugEnabled();


    /**
     * Constructor.
     *
     * @param cf coefficient ring.
     */
    public GreatestCommonDivisorFake(RingFactory<C> cf) {
        super(cf);
    }


    /**
     * Univariate GenSolvablePolynomial greatest common divisor. Uses
     * pseudoRemainder for remainder.
     *
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return 1 = gcd(P,S) with P = P'*gcd(P,S) and S = S'*gcd(P,S).
     */
    @Override
    public GenSolvablePolynomial<C> leftBaseGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " no univariate polynomial");
        }
        logger.warn("fake gcd always returns 1");
        return P.ring.getONE();
    }


    /**
     * Univariate GenSolvablePolynomial right greatest common divisor. Uses
     * pseudoRemainder for remainder.
     *
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return 1 = gcd(P,S) with P = gcd(P,S)*P' and S = gcd(P,S)*S'.
     */
    @Override
    public GenSolvablePolynomial<C> rightBaseGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " no univariate polynomial");
        }
        logger.warn("fake gcd always returns 1");
        return P.ring.getONE();
    }


    /**
     * Univariate GenSolvablePolynomial left recursive greatest common divisor.
     * Uses pseudoRemainder for remainder.
     *
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return 1 = gcd(P,S) with P = P'*gcd(P,S)*p and S = S'*gcd(P,S)*s, where
     * deg_main(p) = deg_main(s) == 0.
     */
    @Override
    public GenSolvablePolynomial<GenPolynomial<C>> leftRecursiveUnivariateGcd(
            GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar > 1) {
            throw new IllegalArgumentException("no univariate polynomial");
        }
        return P.ring.getONE();
    }


    /**
     * Univariate GenSolvablePolynomial right recursive greatest common divisor.
     * Uses pseudoRemainder for remainder.
     *
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return 1 = gcd(P,S) with P = p*gcd(P,S)*P' and S = s*gcd(P,S)*S', where
     * deg_main(p) = deg_main(s) == 0.
     */
    @Override
    public GenSolvablePolynomial<GenPolynomial<C>> rightRecursiveUnivariateGcd(
            GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar > 1) {
            throw new IllegalArgumentException("no univariate polynomial");
        }
        return P.ring.getONE();
    }

}
