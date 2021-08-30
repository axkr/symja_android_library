/*
 * $Id$
 */

package edu.jas.application;


import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.gbufd.SGBFactory;
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
 * SolvableLocalResidue ring factory for SolvableLocalResidue based on
 * GenSolvablePolynomial with GcdRingElem interface. Objects of this class are
 * immutable. It represents the "classical quotient ring modulo an ideal".
 * @author Heinz Kredel
 */
public class SolvableLocalResidueRing<C extends GcdRingElem<C>> implements
                RingFactory<SolvableLocalResidue<C>>,
                QuotPairFactory<GenPolynomial<C>, SolvableLocalResidue<C>> {


    // Can not extend SolvableLocalRing or SolvableQuotientRing 
    // because of different constructor semantics.


    private static final Logger logger = LogManager.getLogger(SolvableLocalResidueRing.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Solvable polynomial ring of the factory.
     */
    public final GenSolvablePolynomialRing<C> ring;


    /**
     * Solvable polynomial ideal for the reduction.
     */
    public final SolvableIdeal<C> ideal;


    /**
     * Syzygy engine of the factory.
     */
    public final SolvableSyzygyAbstract<C> engine;


    /**
     * Groebner base engine.
     */
    protected final SolvableGroebnerBaseAbstract<C> bb;


    /**
     * Indicator if this ring is a field.
     */
    protected int isField = -1; // initially unknown


    /**
     * The constructor creates a SolvableLocalResidueRing object from a
     * SolvableIdeal.
     * @param i ideal in solvable polynomial ring.
     */
    public SolvableLocalResidueRing(SolvableIdeal<C> i) {
        if (i == null) {
            throw new IllegalArgumentException("ideal may not be null");
        }
        ring = i.getRing();
        ideal = i.GB(); // cheap if isGB
        if (ideal.isONE()) {
            throw new IllegalArgumentException("ideal may not be 1");
        }
        if (ideal.isMaximal()) {
            isField = 1;
            //} else if (ideal.isPrime()) {
            //    isField = 1;
        } else {
            //isField = 0;
            logger.warn("ideal not maximal and not known to be prime");
            //throw new IllegalArgumentException("ideal must be prime or maximal");
        }
        engine = new SolvableSyzygySeq<C>(ring.coFac);
        bb = SGBFactory.getImplementation(ring.coFac); //new SolvableGroebnerBaseSeq<C>();
        logger.debug("solvable local residue ring constructed");
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
    public SolvableLocalResidue<C> create(GenPolynomial<C> n) {
        return new SolvableLocalResidue<C>(this, (GenSolvablePolynomial<C>) n);
    }


    /**
     * Create from numerator, denominator pair.
     */
    @SuppressWarnings("unchecked")
    public SolvableLocalResidue<C> create(GenPolynomial<C> n, GenPolynomial<C> d) {
        return new SolvableLocalResidue<C>(this, (GenSolvablePolynomial<C>) n, (GenSolvablePolynomial<C>) d);
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     */
    public boolean isFinite() {
        return ring.isFinite() && bb.commonZeroTest(ideal.getList()) <= 0;
    }


    /**
     * Copy SolvableLocalResidue element c.
     * @param c
     * @return a copy of c.
     */
    public SolvableLocalResidue<C> copy(SolvableLocalResidue<C> c) {
        return new SolvableLocalResidue<C>(c.ring, c.num, c.den, true);
    }


    /**
     * Get the zero element.
     * @return 0 as SolvableLocalResidue.
     */
    public SolvableLocalResidue<C> getZERO() {
        return new SolvableLocalResidue<C>(this, ring.getZERO());
    }


    /**
     * Get the one element.
     * @return 1 as SolvableLocalResidue.
     */
    public SolvableLocalResidue<C> getONE() {
        return new SolvableLocalResidue<C>(this, ring.getONE());
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     */
    public List<SolvableLocalResidue<C>> generators() {
        List<GenSolvablePolynomial<C>> pgens = PolynomialList.<C> castToSolvableList(ring.generators());
        List<SolvableLocalResidue<C>> gens = new ArrayList<SolvableLocalResidue<C>>(pgens.size() * 2 - 1);
        GenSolvablePolynomial<C> one = ring.getONE();
        for (GenSolvablePolynomial<C> p : pgens) {
            SolvableLocalResidue<C> q = new SolvableLocalResidue<C>(this, p);
            if (!q.isZERO() && !gens.contains(q)) {
                gens.add(q);
                if (!p.isONE() && !ideal.contains(p)) {
                    q = new SolvableLocalResidue<C>(this, one, p);
                    gens.add(q);
                }
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
    @SuppressWarnings("unused")
    public boolean isAssociative() {
        if (!ring.isAssociative()) {
            return false;
        }
        SolvableLocalResidue<C> Xi, Xj, Xk, p, q;
        List<SolvableLocalResidue<C>> gens = generators();
        int ngen = gens.size();
        for (int i = 0; i < ngen; i++) {
            Xi = gens.get(i);
            for (int j = i + 1; j < ngen; j++) {
                Xj = gens.get(j);
                for (int k = j + 1; k < ngen; k++) {
                    Xk = gens.get(k);
                    if (Xi.num.degree() == 0 && Xj.num.degree() == 0 && Xk.num.degree() == 0 &&
                        Xi.den.degree() == 0 && Xj.den.degree() == 0 && Xk.den.degree() == 0) {
                        //System.out.println("lr degree == 0");
                        continue; // skip all base elements
                    }
                    try {
                        p = Xk.multiply(Xj).multiply(Xi);
                        q = Xk.multiply(Xj.multiply(Xi));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        continue; // ignore undefined multiplication
                    }
                    if (p.num.equals(q.num) && p.den.equals(q.den)) { // short cut
                        continue;
                    // } else {
                    //     int s = p.num.length() + q.num.length() + p.den.length() + q.den.length();
                    //     if (s > 5) {
                    //         System.out.println("lr assoc: p = " + p.toScript());
                    //         System.out.println("lr assoc: q = " + q.toScript());
                    //         System.out.println("lr assoc: Xk = " + Xk.toScript() + ", Xj = " + Xj.toScript() + ", Xi = " + Xi.toScript());
                    //         System.out.println("lr size = " + s);
                    //      continue;
                    //     }
                    }
                    if (!p.equals(q)) {
                        if (true || debug) {
                            System.out.println("lr assoc: p = " + p.toScript());
                            System.out.println("lr assoc: q = " + q.toScript());
                            System.out.println("lr assoc: Xk = " + Xk.toScript() + ", Xj = " + Xj.toScript() + ", Xi = " + Xi.toScript());
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
     * Get a SolvableLocalResidue element from a BigInteger value.
     * @param a BigInteger.
     * @return a SolvableLocalResidue.
     */
    public SolvableLocalResidue<C> fromInteger(java.math.BigInteger a) {
        return new SolvableLocalResidue<C>(this, ring.fromInteger(a));
    }


    /**
     * Get a SolvableLocalResidue element from a long value.
     * @param a long.
     * @return a SolvableLocalResidue.
     */
    public SolvableLocalResidue<C> fromInteger(long a) {
        return new SolvableLocalResidue<C>(this, ring.fromInteger(a));
    }


    /**
     * Get the String representation as RingFactory.
     */
    @Override
    public String toString() {
        return "SolvableLocalResidueRing[ " + ideal.toString() + " ]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     */
    @Override
    public String toScript() {
        // Python case
        return "SLR(" + ideal.list.toScript() + ")";
    }


    /**
     * Comparison with any other object.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof SolvableLocalResidueRing)) {
            return false;
        }
        SolvableLocalResidueRing<C> a = null;
        try {
            a = (SolvableLocalResidueRing<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        return ring.equals(a.ring);
    }


    /**
     * Hash code for this quotient ring.
     */
    @Override
    public int hashCode() {
        int h;
        h = ideal.hashCode();
        return h;
    }


    /**
     * SolvableLocalResidue random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random quotient element.
     */
    public SolvableLocalResidue<C> random(int n) {
        GenSolvablePolynomial<C> r = ring.random(n).monic();
        r = ideal.normalform(r);
        GenSolvablePolynomial<C> s;
        do {
            s = ring.random(n).monic();
            s = ideal.normalform(s);
        } while (s.isZERO());
        return new SolvableLocalResidue<C>(this, r, s, false);
    }


    /**
     * Generate a random quotient.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @return a random quotient.
     */
    public SolvableLocalResidue<C> random(int k, int l, int d, float q) {
        GenSolvablePolynomial<C> r = ring.random(k, l, d, q).monic();
        r = ideal.normalform(r);
        GenSolvablePolynomial<C> s;
        do {
            s = ring.random(k, l, d, q).monic();
            s = ideal.normalform(s);
        } while (s.isZERO());
        return new SolvableLocalResidue<C>(this, r, s, false);
    }


    /**
     * SolvableLocalResidue random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random quotient element.
     */
    public SolvableLocalResidue<C> random(int n, Random rnd) {
        GenSolvablePolynomial<C> r = ring.random(n, rnd).monic();
        r = ideal.normalform(r);
        GenSolvablePolynomial<C> s;
        do {
            s = ring.random(n, rnd).monic();
            s = ideal.normalform(s);
        } while (s.isZERO());
        return new SolvableLocalResidue<C>(this, r, s, false);
    }


    /**
     * Parse SolvableLocalResidue from String. Syntax:
     * "{ polynomial | polynomial }" or "{ polynomial }" or
     * " polynomial | polynomial " or " polynomial "
     * @param s String.
     * @return SolvableLocalResidue from s.
     */
    public SolvableLocalResidue<C> parse(String s) {
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
            return new SolvableLocalResidue<C>(this, n);
        }
        String s1 = s.substring(0, i);
        String s2 = s.substring(i + 1);
        GenSolvablePolynomial<C> n = ring.parse(s1);
        GenSolvablePolynomial<C> d = ring.parse(s2);
        return new SolvableLocalResidue<C>(this, n, d);
    }


    /**
     * Parse SolvableLocalResidue from Reader.
     * @param r Reader.
     * @return next SolvableLocalResidue from r.
     */
    public SolvableLocalResidue<C> parse(Reader r) {
        String s = StringUtil.nextPairedString(r, '{', '}');
        return parse(s);
    }

}
