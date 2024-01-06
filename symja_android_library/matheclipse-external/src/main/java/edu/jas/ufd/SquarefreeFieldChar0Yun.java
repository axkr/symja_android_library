/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Squarefree decomposition for coefficient fields of characteristic 0,
 * algorithm of Yun.
 * @author Heinz Kredel
 */

public class SquarefreeFieldChar0Yun<C extends GcdRingElem<C>> extends SquarefreeFieldChar0<C> {


    private static final Logger logger = LogManager.getLogger(SquarefreeFieldChar0Yun.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public SquarefreeFieldChar0Yun(RingFactory<C> fac) {
        super(fac);
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName() + " with " + engine + " over " + coFac;
    }


    /**
     * GenPolynomial polynomial squarefree factorization.
     * @param A GenPolynomial.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with A = prod_{i=1,...,k}
     *         p_i^{e_i} and p_i squarefree and gcd(p_i, p_j) = 1, for i != j.
     */
    @Override
    public SortedMap<GenPolynomial<C>, Long> baseSquarefreeFactors(GenPolynomial<C> A) {
        SortedMap<GenPolynomial<C>, Long> sfactors = new TreeMap<GenPolynomial<C>, Long>();
        if (A == null || A.isZERO()) {
            return sfactors;
        }
        if (A.isConstant()) {
            sfactors.put(A, 1L);
            return sfactors;
        }
        GenPolynomialRing<C> pfac = A.ring;
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException(
                            this.getClass().getName() + " only for univariate polynomials");
        }
        C ldbcf = A.leadingBaseCoefficient();
        if (!ldbcf.isONE()) {
            A = A.divide(ldbcf);
            GenPolynomial<C> f1 = pfac.getONE().multiply(ldbcf);
            //System.out.println("gcda sqf f1 = " + f1);
            sfactors.put(f1, 1L);
            ldbcf = pfac.coFac.getONE();
        }
        // divide by trailing term
        ExpVector et = A.trailingExpVector();
        if (!et.isZERO()) {
            GenPolynomial<C> tr = pfac.valueOf(et);
            logger.info("trailing term = {}", tr);
            A = PolyUtil.<C> basePseudoDivide(A, tr);
            long ep = et.getVal(0); // univariate
            et = et.subst(0, 1);
            tr = pfac.valueOf(et);
            logger.info("tr, ep = {}, {}", tr, ep);
            sfactors.put(tr, ep);
            if (A.length() == 1) {
                return sfactors;
            }
        }
        GenPolynomial<C> Tp, T, W, Y, Z;
        long k = 1L; //0L
        Tp = PolyUtil.<C> baseDerivative(A);
        T = engine.baseGcd(A, Tp);
        T = T.monic();
        if (T.isConstant()) {
            sfactors.put(A, k);
            return sfactors;
        }
        W = PolyUtil.<C> basePseudoDivide(A, T);
        //if (W.isConstant()) {
        //    return sfactors;
        //}
        Y = PolyUtil.<C> basePseudoDivide(Tp, T);
        GenPolynomial<C> Wp = PolyUtil.<C> baseDerivative(W);
        Z = Y.subtract(Wp);
        while (!Z.isZERO()) {
            GenPolynomial<C> g = engine.baseGcd(W, Z);
            g = g.monic();
            if (!g.isONE()) {
                sfactors.put(g, k);
            }
            W = PolyUtil.<C> basePseudoDivide(W, g);
            //System.out.println("W = " + W);
            //System.out.println("g = " + g);
            Y = PolyUtil.<C> basePseudoDivide(Z, g);
            Wp = PolyUtil.<C> baseDerivative(W);
            Z = Y.subtract(Wp);
            k++;
        }
        logger.info("W, k = {}, {}", W, k);
        if (!W.isONE()) {
            sfactors.put(W, k);
        }
        return normalizeFactorization(sfactors);
    }


    /**
     * GenPolynomial recursive univariate polynomial squarefree factorization.
     * @param P recursive univariate GenPolynomial.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k}
     *         p_i^{e_i} and p_i squarefree and gcd(p_i, p_j) = 1, for i != j.
     */
    @Override
    public SortedMap<GenPolynomial<GenPolynomial<C>>, Long> recursiveUnivariateSquarefreeFactors(
                    GenPolynomial<GenPolynomial<C>> P) {
        SortedMap<GenPolynomial<GenPolynomial<C>>, Long> sfactors = new TreeMap<GenPolynomial<GenPolynomial<C>>, Long>();
        if (P == null || P.isZERO()) {
            return sfactors;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // recursiveContent not possible by return type
            throw new IllegalArgumentException(
                            this.getClass().getName() + " only for univariate polynomials");
        }
        // if base coefficient ring is a field, make monic
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) pfac.coFac;
        C ldbcf = P.leadingBaseCoefficient().leadingBaseCoefficient();
        if (!ldbcf.isONE()) {
            GenPolynomial<C> lc = cfac.getONE().multiply(ldbcf);
            GenPolynomial<GenPolynomial<C>> pl = pfac.getONE().multiply(lc);
            sfactors.put(pl, 1L);
            C li = ldbcf.inverse();
            //System.out.println("li = " + li);
            P = P.multiply(cfac.getONE().multiply(li));
            //System.out.println("P,monic = " + P);
            ldbcf = P.leadingBaseCoefficient().leadingBaseCoefficient();
        }
        // factors of content
        GenPolynomial<C> Pc = engine.recursiveContent(P);
        logger.info("recursiveContent = {}", Pc);
        Pc = Pc.monic();
        if (!Pc.isONE()) {
            P = PolyUtil.<C> coefficientPseudoDivide(P, Pc);
        }
        SortedMap<GenPolynomial<C>, Long> rsf = squarefreeFactors(Pc);
        logger.info("squarefreeFactors = {}", rsf);
        // add factors of content
        for (Map.Entry<GenPolynomial<C>, Long> me : rsf.entrySet()) {
            GenPolynomial<C> c = me.getKey();
            if (!c.isONE()) {
                GenPolynomial<GenPolynomial<C>> cr = pfac.getONE().multiply(c);
                Long rk = me.getValue();
                sfactors.put(cr, rk);
            }
        }
        // divide by trailing term
        ExpVector et = P.trailingExpVector();
        if (!et.isZERO()) {
            GenPolynomial<GenPolynomial<C>> tr = pfac.valueOf(et);
            logger.info("trailing term = {}", tr);
            P = PolyUtil.<C> recursivePseudoDivide(P, tr);
            long ep = et.getVal(0); // univariate
            et = et.subst(0, 1);
            tr = pfac.valueOf(et);
            sfactors.put(tr, ep);
            //P.length == 1 ??
        }
        // factors of recursive polynomial
        GenPolynomial<GenPolynomial<C>> Tp, T, W, Y, Z;
        long k = 1L; //0L
        Tp = PolyUtil.<C> recursiveDerivative(P);
        T = engine.recursiveUnivariateGcd(P, Tp);
        T = PolyUtil.<C> monic(T);
        if (T.isConstant()) {
            sfactors.put(P, k);
            return sfactors;
        }
        W = PolyUtil.<C> recursivePseudoDivide(P, T);
        //if (W.isConstant()) {
        //    return sfactors;
        //}
        Y = PolyUtil.<C> recursivePseudoDivide(Tp, T);
        GenPolynomial<GenPolynomial<C>> Wp = PolyUtil.<C> recursiveDerivative(W);
        Z = Y.subtract(Wp);

        while (!Z.isZERO()) {
            GenPolynomial<GenPolynomial<C>> g = engine.recursiveGcd(W, Z);
            g = PolyUtil.<C> monic(g);
            if (!g.isONE()) {
                sfactors.put(g, k);
            }
            W = PolyUtil.<C> recursivePseudoDivide(W, g);
            //System.out.println("W = " + W);
            //System.out.println("g = " + g);
            Y = PolyUtil.<C> recursivePseudoDivide(Z, g);
            Wp = PolyUtil.<C> recursiveDerivative(W);
            Z = Y.subtract(Wp);
            k++;
        }
        logger.info("W, k = {}, {}", W, k);
        if (!W.isONE()) {
            sfactors.put(W, k);
        }
        return sfactors; //normalizeFactorization(sfactors);
    }

}
