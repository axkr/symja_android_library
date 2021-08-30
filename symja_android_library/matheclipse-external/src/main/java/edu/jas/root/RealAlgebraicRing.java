/*
 * $Id$
 */

package edu.jas.root;


import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Real algebraic number factory class based on AlgebraicNumberRing with
 * RingFactory interface. Objects of this class are immutable with the exception
 * of the isolating intervals.
 * @author Heinz Kredel
 */

public class RealAlgebraicRing<C extends GcdRingElem<C> & Rational>
                /*extends AlgebraicNumberRing<C>*/
                implements RingFactory<RealAlgebraicNumber<C>> {


    /**
     * Representing AlgebraicNumberRing.
     */
    public final AlgebraicNumberRing<C> algebraic;


    /**
     * Isolating interval for a real root. <b>Note: </b> interval may shrink
     * eventually.
     */
    /*package*/Interval<C> root;


    /**
     * Precision of the isolating rectangle for a complex root.
     */
    public static final int PRECISION = BigDecimal.DEFAULT_PRECISION;


    /**
     * Precision of the isolating interval for a real root.
     */
    protected BigRational eps;


    /**
     * Real root computation engine.
     */
    public final RealRootsSturm<C> engine;


    /**
     * The constructor creates a RealAlgebraicNumber factory object from a
     * GenPolynomial objects module.
     * @param m module GenPolynomial<C>.
     * @param root isolating interval for a real root.
     */
    public RealAlgebraicRing(GenPolynomial<C> m, Interval<C> root) {
        algebraic = new AlgebraicNumberRing<C>(m);
        this.root = root;
        engine = new RealRootsSturm<C>();
        if (m.ring.characteristic().signum() > 0) {
            throw new RuntimeException("characteristic not zero");
        }
        BigRational e = new BigRational(10L); //m.ring.coFac.fromInteger(10L);
        e = e.power(-PRECISION / 2); // better not too much for speed
        eps = e; //BigRational.ONE; // initially
    }


    /**
     * The constructor creates a RealAlgebraicNumber factory object from a
     * GenPolynomial objects module.
     * @param m module GenPolynomial.
     * @param root isolating interval for a real root.
     * @param isField indicator if m is prime.
     */
    public RealAlgebraicRing(GenPolynomial<C> m, Interval<C> root, boolean isField) {
        this(m, root);
        setField(isField);
    }


    /**
     * Get the interval for the real root. <b>Note:</b> interval may shrink
     * later.
     * @return real root isolating interval
     */
    public synchronized Interval<C> getRoot() {
        return root;
    }


    /**
     * Set a refined interval for the real root. <b>Note: </b> interval may
     * shrink eventually.
     * @param v interval.
     */
    public synchronized void setRoot(Interval<C> v) {
        assert root.contains(v) : "root contains v";
        this.root = v;
    }


    /**
     * Get the epsilon.
     * @return eps.
     */
    public synchronized BigRational getEps() {
        return this.eps;
    }


    /**
     * Set a new epsilon.
     * @param e epsilon.
     */
    public synchronized void setEps(C e) {
        setEps(e.getRational());
    }


    /**
     * Set a new epsilon.
     * @param e epsilon.
     */
    public synchronized void setEps(BigRational e) {
        this.eps = e; //algebraic.ring.coFac.parse(e.toString());
    }


    /**
     * Refine root.
     */
    public synchronized void refineRoot() {
        refineRoot(eps);
    }


    /**
     * Refine root.
     * @param e epsilon.
     */
    public synchronized void refineRoot(BigRational e) {
        root = engine.refineInterval(root, algebraic.modul, e);
        eps = e;
    }


    /**
     * RealAlgebraicRing half interval.
     */
    public void halfInterval() {
        Interval<C> v = engine.halfInterval(root, algebraic.modul);
        //System.out.println("old v = " + ring.root + ", new v = " + v);
        setRoot(v);
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return algebraic.isFinite();
    }


    /**
     * Copy RealAlgebraicNumber element c.
     * @param c
     * @return a copy of c.
     */
    public RealAlgebraicNumber<C> copy(RealAlgebraicNumber<C> c) {
        return new RealAlgebraicNumber<C>(this, c.number);
    }


    /**
     * Copy this RealAlgebraicRing.
     * @return a copy of this.
     */
    public RealAlgebraicRing<C> copy() {
        if (algebraic.isField()) {
            return new RealAlgebraicRing<C>(algebraic.modul, root, algebraic.isField());
        }
        return new RealAlgebraicRing<C>(algebraic.modul, root);
    }


    /**
     * Get the zero element.
     * @return 0 as RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> getZERO() {
        return new RealAlgebraicNumber<C>(this, algebraic.getZERO());
    }


    /**
     * Get the one element.
     * @return 1 as RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> getONE() {
        return new RealAlgebraicNumber<C>(this, algebraic.getONE());
    }


    /**
     * Get the generating element.
     * @return alpha as RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> getGenerator() {
        return new RealAlgebraicNumber<C>(this, algebraic.getGenerator());
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<RealAlgebraicNumber<C>> generators() {
        List<AlgebraicNumber<C>> agens = algebraic.generators();
        List<RealAlgebraicNumber<C>> gens = new ArrayList<RealAlgebraicNumber<C>>(agens.size());
        for (AlgebraicNumber<C> a : agens) {
            gens.add(getZERO().sum(a.getVal()));
        }
        return gens;
    }


    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return algebraic.isCommutative();
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        return algebraic.isAssociative();
    }


    /**
     * Query if this ring is a field.
     * @return true if algebraic is prime, else false.
     */
    public boolean isField() {
        return algebraic.isField();
    }


    /**
     * Assert that this ring is a field.
     * @param isField true if this ring is a field, else false.
     */
    public void setField(boolean isField) {
        algebraic.setField(isField);
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return algebraic.characteristic();
    }


    /**
     * Get a RealAlgebraicNumber element from a BigInteger value.
     * @param a BigInteger.
     * @return a RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> fromInteger(java.math.BigInteger a) {
        return new RealAlgebraicNumber<C>(this, algebraic.fromInteger(a));
    }


    /**
     * Get a RealAlgebraicNumber element from a BigRational value.
     * @param a BigRational.
     * @return a RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> fromRational(BigRational a) {
        return new RealAlgebraicNumber<C>(this, algebraic.parse(a.toString()));
    }


    /**
     * Get a RealAlgebraicNumber element from a long value.
     * @param a long.
     * @return a RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> fromInteger(long a) {
        return new RealAlgebraicNumber<C>(this, algebraic.fromInteger(a));
    }


    /**
     * Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RealAlgebraicRing[ " + algebraic.modul.toString() + " in " + root + " | isField="
                        + algebraic.isField() + " :: " + algebraic.ring.toString() + " ]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return "RealN( " + algebraic.modul.toScript() + ", " + root.toScript()
        //+ ", " + algebraic.isField() 
        //+ ", " + algebraic.ring.toScript() 
                        + " )";
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (b == null) {
            return false;
        }
        if (!(b instanceof RealAlgebraicRing)) {
            return false;
        }
        RealAlgebraicRing<C> a = (RealAlgebraicRing<C>) b;
        return algebraic.equals(a.algebraic) && root.equals(a.root);
    }


    /**
     * Hash code for this RealAlgebraicNumber.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * algebraic.hashCode() + root.hashCode();
    }


    /**
     * RealAlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random integer mod modul.
     */
    public RealAlgebraicNumber<C> random(int n) {
        return new RealAlgebraicNumber<C>(this, algebraic.random(n));
    }


    /**
     * RealAlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random integer mod modul.
     */
    public RealAlgebraicNumber<C> random(int n, Random rnd) {
        return new RealAlgebraicNumber<C>(this, algebraic.random(n, rnd));
    }


    /**
     * Parse RealAlgebraicNumber from String.
     * @param s String.
     * @return RealAlgebraicNumber from s.
     */
    public RealAlgebraicNumber<C> parse(String s) {
        return new RealAlgebraicNumber<C>(this, algebraic.parse(s));
    }


    /**
     * Parse RealAlgebraicNumber from Reader.
     * @param r Reader.
     * @return next RealAlgebraicNumber from r.
     */
    public RealAlgebraicNumber<C> parse(Reader r) {
        return new RealAlgebraicNumber<C>(this, algebraic.parse(r));
    }

}
