/*
 * $Id$
 */

package edu.jas.application;


import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.gb.WordGroebnerBaseAbstract;
import edu.jas.gb.WordGroebnerBaseSeq;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.QuotPairFactory;
import edu.jas.structure.RingFactory;
import edu.jas.structure.ValueFactory;


/**
 * WordResidue ring factory based on GenWordPolynomialRing with GcdRingFactory
 * interface. Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class WordResidueRing<C extends GcdRingElem<C>> implements RingFactory<WordResidue<C>>,
                QuotPairFactory<GenWordPolynomial<C>, WordResidue<C>>,
                ValueFactory<GenWordPolynomial<C>, WordResidue<C>> {


    private static final Logger logger = LogManager.getLogger(WordResidueRing.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Groebner base engine.
     */
    protected final WordGroebnerBaseAbstract<C> bb;


    /**
     * Word polynomial ideal for the reduction.
     */
    public final WordIdeal<C> ideal;


    /**
     * Polynomial ring of the factory. Shortcut to ideal.list.ring.
     */
    public final GenWordPolynomialRing<C> ring;


    /**
     * Indicator if this ring is a field.
     */
    protected int isField = -1; // initially unknown


    /**
     * The constructor creates a WordResidueRing object from an Ideal.
     * @param i polynomial ideal.
     */
    public WordResidueRing(WordIdeal<C> i) {
        this(i, false);
    }


    /**
     * The constructor creates a WordResidueRing object from an WordIdeal.
     * @param i solvable polynomial ideal.
     * @param isMaximal true, if ideal is maxmal.
     */
    public WordResidueRing(WordIdeal<C> i, boolean isMaximal) {
        //System.out.println("i    = " + i);
        ideal = i.GB(); // cheap if isGB
        //System.out.println("i.GB = " + ideal);
        ring = ideal.getRing();
        bb = new WordGroebnerBaseSeq<C>();
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
     * Factory for base elements.
     */
    public GenWordPolynomialRing<C> pairFactory() {
        return ring;
    }


    /**
     * Factory for base elements.
     */
    public GenWordPolynomialRing<C> valueFactory() {
        return ring;
    }


    /**
     * Create from numerator.
     */
    public WordResidue<C> create(GenWordPolynomial<C> n) {
        return new WordResidue<C>(this, n);
    }


    /**
     * Create from numerator, denominator pair.
     */
    public WordResidue<C> create(GenWordPolynomial<C> n, GenWordPolynomial<C> d) {
        if (d != null && !d.isONE()) {
            throw new UnsupportedOperationException("d must be 1, but d = " + d);
        }
        return new WordResidue<C>(this, n);
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
     * Copy WordResidue element c.
     * @param c
     * @return a copy of c.
     */
    public WordResidue<C> copy(WordResidue<C> c) {
        //System.out.println("rr copy in    = " + c.val);
        if (c == null) { // where does this happen?
            return getZERO(); // or null?
        }
        WordResidue<C> r = new WordResidue<C>(this, c.val);
        //System.out.println("rr copy out   = " + r.val);
        //System.out.println("rr copy ideal = " + ideal.list.list);
        return r; //new WordResidue<C>( c.ring, c.val );
    }


    /**
     * Get the zero element.
     * @return 0 as WordResidue.
     */
    public WordResidue<C> getZERO() {
        return new WordResidue<C>(this, ring.getZERO());
    }


    /**
     * Get the one element.
     * @return 1 as WordResidue.
     */
    public WordResidue<C> getONE() {
        WordResidue<C> one = new WordResidue<C>(this, ring.getONE());
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
    public List<WordResidue<C>> generators() {
        List<GenWordPolynomial<C>> pgens = ring.generators();
        List<WordResidue<C>> gens = new ArrayList<WordResidue<C>>(pgens.size());
        SortedSet<WordResidue<C>> sgens = new TreeSet<WordResidue<C>>();
        List<GenWordPolynomial<C>> rgens = new ArrayList<GenWordPolynomial<C>>(pgens.size());
        WordIdeal<C> gi = new WordIdeal<C>(ring, rgens);
        WordResidueRing<C> gr = new WordResidueRing<C>(gi);
        for (GenWordPolynomial<C> s : pgens) {
            WordResidue<C> r = new WordResidue<C>(this, s);
            if (r.isZERO()) {
                continue;
            }
            if (!r.isONE() && r.val.isConstant()) {
                continue;
            }
            // avoid duplicate generators with sgens
            WordResidue<C> x = new WordResidue<C>(gr, r.val);
            if (x.isZERO()) {
                continue;
            }
            if (!x.isONE() && x.val.isConstant()) {
                continue;
            }
            r = new WordResidue<C>(this, x.val);
            if (r.isZERO()) {
                continue;
            }
            r = r.monic();
            if (!r.isONE() && !r.val.isConstant()) {
                rgens.add(r.val);
                //System.out.println("rgens = " + rgens);
                gi = new WordIdeal<C>(ring, rgens);
                gr = new WordResidueRing<C>(gi);
            }
            //gens.add(r);
            sgens.add(r);
        }
        gens.addAll(sgens);
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
        if (ideal.isMaximal()) {
            isField = 1;
            return true;
        }
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
     * Get a WordResidue element from a BigInteger value.
     * @param a BigInteger.
     * @return a WordResidue.
     */
    public WordResidue<C> fromInteger(java.math.BigInteger a) {
        return new WordResidue<C>(this, ring.fromInteger(a));
    }


    /**
     * Get a WordResidue element from a long value.
     * @param a long.
     * @return a WordResidue.
     */
    public WordResidue<C> fromInteger(long a) {
        return new WordResidue<C>(this, ring.fromInteger(a));
    }


    /**
     * Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "WordResidueRing[ " + ideal.toString() + " ]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return "WRC(" + ideal.toScript() + ")";
        //return "WRC(" + ideal.toScript() + "," + ring.toScript()  + ")";
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof WordResidueRing)) {
            return false;
        }
        WordResidueRing<C> a = null;
        try {
            a = (WordResidueRing<C>) b;
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
     * WordResidue random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random residue element.
     */
    public WordResidue<C> random(int n) {
        GenWordPolynomial<C> x = ring.random(n).monic();
        return new WordResidue<C>(this, x);
    }


    /**
     * Generate a random residum polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @return a random residue polynomial.
     */
    public WordResidue<C> random(int k, int l, int d) {
        GenWordPolynomial<C> x = ring.random(k, l, d).monic();
        return new WordResidue<C>(this, x);
    }


    /**
     * WordResidue random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random residue element.
     */
    public WordResidue<C> random(int n, Random rnd) {
        GenWordPolynomial<C> x = ring.random(n, rnd).monic();
        return new WordResidue<C>(this, x);
    }


    /**
     * Parse WordResidue from String.
     * @param s String.
     * @return WordResidue from s.
     */
    public WordResidue<C> parse(String s) {
        GenWordPolynomial<C> x = ring.parse(s);
        return new WordResidue<C>(this, x);
    }


    /**
     * Parse WordResidue from Reader.
     * @param r Reader.
     * @return next WordResidue from r.
     */
    public WordResidue<C> parse(Reader r) {
        GenWordPolynomial<C> x = ring.parse(r);
        return new WordResidue<C>(this, x);
    }

}
