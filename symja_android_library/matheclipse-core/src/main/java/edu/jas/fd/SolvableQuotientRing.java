/*
 * $Id$
 */

package edu.jas.fd;


import org.apache.log4j.Logger;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.jas.gbufd.SolvableSyzygyAbstract;
import edu.jas.gbufd.SolvableSyzygySeq;
import edu.jas.kern.StringUtil;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.QuotPairFactory;
import edu.jas.structure.RingFactory;


/**
 * SolvableQuotient ring factory based on GenPolynomial with RingElem interface.
 * Objects of this class are immutable.
 *
 * @author Heinz Kredel
 */
public class SolvableQuotientRing<C extends GcdRingElem<C>> implements RingFactory<SolvableQuotient<C>>,
        QuotPairFactory<GenPolynomial<C>, SolvableQuotient<C>> {
    // should be QuotPairFactory<GenSolvablePolynomial<C>


    private static final Logger logger = Logger.getLogger(SolvableQuotientRing.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Solvable polynomial ring of the factory.
     */
    public final GenSolvablePolynomialRing<C> ring;


    /**
     * Syzygy engine of the factory.
     */
    public final SolvableSyzygyAbstract<C> engine;


    /**
     * The constructor creates a SolvableQuotientRing object from a
     * GenSolvablePolynomialRing.
     *
     * @param r solvable polynomial ring.
     */
    public SolvableQuotientRing(GenSolvablePolynomialRing<C> r) {
        ring = r;
        engine = new SolvableSyzygySeq<C>(ring.coFac);
        logger.debug("quotient ring constructed");
    }


    /**
     * Factory for base elements.
     */
    public GenSolvablePolynomialRing<C> pairFactory() {
        return ring;
    }


    /**
     * Create from numerator.
     */
    @SuppressWarnings("unchecked")
    public SolvableQuotient<C> create(GenPolynomial<C> n) {
        return new SolvableQuotient<C>(this, (GenSolvablePolynomial<C>) n);
    }


    /**
     * Create from numerator, denominator pair.
     */
    @SuppressWarnings("unchecked")
    public SolvableQuotient<C> create(GenPolynomial<C> n, GenPolynomial<C> d) {
        return new SolvableQuotient<C>(this, (GenSolvablePolynomial<C>) n, (GenSolvablePolynomial<C>) d);
    }


    /**
     * Is this structure finite or infinite.
     *
     * @return true if this structure is finite, else false.
     */
    public boolean isFinite() {
        return ring.isFinite();
    }


    /**
     * Copy SolvableQuotient element c.
     *
     * @param c
     * @return a copy of c.
     */
    public SolvableQuotient<C> copy(SolvableQuotient<C> c) {
        return new SolvableQuotient<C>(c.ring, c.num, c.den, true);
    }


    /**
     * Get the zero element.
     *
     * @return 0 as SolvableQuotient.
     */
    public SolvableQuotient<C> getZERO() {
        return new SolvableQuotient<C>(this, ring.getZERO());
    }


    /**
     * Get the one element.
     *
     * @return 1 as SolvableQuotient.
     */
    public SolvableQuotient<C> getONE() {
        return new SolvableQuotient<C>(this, ring.getONE());
    }


    /**
     * Get a list of the generating elements.
     *
     * @return list of generators for the algebraic structure.
     */
    public List<SolvableQuotient<C>> generators() {
        List<GenSolvablePolynomial<C>> pgens = PolynomialList.<C>castToSolvableList(ring.generators());
        List<SolvableQuotient<C>> gens = new ArrayList<SolvableQuotient<C>>(pgens.size() * 2 - 1);
        GenSolvablePolynomial<C> one = ring.getONE();
        for (GenSolvablePolynomial<C> p : pgens) {
            SolvableQuotient<C> q = new SolvableQuotient<C>(this, p);
            gens.add(q);
            if (!p.isONE()) {
                q = new SolvableQuotient<C>(this, one, p);
                gens.add(q);
            }
        }
        return gens;
    }


    /**
     * Query if this ring is commutative.
     *
     * @return true if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return ring.isCommutative();
    }


    /**
     * Query if this ring is associative.
     *
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        if (!ring.isAssociative()) {
            return false;
        }
        SolvableQuotient<C> Xi, Xj, Xk, p, q;
        List<SolvableQuotient<C>> gens = generators();
        int ngen = gens.size();
        for (int i = 0; i < ngen; i++) {
            Xi = gens.get(i);
            for (int j = i + 1; j < ngen; j++) {
                Xj = gens.get(j);
                for (int k = j + 1; k < ngen; k++) {
                    Xk = gens.get(k);
                    p = Xk.multiply(Xj).multiply(Xi);
                    q = Xk.multiply(Xj.multiply(Xi));
                    if (!p.equals(q)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("Xk = " + Xk + ", Xj = " + Xj + ", Xi = " + Xi);
                            logger.info("p = ( Xk * Xj ) * Xi = " + p);
                            logger.info("q = Xk * ( Xj * Xi ) = " + q);
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Query if this ring is a field.
     *
     * @return true.
     */
    public boolean isField() {
        return true;
    }


    /**
     * Characteristic of this ring.
     *
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return ring.characteristic();
    }


    /**
     * Get a SolvableQuotient element from a BigInteger value.
     *
     * @param a BigInteger.
     * @return a SolvableQuotient.
     */
    public SolvableQuotient<C> fromInteger(java.math.BigInteger a) {
        return new SolvableQuotient<C>(this, ring.fromInteger(a));
    }


    /**
     * Get a SolvableQuotient element from a long value.
     *
     * @param a long.
     * @return a SolvableQuotient.
     */
    public SolvableQuotient<C> fromInteger(long a) {
        return new SolvableQuotient<C>(this, ring.fromInteger(a));
    }


    /**
     * Get the String representation as RingFactory.
     */
    @Override
    public String toString() {
        String s = null;
        if (ring.coFac.characteristic().signum() == 0) {
            s = "RatFunc";
        } else {
            s = "ModFunc";
        }
        return s + "( " + ring.toString() + " )";
    }


    /**
     * Get a scripting compatible string representation.
     *
     * @return script compatible representation for this ElemFactory.
     */
    @Override
    public String toScript() {
        // Python case
        return "SRF(" + ring.toScript() + ")";
    }


    /**
     * Comparison with any other object.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (b == null) {
            return false;
        }
        if (!(b instanceof SolvableQuotientRing)) {
            return false;
        }
        SolvableQuotientRing<C> a = (SolvableQuotientRing<C>) b;
        return ring.equals(a.ring);
    }


    /**
     * Hash code for this quotient ring.
     */
    @Override
    public int hashCode() {
        int h;
        h = ring.hashCode();
        return h;
    }


    /**
     * SolvableQuotient random.
     *
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random quotient element.
     */
    public SolvableQuotient<C> random(int n) {
        GenSolvablePolynomial<C> r = ring.random(n).monic();
        GenSolvablePolynomial<C> s;
        do {
            s = ring.random(n).monic();
        } while (s.isZERO());
        return new SolvableQuotient<C>(this, r, s, false);
    }


    /**
     * Generate a random quotient.
     *
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @return a random quotient.
     */
    public SolvableQuotient<C> random(int k, int l, int d, float q) {
        GenSolvablePolynomial<C> r = ring.random(k, l, d, q).monic();
        GenSolvablePolynomial<C> s = ring.random(k, l, d, q).monic();
        do {
            s = ring.random(k, l, d, q).monic();
        } while (s.isZERO());
        return new SolvableQuotient<C>(this, r, s, false);
    }


    /**
     * SolvableQuotient random.
     *
     * @param n   such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random quotient element.
     */
    public SolvableQuotient<C> random(int n, Random rnd) {
        GenSolvablePolynomial<C> r = ring.random(n, rnd).monic();
        GenSolvablePolynomial<C> s = ring.random(n, rnd).monic();
        do {
            s = ring.random(n, rnd).monic();
        } while (s.isZERO());
        return new SolvableQuotient<C>(this, r, s, false);
    }


    /**
     * Parse SolvableQuotient from String. Syntax: "{ polynomial | polynomial }"
     * or "{ polynomial }" or " polynomial | polynomial " or " polynomial "
     *
     * @param s String.
     * @return SolvableQuotient from s.
     */
    public SolvableQuotient<C> parse(String s) {
        int i = s.indexOf("{");
        if (i >= 0) {
            s = s.substring(i + 1);
        }
        i = s.lastIndexOf("}");
        if (i >= 0) {
            s = s.substring(0, i);
        }
        i = s.indexOf("|");
        if (i < 0) {
            GenSolvablePolynomial<C> n = ring.parse(s);
            return new SolvableQuotient<C>(this, n);
        }
        String s1 = s.substring(0, i);
        String s2 = s.substring(i + 1);
        GenSolvablePolynomial<C> n = ring.parse(s1);
        GenSolvablePolynomial<C> d = ring.parse(s2);
        return new SolvableQuotient<C>(this, n, d);
    }


    /**
     * Parse SolvableQuotient from Reader.
     *
     * @param r Reader.
     * @return next SolvableQuotient from r.
     */
    public SolvableQuotient<C> parse(Reader r) {
        String s = StringUtil.nextPairedString(r, '{', '}');
        return parse(s);
    }

}
