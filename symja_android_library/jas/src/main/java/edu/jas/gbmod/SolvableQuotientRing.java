/*
 * $Id$
 */

package edu.jas.gbmod;


import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import edu.jas.kern.StringUtil;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.application.SolvableIdeal;


/**
 * SolvableQuotient ring factory based on GenPolynomial with RingElem interface. Objects
 * of this class are immutable.
 * @author Heinz Kredel
 */
public class SolvableQuotientRing<C extends GcdRingElem<C>> implements RingFactory<SolvableQuotient<C>> {


    private static final Logger logger = Logger.getLogger(SolvableQuotientRing.class);


    private boolean debug = logger.isDebugEnabled();


    /**
     * Solvable polynomial ring of the factory.
     */
    public final GenSolvablePolynomialRing<C> ring;


    /**
     * Syzygy engine of the factory.
     */
    public final SolvableSyzygyAbstract<C> engine;


    /**
     * The constructor creates a SolvableQuotientRing object from a GenSolvablePolynomialRing.
     * @param r solvable polynomial ring.
     */
    public SolvableQuotientRing(GenSolvablePolynomialRing<C> r) {
        ring = r;
        engine = new SolvableSyzygyAbstract<C>();
        logger.debug("quotient ring constructed");
    }


    /**
     * Least common multiple. 
     * @param n first solvable polynomial.
     * @param d second solvable polynomial.
     * @return lcm(n,d)
     */
    protected GenSolvablePolynomial<C> syzLcm(GenSolvablePolynomial<C> n, 
                                              GenSolvablePolynomial<C> d) {
        List<GenSolvablePolynomial<C>> list = new ArrayList<GenSolvablePolynomial<C>>(1);
        list.add(n);
        SolvableIdeal<C> N = new SolvableIdeal<C>(n.ring, list, true);
        list = new ArrayList<GenSolvablePolynomial<C>>(1);
        list.add(d);
        SolvableIdeal<C> D = new SolvableIdeal<C>(n.ring, list, true);
        SolvableIdeal<C> L = N.intersect(D);
        if (L.getList().size() != 1) {
            throw new RuntimeException("lcm not uniqe");
        }
        GenSolvablePolynomial<C> lcm = L.getList().get(0);
        return lcm;
    }


    /**
     * Greatest common divisor. 
     * @param n first solvable polynomial.
     * @param d second solvable polynomial.
     * @return gcd(n,d)
     */
    protected GenSolvablePolynomial<C> syzGcd(GenSolvablePolynomial<C> n, 
                                              GenSolvablePolynomial<C> d) {
        if (n.isZERO()) {
            return d;
        }
        if (d.isZERO()) {
            return n;
        }
        if (n.isONE()) {
            return n;
        }
        if (d.isONE()) {
            return d;
        }
        GenSolvablePolynomial<C> p = n.multiply(d);
        GenSolvablePolynomial<C> lcm = syzLcm(n, d);
        // divide
        List<GenSolvablePolynomial<C>> Q = new ArrayList<GenSolvablePolynomial<C>>(1);
        Q.add(ring.getZERO());
        List<GenSolvablePolynomial<C>> V = new ArrayList<GenSolvablePolynomial<C>>(1);
        V.add(lcm);
        GenSolvablePolynomial<C> x = engine.sred.leftNormalform(Q,V, p);
        GenSolvablePolynomial<C> y = Q.get(0);
        // GenSolvablePolynomial<C> gcd = divide(p, lcm);
        return y;
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return ring.isFinite();
    }


    /**
     * Copy SolvableQuotient element c.
     * @param c
     * @return a copy of c.
     */
    public SolvableQuotient<C> copy(SolvableQuotient<C> c) {
        return new SolvableQuotient<C>(c.ring, c.num, c.den, true);
    }


    /**
     * Get the zero element.
     * @return 0 as SolvableQuotient.
     */
    public SolvableQuotient<C> getZERO() {
        return new SolvableQuotient<C>(this, ring.getZERO());
    }


    /**
     * Get the one element.
     * @return 1 as SolvableQuotient.
     */
    public SolvableQuotient<C> getONE() {
        return new SolvableQuotient<C>(this, ring.getONE());
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<SolvableQuotient<C>> generators() {
        List<GenSolvablePolynomial<C>> pgens = PolynomialList.<C> castToSolvableList(ring.generators());
        List<SolvableQuotient<C>> gens = new ArrayList<SolvableQuotient<C>>(pgens.size()*2-1);
        GenSolvablePolynomial<C> one = ring.getONE();
        for (GenSolvablePolynomial<C> p : pgens) {
            SolvableQuotient<C> q = new SolvableQuotient<C>(this, p);
            gens.add(q);
            if ( !p.isONE() ) {
                q = new SolvableQuotient<C>(this, one, p);
                gens.add(q);
            }
        }
        return gens;
    }


    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return ring.isCommutative();
    }


    /**
     * Query if this ring is associative.
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
            Xi = (SolvableQuotient<C>) gens.get(i);
            for (int j = i + 1; j < ngen; j++) {
                Xj = (SolvableQuotient<C>) gens.get(j);
                for (int k = j + 1; k < ngen; k++) {
                    Xk = (SolvableQuotient<C>) gens.get(k);
                    p = Xk.multiply(Xj).multiply(Xi);
                    q = Xk.multiply(Xj.multiply(Xi));
                    if (!p.equals(q)) {
                        if (true || debug) {
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
     * @return true.
     */
    public boolean isField() {
        return true;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return ring.characteristic();
    }


    /**
     * Get a SolvableQuotient element from a BigInteger value.
     * @param a BigInteger.
     * @return a SolvableQuotient.
     */
    public SolvableQuotient<C> fromInteger(java.math.BigInteger a) {
        return new SolvableQuotient<C>(this, ring.fromInteger(a));
    }


    /**
     * Get a SolvableQuotient element from a long value.
     * @param a long.
     * @return a SolvableQuotient.
     */
    public SolvableQuotient<C> fromInteger(long a) {
        return new SolvableQuotient<C>(this, ring.fromInteger(a));
    }


    /**
     * Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
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
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return "SRF(" + ring.toScript() + ")";
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof SolvableQuotientRing)) {
            return false;
        }
        SolvableQuotientRing<C> a = null;
        try {
            a = (SolvableQuotientRing<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        return ring.equals(a.ring);
    }


    /**
     * Hash code for this quotient ring.
     * @see java.lang.Object#hashCode()
     */
    @Override
        public int hashCode() {
        int h;
        h = ring.hashCode();
        return h;
    }


    /**
     * SolvableQuotient random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random quotient element.
     */
    public SolvableQuotient<C> random(int n) {
        GenSolvablePolynomial<C> r = (GenSolvablePolynomial<C>) ring.random(n).monic();
        GenSolvablePolynomial<C> s = (GenSolvablePolynomial<C>) ring.random(n).monic();
        while (s.isZERO()) {
            s = (GenSolvablePolynomial<C>) ring.random(n).monic();
        }
        return new SolvableQuotient<C>(this, r, s, false);
    }


    /**
     * Generate a random quotient.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @return a random quotient.
     */
    public SolvableQuotient<C> random(int k, int l, int d, float q) {
        GenSolvablePolynomial<C> r = (GenSolvablePolynomial<C>) ring.random(k, l, d, q).monic();
        GenSolvablePolynomial<C> s = (GenSolvablePolynomial<C>) ring.random(k, l, d, q).monic();
        while (s.isZERO()) {
            s = (GenSolvablePolynomial<C>) ring.random(k, l, d, q).monic();
        }
        return new SolvableQuotient<C>(this, r, s, false);
    }


    /**
     * SolvableQuotient random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random quotient element.
     */
    public SolvableQuotient<C> random(int n, Random rnd) {
        GenSolvablePolynomial<C> r = (GenSolvablePolynomial<C>) ring.random(n, rnd).monic();
        GenSolvablePolynomial<C> s = (GenSolvablePolynomial<C>) ring.random(n, rnd).monic();
        while (s.isZERO()) {
            s = (GenSolvablePolynomial<C>) ring.random(n, rnd).monic();
        }
        return new SolvableQuotient<C>(this, r, s, false);
    }


    /**
     * Parse SolvableQuotient from String. Syntax: "{ polynomial | polynomial }" or
     * "{ polynomial }" or " polynomial | polynomial " or " polynomial "
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
     * @param r Reader.
     * @return next SolvableQuotient from r.
     */
    public SolvableQuotient<C> parse(Reader r) {
        String s = StringUtil.nextPairedString(r, '{', '}');
        return parse(s);
    }

}
