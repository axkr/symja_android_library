/*
 * $Id$
 */

package edu.jas.application;


import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.kern.StringUtil;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.QuotPairFactory;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;


/**
 * Local ring class based on GenPolynomial with RingElem interface. Objects of
 * this class are effective immutable.
 * @author Heinz Kredel
 */
public class LocalRing<C extends GcdRingElem<C>>
                implements RingFactory<Local<C>>, QuotPairFactory<GenPolynomial<C>, Local<C>> {


    private static final Logger logger = LogManager.getLogger(LocalRing.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Greatest common divisor engine for coefficient content and primitive
     * parts.
     */
    protected final GreatestCommonDivisor<C> engine;


    /**
     * Polynomial ideal for localization.
     */
    public final Ideal<C> ideal;


    /**
     * Polynomial ring of the factory.
     */
    public final GenPolynomialRing<C> ring;


    /**
     * Indicator if this ring is a field.
     */
    protected int isField = -1; // initially unknown


    /**
     * The constructor creates a LocalRing object from an Ideal.
     * @param i localization polynomial ideal.
     */
    public LocalRing(Ideal<C> i) {
        if (i == null) {
            throw new IllegalArgumentException("ideal may not be null");
        }
        ideal = i.GB(); // cheap if isGB
        if (ideal.isONE()) {
            throw new IllegalArgumentException("ideal may not be 1");
        }
        if (ideal.isMaximal()) {
            isField = 1;
        } else {
            isField = 0;
            logger.warn("ideal not maximal");
            //throw new IllegalArgumentException("ideal must be maximal");
        }
        ring = ideal.list.ring;
        //engine = GCDFactory.<C>getImplementation( ring.coFac );
        engine = GCDFactory.<C> getProxy(ring.coFac);
    }


    /**
     * Factory for base elements.
     */
    public GenPolynomialRing<C> pairFactory() {
        return ring;
    }


    /**
     * Create from numerator.
     */
    public Local<C> create(GenPolynomial<C> n) {
        return new Local<C>(this, n);
    }


    /**
     * Create from numerator, denominator pair.
     */
    public Local<C> create(GenPolynomial<C> n, GenPolynomial<C> d) {
        return new Local<C>(this, n, d);
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return ring.isFinite() && ideal.bb.commonZeroTest(ideal.getList()) <= 0;
    }


    /**
     * Copy Local element c.
     * @param c
     * @return a copy of c.
     */
    public Local<C> copy(Local<C> c) {
        return new Local<C>(c.ring, c.num, c.den, true);
    }


    /**
     * Get the zero element.
     * @return 0 as Local.
     */
    public Local<C> getZERO() {
        return new Local<C>(this, ring.getZERO());
    }


    /**
     * Get the one element.
     * @return 1 as Local.
     */
    public Local<C> getONE() {
        return new Local<C>(this, ring.getONE());
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<Local<C>> generators() {
        List<GenPolynomial<C>> pgens = ring.generators();
        List<Local<C>> gens = new ArrayList<Local<C>>(pgens.size());
        GenPolynomial<C> one = ring.getONE();
        for (GenPolynomial<C> p : pgens) {
            Local<C> q = new Local<C>(this, p);
            gens.add(q);
            if (!p.isONE() && !ideal.contains(p)) { // q.isUnit()
                q = new Local<C>(this, one, p);
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
        return ring.isAssociative();
    }


    /**
     * Query if this ring is a field.
     * @return false.
     */
    public boolean isField() {
        if (isField > 0) {
            return true;
        }
        if (isField == 0) {
            return false;
        }
        // not reached
        return false;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return ring.characteristic();
    }


    /**
     * Get a Local element from a BigInteger value.
     * @param a BigInteger.
     * @return a Local.
     */
    public Local<C> fromInteger(java.math.BigInteger a) {
        return new Local<C>(this, ring.fromInteger(a));
    }


    /**
     * Get a Local element from a long value.
     * @param a long.
     * @return a Local.
     */
    public Local<C> fromInteger(long a) {
        return new Local<C>(this, ring.fromInteger(a));
    }


    /**
     * Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LocalRing[ " + ideal.toString() + " ]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return "LC(" + ideal.list.toScript() + ")";
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    // not jet working
    public boolean equals(Object b) {
        if (!(b instanceof LocalRing)) {
            return false;
        }
        LocalRing<C> a = null;
        try {
            a = (LocalRing<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        if (!ring.equals(a.ring)) {
            return false;
        }
        return ideal.equals(a.ideal);
    }


    /**
     * Hash code for this local ring.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = ideal.hashCode();
        return h;
    }


    /**
     * Local random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random residue element.
     */
    public Local<C> random(int n) {
        GenPolynomial<C> r = ring.random(n).monic();
        r = ideal.normalform(r);
        GenPolynomial<C> s;
        do {
            s = ring.random(n).monic();
            s = ideal.normalform(s);
        } while (s.isZERO());
        return new Local<C>(this, r, s, false);
    }


    /**
     * Generate a random residum polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @return a random residue polynomial.
     */
    public Local<C> random(int k, int l, int d, float q) {
        GenPolynomial<C> r = ring.random(k, l, d, q).monic();
        r = ideal.normalform(r);
        GenPolynomial<C> s;
        do {
            s = ring.random(k, l, d, q).monic();
            s = ideal.normalform(s);
        } while (s.isZERO());
        return new Local<C>(this, r, s, false);
    }


    /**
     * Local random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random residue element.
     */
    public Local<C> random(int n, Random rnd) {
        GenPolynomial<C> r = ring.random(n, rnd).monic();
        r = ideal.normalform(r);
        GenPolynomial<C> s;
        do {
            s = ring.random(n).monic();
            s = ideal.normalform(s);
        } while (s.isZERO());
        return new Local<C>(this, r, s, false);
    }


    /**
     * Parse Local from String.
     * @param s String.
     * @return Local from s.
     */
    public Local<C> parse(String s) {
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
            GenPolynomial<C> n = ring.parse(s);
            return new Local<C>(this, n);
        }
        String s1 = s.substring(0, i);
        String s2 = s.substring(i + 1);
        GenPolynomial<C> n = ring.parse(s1);
        GenPolynomial<C> d = ring.parse(s2);
        return new Local<C>(this, n, d);
    }


    /**
     * Parse Local from Reader.
     * @param r Reader.
     * @return next Local from r.
     */
    public Local<C> parse(Reader r) {
        String s = StringUtil.nextPairedString(r, '{', '}');
        return parse(s);
    }

}
