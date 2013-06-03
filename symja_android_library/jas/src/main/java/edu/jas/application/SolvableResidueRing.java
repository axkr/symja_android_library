/*
 * $Id$
 */

package edu.jas.application;


import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.gb.SolvableGroebnerBaseSeq;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * SolvableResidue ring factory based on GenSolvablePolynomial with RingFactory
 * interface. Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class SolvableResidueRing<C extends GcdRingElem<C>> implements RingFactory<SolvableResidue<C>> {


    private static final Logger logger = Logger.getLogger(SolvableResidueRing.class);


    //private boolean debug = logger.isDebugEnabled();


    /**
     * Groebner base engine.
     */
    protected final SolvableGroebnerBaseAbstract<C> bb;


    /**
     * Solvable polynomial ideal for the reduction.
     */
    public final SolvableIdeal<C> ideal;


    /**
     * Polynomial ring of the factory. Shortcut to ideal.list.ring.
     */
    public final GenSolvablePolynomialRing<C> ring;


    /**
     * Indicator if this ring is a field.
     */
    protected int isField = -1; // initially unknown


    /**
     * The constructor creates a SolvableResidueRing object from an Ideal.
     * @param i polynomial ideal.
     */
    public SolvableResidueRing(SolvableIdeal<C> i) {
        this(i, false);
    }


    /**
     * The constructor creates a SolvableResidueRing object from an
     * SolvableIdeal.
     * @param i solvable polynomial ideal.
     * @param isMaximal true, if ideal is maxmal.
     */
    public SolvableResidueRing(SolvableIdeal<C> i, boolean isMaximal) {
        ideal = i.GB(); // cheap if isGB
        ring = ideal.getRing();
        bb = new SolvableGroebnerBaseSeq<C>();
        if (isMaximal) {
            isField = 1;
            return;
        }
        if (ideal.isONE()) {
            logger.warn("ideal is one, so all residues are 0");
        }
        //System.out.println("rr ring   = " + ring.getClass().getName());
        //System.out.println("rr cofac  = " + ring.coFac.getClass().getName());
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return ideal.commonZeroTest() <= 0 && ring.coFac.isFinite();
    }


    /**
     * Copy SolvableResidue element c.
     * @param c
     * @return a copy of c.
     */
    public SolvableResidue<C> copy(SolvableResidue<C> c) {
        //System.out.println("rr copy in    = " + c.val);
        if (c == null) { // where does this happen?
            return getZERO(); // or null?
        }
        SolvableResidue<C> r = new SolvableResidue<C>(this, c.val);
        //System.out.println("rr copy out   = " + r.val);
        //System.out.println("rr copy ideal = " + ideal.list.list);
        return r; //new SolvableResidue<C>( c.ring, c.val );
    }


    /**
     * Get the zero element.
     * @return 0 as SolvableResidue.
     */
    public SolvableResidue<C> getZERO() {
        return new SolvableResidue<C>(this, ring.getZERO());
    }


    /**
     * Get the one element.
     * @return 1 as SolvableResidue.
     */
    public SolvableResidue<C> getONE() {
        SolvableResidue<C> one = new SolvableResidue<C>(this, ring.getONE());
        if (one.isZERO()) {
            logger.warn("ideal is one, so all residues are 0");
        }
        return one;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<SolvableResidue<C>> generators() {
        List<GenPolynomial<C>> pgens = ring.generators();
        List<SolvableResidue<C>> gens = new ArrayList<SolvableResidue<C>>(pgens.size());
        for (GenPolynomial<C> p : pgens) {
            GenSolvablePolynomial<C> s = (GenSolvablePolynomial<C>) p;
            SolvableResidue<C> r = new SolvableResidue<C>(this, s);
            if ( r.isZERO() ) {
                continue;
            }
            if ( !r.isONE() && r.val.isConstant() ) {
                continue;
            }

            gens.add(r);
        }
        return gens;
    }


    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return ring.isCommutative(); // check also vector space structure
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        return ring.isAssociative(); // sufficient ??
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
        // ideal is (complete) prime or maximal ?
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
     * Get a SolvableResidue element from a BigInteger value.
     * @param a BigInteger.
     * @return a SolvableResidue.
     */
    public SolvableResidue<C> fromInteger(java.math.BigInteger a) {
        return new SolvableResidue<C>(this, ring.fromInteger(a));
    }


    /**
     * Get a SolvableResidue element from a long value.
     * @param a long.
     * @return a SolvableResidue.
     */
    public SolvableResidue<C> fromInteger(long a) {
        return new SolvableResidue<C>(this, ring.fromInteger(a));
    }


    /**
     * Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SolvableResidueRing[ " + ideal.toString() + " ]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return "SRC(" + ideal.list.toScript() + ")";
        //return "SRC(" + ideal.toScript() + "," + ring.toScript()  + ")";
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof SolvableResidueRing)) {
            return false;
        }
        SolvableResidueRing<C> a = null;
        try {
            a = (SolvableResidueRing<C>) b;
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
     * Hash code for this residue ring.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = ideal.hashCode();
        return h;
    }


    /**
     * SolvableResidue random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random residue element.
     */
    public SolvableResidue<C> random(int n) {
        GenSolvablePolynomial<C> x = (GenSolvablePolynomial<C>) ring.random(n).monic();
        return new SolvableResidue<C>(this, x);
    }


    /**
     * Generate a random residum polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @return a random residue polynomial.
     */
    public SolvableResidue<C> random(int k, int l, int d, float q) {
        GenSolvablePolynomial<C> x = (GenSolvablePolynomial<C>) ring.random(k, l, d, q).monic();
        return new SolvableResidue<C>(this, x);
    }


    /**
     * SolvableResidue random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random residue element.
     */
    public SolvableResidue<C> random(int n, Random rnd) {
        GenSolvablePolynomial<C> x = (GenSolvablePolynomial<C>) ring.random(n, rnd).monic();
        return new SolvableResidue<C>(this, x);
    }


    /**
     * Parse SolvableResidue from String.
     * @param s String.
     * @return SolvableResidue from s.
     */
    public SolvableResidue<C> parse(String s) {
        GenSolvablePolynomial<C> x = ring.parse(s);
        return new SolvableResidue<C>(this, x);
    }


    /**
     * Parse SolvableResidue from Reader.
     * @param r Reader.
     * @return next SolvableResidue from r.
     */
    public SolvableResidue<C> parse(Reader r) {
        GenSolvablePolynomial<C> x = ring.parse(r);
        return new SolvableResidue<C>(this, x);
    }

}
