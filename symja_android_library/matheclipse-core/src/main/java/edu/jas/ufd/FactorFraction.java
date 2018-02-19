/*
 * $Id$
 */

package edu.jas.ufd;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.QuotPair;
import edu.jas.structure.QuotPairFactory;


/**
 * Fraction factorization algorithms. This class implements
 * factorization methods for fractions represended as pairs of
 * polynomials.
 *
 * @author Heinz Kredel
 */

public class FactorFraction<C extends GcdRingElem<C>,
        D extends GcdRingElem<D> & QuotPair<GenPolynomial<C>>> {


    private static final Logger logger = Logger.getLogger(FactorFraction.class);


    /**
     * Quotient pairs ring factory.
     * D == QuotPair&lt;GenPolynomial&lt;C&gt;&gt; must hold.
     */
    protected final QuotPairFactory<GenPolynomial<C>, D> qfac;


    /**
     * Factorization engine for normal coefficients.
     */
    protected final FactorAbstract<C> nengine;


    /**
     * No argument constructor.
     */
    protected FactorFraction() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor.
     *
     * @param fac coefficient quotient ring factory.
     */
    public FactorFraction(QuotPairFactory<GenPolynomial<C>, D> fac) {
        this(fac, FactorFactory.<C>getImplementation(((GenPolynomialRing<C>) fac.pairFactory()).coFac));
    }


    /**
     * Constructor.
     *
     * @param fac     coefficient quotient ring factory.
     * @param nengine factorization engine for polynomials over base
     *                coefficients.
     */
    public FactorFraction(QuotPairFactory<GenPolynomial<C>, D> fac, FactorAbstract<C> nengine) {
        this.qfac = fac;
        this.nengine = nengine;
        logger.info("qfac.fac: " + qfac.pairFactory().toScript());
    }


    /**
     * Get the String representation.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName();
    }


    /**
     * Test if a quotient pair is irreducible.
     *
     * @param P quotient pair (num,den), with gcd(num,den) == 1.
     * @return true if P is irreducible, else false.
     */
    public boolean isIrreducible(D P) {
        SortedMap<D, Long> F = factors(P);
        for (Long e : F.values()) {
            if (e == null || e != 1L) {
                return false;
            }
        }
        if (F.size() <= 1) { // x/1
            return true;
        } else if (F.size() == 2) { // x/1, 1/y
            List<D> pp = new ArrayList<D>(F.keySet());
            D f = pp.get(0);
            D g = pp.get(1);
            if ((f.numerator().isONE() && g.denominator().isONE()) || (g.numerator().isONE() && f.denominator().isONE())) {
                return true;
            }
            return false;
        } else if (F.size() > 2) {
            return false;
        }
        return false;
    }


    /**
     * Test if a non trivial factorization exsists.
     *
     * @param P quotient pair (num,den), with gcd(num,den) == 1.
     * @return true if P is reducible, else false.
     */
    public boolean isReducible(D P) {
        return !isIrreducible(P);
    }


    /**
     * Quotient pair factorization.
     *
     * @param P quotient pair (num,den), with gcd(num,den) == 1.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k} p_i**e_i.
     */
    public SortedMap<D, Long> factors(D P) {
        // D == QuotPair<GenPolynomial<C>>
        SortedMap<D, Long> facs = new TreeMap<D, Long>();
        if (P == null) {
            return facs;
        }
        GenPolynomial<C> n = P.numerator();
        GenPolynomial<C> d = P.denominator();
        if (n.isZERO() || d.isZERO()) {
            return facs;
        }
        if (n.isONE() && d.isONE()) {
            facs.put(P, 1L);
            return facs;
        }
        // assert gcd(n,d) == 1
        GenPolynomial<C> one = qfac.pairFactory().getONE();
        if (!n.isONE()) {
            SortedMap<GenPolynomial<C>, Long> nfacs = nengine.factors(n);
            for (Map.Entry<GenPolynomial<C>, Long> m : nfacs.entrySet()) {
                D q = qfac.create(m.getKey(), one);
                facs.put(q, m.getValue());
            }
        }
        if (!d.isONE()) {
            SortedMap<GenPolynomial<C>, Long> dfacs = nengine.factors(d);
            for (Map.Entry<GenPolynomial<C>, Long> m : dfacs.entrySet()) {
                D q = qfac.create(one, m.getKey());
                facs.put(q, m.getValue());
            }
        }
        return facs;
    }


    /**
     * Test quotient pair factorization.
     *
     * @param P quotient pair.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i, else false.
     */
    public boolean isFactorization(D P, SortedMap<D, Long> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        if (P.isZERO() && F.size() == 0) {
            return true;
        }
        D t = null; //P.ring.getONE();
        for (Map.Entry<D, Long> me : F.entrySet()) {
            D f = me.getKey();
            Long E = me.getValue();
            long e = E.longValue();
            D g = f.power(e);
            if (t == null) {
                t = g;
            } else {
                t = t.multiply(g);
            }
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            System.out.println("\nfactorization(map): " + f);
            System.out.println("F = " + F);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            //RuntimeException e = new RuntimeException("fac-map");
            //e.printStackTrace();
            //throw e;
        }
        return f;
    }

}
