/*
 * $Id$
 */

package edu.jas.fd;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.gb.SolvableGroebnerBaseSeq;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * (Non-unique) factorization domain greatest common divisor common algorithms
 * with syzygy computation. The implementation uses solvable syzygy gcd
 * computation.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GreatestCommonDivisorSyzygy<C extends GcdRingElem<C>> extends GreatestCommonDivisorAbstract<C> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorSyzygy.class);


    private static final boolean debug = true; //logger.isDebugEnabled();


    /**
     * Constructor.
     *
     * @param cf coefficient ring.
     */
    public GreatestCommonDivisorSyzygy(RingFactory<C> cf) {
        super(cf);
    }


    /**
     * Left univariate GenSolvablePolynomial greatest common divisor.
     *
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return gcd(P, S) with P = P'*gcd(P,S) and S = S'*gcd(P,S).
     */
    @Override
    public GenSolvablePolynomial<C> leftBaseGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        return leftGcd(P, S);
    }


    /**
     * Right univariate GenSolvablePolynomial greatest common divisor.
     *
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return gcd(P, S) with P = P'*gcd(P,S) and S = S'*gcd(P,S).
     */
    @Override
    public GenSolvablePolynomial<C> rightBaseGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        return rightGcd(P, S);
    }


    /**
     * Left GenSolvablePolynomial greatest common divisor.
     *
     * @param P GenSolvablePolynomial.
     * @param S GenSolvablePolynomial.
     * @return gcd(P, S) with P = P'*gcd(P,S) and S = S'*gcd(P,S).
     */
    @Override
    public GenSolvablePolynomial<C> leftGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.isConstant()) {
            return P.ring.getONE();
        }
        if (S.isConstant()) {
            return P.ring.getONE();
        }
        List<GenSolvablePolynomial<C>> A = new ArrayList<GenSolvablePolynomial<C>>(2);
        A.add(P);
        A.add(S);
        SolvableGroebnerBaseAbstract<C> sbb = new SolvableGroebnerBaseSeq<C>();
        logger.warn("left syzGcd computing GB: " + A);
        List<GenSolvablePolynomial<C>> G = sbb.rightGB(A); //not: leftGB, not: sbb.twosidedGB(A);
        if (debug) {
            logger.info("G = " + G);
        }
        if (G.size() == 1) {
            return G.get(0);
        }
        logger.warn("gcd not determined, set to 1: " + G); // + ", A = " + A);
        return P.ring.getONE();
    }


    /**
     * Right GenSolvablePolynomial right greatest common divisor.
     *
     * @param P GenSolvablePolynomial.
     * @param S GenSolvablePolynomial.
     * @return gcd(P, S) with P = gcd(P,S)*P' and S = gcd(P,S)*S'.
     */
    @Override
    public GenSolvablePolynomial<C> rightGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.isConstant()) {
            return P.ring.getONE();
        }
        if (S.isConstant()) {
            return P.ring.getONE();
        }
        List<GenSolvablePolynomial<C>> A = new ArrayList<GenSolvablePolynomial<C>>(2);
        A.add(P);
        A.add(S);
        SolvableGroebnerBaseAbstract<C> sbb = new SolvableGroebnerBaseSeq<C>();
        logger.warn("left syzGcd computing GB: " + A);
        List<GenSolvablePolynomial<C>> G = sbb.leftGB(A); //not: sbb.twosidedGB(A);
        if (debug) {
            logger.info("G = " + G);
        }
        if (G.size() == 1) {
            return G.get(0);
        }
        logger.warn("gcd not determined, set to 1: " + G); // + ", A = " + A);
        return P.ring.getONE();
    }


    /**
     * Univariate GenSolvablePolynomial left recursive greatest common divisor.
     *
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return gcd(P, S) with P = P'*gcd(P,S)*p and S = S'*gcd(P,S)*s, where
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
        if (P.isConstant()) {
            return P.ring.getONE();
        }
        if (S.isConstant()) {
            return P.ring.getONE();
        }
        List<GenSolvablePolynomial<GenPolynomial<C>>> A = new ArrayList<GenSolvablePolynomial<GenPolynomial<C>>>(
                2);
        A.add(P);
        A.add(S);
        SolvableGroebnerBaseAbstract<GenPolynomial<C>> sbb = new SolvableGroebnerBaseSeq<GenPolynomial<C>>();
        logger.warn("left syzGcd computing GB: " + A);
        // will not work, not field
        List<GenSolvablePolynomial<GenPolynomial<C>>> G = sbb.rightGB(A); //not: leftGB, not: sbb.twosidedGB(A);
        if (debug) {
            logger.info("G = " + G);
        }
        if (G.size() == 1) {
            return G.get(0);
        }
        logger.warn("gcd not determined, set to 1: " + G); // + ", A = " + A);
        return P.ring.getONE();
    }


    /**
     * Univariate GenSolvablePolynomial right recursive greatest common divisor.
     *
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return gcd(P, S) with P = p*gcd(P,S)*P' and S = s*gcd(P,S)*S', where
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
        if (P.isConstant()) {
            return P.ring.getONE();
        }
        if (S.isConstant()) {
            return P.ring.getONE();
        }
        List<GenSolvablePolynomial<GenPolynomial<C>>> A = new ArrayList<GenSolvablePolynomial<GenPolynomial<C>>>(
                2);
        A.add(P);
        A.add(S);
        SolvableGroebnerBaseAbstract<GenPolynomial<C>> sbb = new SolvableGroebnerBaseSeq<GenPolynomial<C>>();
        logger.warn("right syzGcd computing GB: " + A);
        // will not work, not field
        List<GenSolvablePolynomial<GenPolynomial<C>>> G = sbb.leftGB(A); //not: sbb.twosidedGB(A);
        if (debug) {
            logger.info("G = " + G);
        }
        if (G.size() == 1) {
            return G.get(0);
        }
        logger.warn("gcd not determined, set to 1: " + G); // + ", A = " + A);
        return P.ring.getONE();
    }

}
