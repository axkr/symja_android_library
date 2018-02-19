/*
 * $Id$
 */

package edu.jas.ufd;


import org.apache.log4j.Logger;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Squarefree decomposition for coefficient fields of characteristic 0.
 *
 * @author Heinz Kredel
 */

public class SquarefreeFieldChar0<C extends GcdRingElem<C>> extends SquarefreeAbstract<C> {


    private static final Logger logger = Logger.getLogger(SquarefreeFieldChar0.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Factory for field of characteristic 0 coefficients.
     */
    protected final RingFactory<C> coFac;


    /**
     * Constructor.
     */
    public SquarefreeFieldChar0(RingFactory<C> fac) {
        super(GCDFactory.<C>getProxy(fac));
        if (!fac.isField()) {
            throw new IllegalArgumentException("fac must be a field");
        }
        if (fac.characteristic().signum() != 0) {
            throw new IllegalArgumentException("characterisic(fac) must be zero");
        }
        coFac = fac;
    }


    /**
     * Get the String representation.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName() + " with " + engine + " over " + coFac;
    }


    /**
     * GenPolynomial polynomial greatest squarefree divisor.
     *
     * @param P GenPolynomial.
     * @return squarefree(pp(P)).
     */
    @Override
    public GenPolynomial<C> baseSquarefreePart(GenPolynomial<C> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " only for univariate polynomials");
        }
        GenPolynomial<C> pp = P.monic();
        if (pp.isConstant()) {
            return pp;
        }
        GenPolynomial<C> d = PolyUtil.<C>baseDeriviative(pp);
        d = d.monic();
        //System.out.println("d = " + d);
        GenPolynomial<C> g = engine.baseGcd(pp, d);
        g = g.monic();
        GenPolynomial<C> q = PolyUtil.<C>basePseudoDivide(pp, g);
        q = q.monic();
        return q;
    }


    /**
     * GenPolynomial test if is squarefree.
     *
     * @param P GenPolynomial.
     * @return true if P is squarefree, else false.
     */
    public boolean isBaseSquarefree(GenPolynomial<C> P) {
        if (P == null || P.isZERO()) {
            return true;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " only for univariate polynomials");
        }
        GenPolynomial<C> pp = P.monic();
        if (pp.isConstant()) {
            return true;
        }
        GenPolynomial<C> d = PolyUtil.<C>baseDeriviative(pp);
        d = d.monic();
        //System.out.println("d = " + d);
        GenPolynomial<C> g = engine.baseGcd(pp, d);
        g = g.monic();
        return g.isONE();
    }


    /**
     * GenPolynomial polynomial squarefree factorization.
     *
     * @param A GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     * and p_i squarefree.
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
            throw new IllegalArgumentException(this.getClass().getName() + " only for univariate polynomials");
        }
        C ldbcf = A.leadingBaseCoefficient();
        if (!ldbcf.isONE()) {
            A = A.divide(ldbcf);
            GenPolynomial<C> f1 = pfac.getONE().multiply(ldbcf);
            //System.out.println("gcda sqf f1 = " + f1);
            sfactors.put(f1, 1L);
            ldbcf = pfac.coFac.getONE();
        }
        GenPolynomial<C> T0 = A;
        GenPolynomial<C> Tp;
        GenPolynomial<C> T = null;
        GenPolynomial<C> V = null;
        long k = 0L;
        boolean init = true;
        while (true) {
            if (init) {
                if (T0.isConstant() || T0.isZERO()) {
                    break;
                }
                Tp = PolyUtil.<C>baseDeriviative(T0);
                T = engine.baseGcd(T0, Tp);
                T = T.monic();
                V = PolyUtil.<C>basePseudoDivide(T0, T);
                //System.out.println("iT0 = " + T0);
                //System.out.println("iTp = " + Tp);
                //System.out.println("iT  = " + T);
                //System.out.println("iV  = " + V);
                k = 0L;
                init = false;
            }
            if (V.isConstant()) {
                break;
            }
            k++;
            GenPolynomial<C> W = engine.baseGcd(T, V);
            W = W.monic();
            GenPolynomial<C> z = PolyUtil.<C>basePseudoDivide(V, W);
            //System.out.println("W = " + W);
            //System.out.println("z = " + z);
            V = W;
            T = PolyUtil.<C>basePseudoDivide(T, V);
            //System.out.println("V = " + V);
            //System.out.println("T = " + T);
            if (z.degree(0) > 0) {
                if (ldbcf.isONE() && !z.leadingBaseCoefficient().isONE()) {
                    z = z.monic();
                    logger.info("z,monic = " + z);
                }
                sfactors.put(z, k);
            }
        }
        //      look, a stupid error:
        //         if ( pfac.coFac.isField() && !ldbcf.isONE() ) {
        //             GenPolynomial<C> f1 = sfactors.firstKey();
        //             long e1 = sfactors.remove(f1);
        //             System.out.println("gcda sqf c = " + c);
        //             f1 = f1.multiply(c);
        //             //System.out.println("gcda sqf f1e = " + f1);
        //             sfactors.put(f1,e1);
        //         }
        return normalizeFactorization(sfactors);
    }


    /**
     * GenPolynomial recursive univariate polynomial greatest squarefree
     * divisor.
     *
     * @param P recursive univariate GenPolynomial.
     * @return squarefree(pp(P)).
     */
    @Override
    public GenPolynomial<GenPolynomial<C>> recursiveUnivariateSquarefreePart(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName()
                    + " only for multivariate polynomials");
        }
        // squarefree content
        GenPolynomial<GenPolynomial<C>> pp = P;
        GenPolynomial<C> Pc = engine.recursiveContent(P);
        Pc = Pc.monic();
        if (!Pc.isONE()) {
            pp = PolyUtil.<C>coefficientPseudoDivide(pp, Pc);
            //System.out.println("pp,sqp = " + pp);
            //GenPolynomial<C> Pr = squarefreePart(Pc);
            //Pr = Pr.monic();
            //System.out.println("Pr,sqp = " + Pr);
        }
        if (pp.leadingExpVector().getVal(0) < 1) {
            //System.out.println("pp = " + pp);
            //System.out.println("Pc = " + Pc);
            return pp.multiply(Pc);
        }
        GenPolynomial<GenPolynomial<C>> d = PolyUtil.<C>recursiveDeriviative(pp);
        //System.out.println("d = " + d);
        GenPolynomial<GenPolynomial<C>> g = engine.recursiveUnivariateGcd(pp, d);
        //System.out.println("g,rec = " + g);
        g = PolyUtil.<C>monic(g);
        GenPolynomial<GenPolynomial<C>> q = PolyUtil.<C>recursivePseudoDivide(pp, g);
        q = PolyUtil.<C>monic(q);
        return q.multiply(Pc);
    }


    /**
     * GenPolynomial test if is squarefree.
     *
     * @param P GenPolynomial.
     * @return true if P is squarefree, else false.
     */
    public boolean isRecursiveUnivariateSquarefree(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null || P.isZERO()) {
            return true;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName()
                    + " only for multivariate polynomials");
        }
        // squarefree content
        GenPolynomial<GenPolynomial<C>> pp = P;
        GenPolynomial<C> Pc = engine.recursiveContent(P);
        if (logger.isInfoEnabled()) {
            logger.info("recursiveContent = " + Pc);
        }
        if (!isSquarefree(Pc)) {
            return false;
        }
        Pc = Pc.monic();
        if (!Pc.isONE()) {
            pp = PolyUtil.<C>coefficientPseudoDivide(pp, Pc);
            //System.out.println("pp,sqp = " + pp);
        }
        if (pp.leadingExpVector().getVal(0) <= 1) {
            //System.out.println("pp = " + pp);
            //System.out.println("Pc = " + Pc);
            return true;
        }
        GenPolynomial<GenPolynomial<C>> d = PolyUtil.<C>recursiveDeriviative(pp);
        //System.out.println("d = " + d);
        GenPolynomial<GenPolynomial<C>> g = engine.recursiveUnivariateGcd(pp, d);
        if (logger.isInfoEnabled()) {
            logger.info("gcd = " + g);
        }
        //System.out.println("g,rec = " + g);
        g = PolyUtil.<C>monic(g);
        return g.isONE();
    }


    /**
     * GenPolynomial recursive univariate polynomial squarefree factorization.
     *
     * @param P recursive univariate GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     * and p_i squarefree.
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
            throw new IllegalArgumentException(this.getClass().getName() + " only for univariate polynomials");
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
        if (logger.isInfoEnabled()) {
            logger.info("recursiveContent = " + Pc);
        }
        Pc = Pc.monic();
        if (!Pc.isONE()) {
            P = PolyUtil.<C>coefficientPseudoDivide(P, Pc);
        }
        SortedMap<GenPolynomial<C>, Long> rsf = squarefreeFactors(Pc);
        if (logger.isInfoEnabled()) {
            logger.info("squarefreeFactors = " + rsf);
        }
        // add factors of content
        for (Map.Entry<GenPolynomial<C>, Long> me : rsf.entrySet()) {
            GenPolynomial<C> c = me.getKey();
            if (!c.isONE()) {
                GenPolynomial<GenPolynomial<C>> cr = pfac.getONE().multiply(c);
                Long rk = me.getValue(); // rsf.get(c);
                sfactors.put(cr, rk);
            }
        }
        // divide by trailing term
        ExpVector et = P.trailingExpVector();
        if (!et.isZERO()) {
            GenPolynomial<GenPolynomial<C>> tr = pfac.valueOf(et);
            if (logger.isInfoEnabled()) {
                logger.info("trailing term = " + tr);
            }
            P = PolyUtil.<C>recursivePseudoDivide(P, tr);
            long ep = et.getVal(0); // univariate
            et = et.subst(0, 1);
            tr = pfac.valueOf(et);
            sfactors.put(tr, ep);
        }

        // factors of recursive polynomial
        GenPolynomial<GenPolynomial<C>> T0 = P;
        GenPolynomial<GenPolynomial<C>> Tp;
        GenPolynomial<GenPolynomial<C>> T = null;
        GenPolynomial<GenPolynomial<C>> V = null;
        long k = 0L;
        boolean init = true;
        while (true) {
            if (init) {
                if (T0.isConstant() || T0.isZERO()) {
                    break;
                }
                Tp = PolyUtil.<C>recursiveDeriviative(T0);
                T = engine.recursiveUnivariateGcd(T0, Tp);
                T = PolyUtil.<C>monic(T);
                V = PolyUtil.<C>recursivePseudoDivide(T0, T);
                //System.out.println("iT0 = " + T0);
                //System.out.println("iTp = " + Tp);
                //System.out.println("iT = " + T);
                //System.out.println("iV = " + V);
                k = 0L;
                init = false;
            }
            if (V.isConstant()) {
                break;
            }
            k++;
            GenPolynomial<GenPolynomial<C>> W = engine.recursiveUnivariateGcd(T, V);
            W = PolyUtil.<C>monic(W);
            GenPolynomial<GenPolynomial<C>> z = PolyUtil.<C>recursivePseudoDivide(V, W);
            //System.out.println("W = " + W);
            //System.out.println("z = " + z);
            V = W;
            T = PolyUtil.<C>recursivePseudoDivide(T, V);
            //System.out.println("V = " + V);
            //System.out.println("T = " + T);
            //was: if ( z.degree(0) > 0 ) {
            if (!z.isONE() && !z.isZERO()) {
                if (ldbcf.isONE()) {
                    z = PolyUtil.<C>monic(z);
                    logger.info("z,monic = " + z);
                }
                sfactors.put(z, k);
            }
        }
        return sfactors;
    }


    /**
     * GenPolynomial greatest squarefree divisor.
     *
     * @param P GenPolynomial.
     * @return squarefree(pp(P)).
     */
    @Override
    public GenPolynomial<C> squarefreePart(GenPolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            return baseSquarefreePart(P);
        }
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1);
        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C>recursive(rfac, P);
        GenPolynomial<C> Pc = engine.recursiveContent(Pr);
        Pr = PolyUtil.<C>coefficientPseudoDivide(Pr, Pc);
        GenPolynomial<C> Ps = squarefreePart(Pc);
        if (logger.isInfoEnabled()) {
            logger.info("content = " + Pc + ", squarefreePart = " + Ps);
        }
        GenPolynomial<GenPolynomial<C>> PP = recursiveUnivariateSquarefreePart(Pr);
        GenPolynomial<GenPolynomial<C>> PS = PP.multiply(Ps);
        GenPolynomial<C> D = PolyUtil.<C>distribute(pfac, PS);
        if (logger.isInfoEnabled()) {
            logger.info("univRec = " + Pr + ", squarefreePart = " + PP);
        }
        return D;
    }


    /**
     * GenPolynomial test if is squarefree.
     *
     * @param P GenPolynomial.
     * @return true if P is squarefree, else false.
     */
    @Override
    public boolean isSquarefree(GenPolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return true;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            return isBaseSquarefree(P);
        }
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1);
        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C>recursive(rfac, P);
        return isRecursiveUnivariateSquarefree(Pr);
    }


    /**
     * GenPolynomial squarefree factorization.
     *
     * @param P GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     * and p_i squarefree.
     */
    @Override
    public SortedMap<GenPolynomial<C>, Long> squarefreeFactors(GenPolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            return normalizeFactorization(baseSquarefreeFactors(P));
        }
        SortedMap<GenPolynomial<C>, Long> sfactors = new TreeMap<GenPolynomial<C>, Long>();
        if (P.isZERO()) {
            return normalizeFactorization(sfactors);
        }
        if (P.isONE()) {
            sfactors.put(P, 1L);
            return normalizeFactorization(sfactors);
        }
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1);
        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C>recursive(rfac, P);
        SortedMap<GenPolynomial<GenPolynomial<C>>, Long> PP = recursiveUnivariateSquarefreeFactors(Pr);

        for (Map.Entry<GenPolynomial<GenPolynomial<C>>, Long> m : PP.entrySet()) {
            Long i = m.getValue();
            GenPolynomial<GenPolynomial<C>> Dr = m.getKey();
            GenPolynomial<C> D = PolyUtil.<C>distribute(pfac, Dr);
            sfactors.put(D, i);
        }
        if (logger.isInfoEnabled()) {
            logger.info("squarefreeFactors(" + P + ") = " + sfactors);
        }
        return normalizeFactorization(sfactors);
    }


    /**
     * Coefficients squarefree factorization.
     *
     * @param P coefficient.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     * and p_i squarefree.
     */
    @Override
    public SortedMap<C, Long> squarefreeFactors(C P) {
        throw new UnsupportedOperationException("method not implemented");
    }

}
