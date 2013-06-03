/*
 * $Id$
 */

package edu.jas.application;


import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import edu.jas.kern.Scripting;
import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.root.RealRootTuple;
import edu.jas.root.PolyUtilRoot;
import edu.jas.root.Interval;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;


/**
 * Real algebraic number factory class based on bi-variate real algebraic
 * numbers. Objects of this class are immutable with the exception of the
 * isolating intervals. Bi-variate ideal implementation is in version 3614
 * 2011-04-28 09:20:34Z.
 * @author Heinz Kredel
 */

public class RealAlgebraicRing<C extends GcdRingElem<C> & Rational> implements
                RingFactory<RealAlgebraicNumber<C>> {


    /**
     * Representing ideal with univariate polynomials IdealWithUniv.
     */
    /*package*/final IdealWithUniv<C> univs;


    /**
     * Representing ResidueRing.
     */
    /*package*/final ResidueRing<C> algebraic;


    /**
     * Isolating intervals for the real algebraic roots of the real and
     * imaginary part. <b>Note: </b> intervals may shrink eventually.
     */
    /*package*/RealRootTuple<C> root;


    /**
     * Recursive real root ring.
     */
    public final edu.jas.root.RealAlgebraicRing<edu.jas.root.RealAlgebraicNumber<C>> realRing;


    /**
     * Epsilon of the isolating rectangle for a complex root.
     */
    protected C eps;


    /**
     * Precision of the isolating rectangle for a complex root.
     */
    public final int PRECISION = 9; //BigDecimal.DEFAULT_PRECISION;


    private static final Logger logger = Logger.getLogger(RealAlgebraicRing.class);


    /**
     * The constructor creates a RealAlgebraicNumber factory object from a
     * IdealWithUniv, ResidueRing and a root tuple.
     * @param m module IdealWithUniv&lt;C&gt;.
     * @param a module ResidueRing&lt;C&gt;.
     * @param r isolating rectangle for a complex root.
     */
    public RealAlgebraicRing(IdealWithUniv<C> m, ResidueRing<C> a, RealRootTuple<C> r) {
        univs = m;
        algebraic = a;
        root = r;
        if (algebraic.characteristic().signum() > 0) {
            throw new IllegalArgumentException("characteristic not zero");
        }
        C e = m.ideal.list.ring.coFac.fromInteger(10L);
        e = e.inverse();
        e = Power.positivePower(e, PRECISION);
        eps = e;
        edu.jas.root.RealAlgebraicRing<C> rfac1 = root.tuple.get(0).factory();
        edu.jas.root.RealAlgebraicRing<C> rfac2 = root.tuple.get(1).factory();
        GenPolynomial<C> p0 = PolyUtil.<C> selectWithVariable(univs.ideal.list.list, 0);
        if (p0 == null) {
            throw new RuntimeException("no polynomial found in " + (0) + " of  " + univs.ideal);
        }
        //System.out.println("realRing, pol = " + p0.toScript());
        GenPolynomialRing<C> pfac = p0.ring;
        GenPolynomialRing<GenPolynomial<C>> prfac = pfac.recursive(1);
        //System.out.println("prfac = " + prfac);
        GenPolynomial<GenPolynomial<C>> p0r = PolyUtil.<C> recursive(prfac,p0);
        GenPolynomialRing<edu.jas.root.RealAlgebraicNumber<C>> parfac 
            = new GenPolynomialRing<edu.jas.root.RealAlgebraicNumber<C>>(rfac1,prfac);
        GenPolynomial<edu.jas.root.RealAlgebraicNumber<C>> p0ar 
           = PolyUtilRoot.<C> convertRecursiveToAlgebraicCoefficients(parfac,p0r);
        Interval<C> r2 = rfac2.getRoot();
        edu.jas.root.RealAlgebraicNumber<C> rleft = rfac1.getZERO().sum(r2.left);
        edu.jas.root.RealAlgebraicNumber<C> rright = rfac1.getZERO().sum(r2.right);
        Interval<edu.jas.root.RealAlgebraicNumber<C>> r2r = new Interval<edu.jas.root.RealAlgebraicNumber<C>>(rleft,rright);
        edu.jas.root.RealAlgebraicRing<edu.jas.root.RealAlgebraicNumber<C>> rr 
            = new edu.jas.root.RealAlgebraicRing<edu.jas.root.RealAlgebraicNumber<C>>(p0ar,r2r); 
        logger.info("realRing = " + rr);
        realRing = rr;
    }


    /**
     * The constructor creates a RealAlgebraicNumber factory object from a
     * IdealWithUniv and a root tuple.
     * @param m module IdealWithUniv&lt;C&gt;.
     * @param root isolating rectangle for a complex root.
     */
    public RealAlgebraicRing(IdealWithUniv<C> m, RealRootTuple<C> root) {
        this(m, new ResidueRing<C>(m.ideal), root);
    }


    /**
     * The constructor creates a RealAlgebraicNumber factory object from a
     * IdealWithUniv and a root tuple.
     * @param m module IdealWithUniv&lt;C&gt;.
     * @param root isolating rectangle for a complex root.
     * @param isField indicator if m is maximal.
     */
    public RealAlgebraicRing(IdealWithUniv<C> m, RealRootTuple<C> root, boolean isField) {
        this(m, new ResidueRing<C>(m.ideal, isField), root);
    }


    /**
     * Set a refined rectangle for the complex root. <b>Note: </b> rectangle may
     * shrink eventually.
     * @param v rectangle.
     */
    public synchronized void setRoot(RealRootTuple<C> v) {
        // assert v is contained in root
        this.root = v;
    }


    /**
     * Get rectangle for the complex root.
     * @return v rectangle.
     */
    public synchronized RealRootTuple<C> getRoot() {
        return this.root;
    }


    /**
     * Get epsilon.
     * @return epsilon.
     */
    public synchronized C getEps() {
        return this.eps;
    }


    /**
     * Set a new epsilon.
     * @param e epsilon.
     */
    public synchronized void setEps(C e) {
        this.eps = e;
    }


    /**
     * Set a new epsilon.
     * @param e epsilon.
     */
    public synchronized void setEps(BigRational e) {
        edu.jas.root.RealAlgebraicRing<C> r = (edu.jas.root.RealAlgebraicRing<C>) realRing.algebraic.ring.coFac;
        this.eps = r.algebraic.ring.coFac.parse(e.toString());
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return realRing.isFinite();
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
     * Get the zero element.
     * @return 0 as RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> getZERO() {
        return new RealAlgebraicNumber<C>(this, realRing.getZERO());
    }


    /**
     * Get the one element.
     * @return 1 as RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> getONE() {
        return new RealAlgebraicNumber<C>(this, realRing.getONE());
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<RealAlgebraicNumber<C>> generators() {
        List<edu.jas.root.RealAlgebraicNumber<edu.jas.root.RealAlgebraicNumber<C>>> agens = realRing
                        .generators();
        List<RealAlgebraicNumber<C>> gens = new ArrayList<RealAlgebraicNumber<C>>(agens.size());
        for (edu.jas.root.RealAlgebraicNumber<edu.jas.root.RealAlgebraicNumber<C>> a : agens) {
            gens.add(getZERO().sum(a));
        }
        return gens;
    }


    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return realRing.isCommutative();
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        return realRing.isAssociative();
    }


    /**
     * Query if this ring is a field.
     * @return true if algebraic is prime, else false.
     */
    public boolean isField() {
        return realRing.isField();
    }


    /**
     * Assert that this ring is a field.
     * @param isField true if this ring is a field, else false.
     */
    public void setField(boolean isField) {
        realRing.setField(isField);
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return realRing.characteristic();
    }


    /**
     * Get a RealAlgebraicNumber element from a BigInteger value.
     * @param a BigInteger.
     * @return a RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> fromInteger(java.math.BigInteger a) {
        return new RealAlgebraicNumber<C>(this, realRing.fromInteger(a));
    }


    /**
     * Get a RealAlgebraicNumber element from a long value.
     * @param a long.
     * @return a RealAlgebraicNumber.
     */
    public RealAlgebraicNumber<C> fromInteger(long a) {
        return new RealAlgebraicNumber<C>(this, realRing.fromInteger(a));
    }


    /**
     * Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RealAlgebraicRing[ " + realRing.toString() + " in " + root + " | isField="
                        + realRing.isField() + ", algebraic.ideal=" + algebraic.ideal.toString() + " ]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        return "RealRecN( " + realRing.toScript() + ", " + root.toScript()
        //+ ", " + realRing.isField() 
        //+ ", " + realRing.ring.toScript() 
                        + " )";
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    // not jet working
    public boolean equals(Object b) {
        if (!(b instanceof RealAlgebraicRing)) {
            return false;
        }
        RealAlgebraicRing<C> a = null;
        try {
            a = (RealAlgebraicRing<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        return realRing.equals(a.realRing) && root.equals(a.root);
    }


    /**
     * Hash code for this RealAlgebraicNumber.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * realRing.hashCode() + root.hashCode();
    }


    /**
     * RealAlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random integer mod modul.
     */
    public RealAlgebraicNumber<C> random(int n) {
        return new RealAlgebraicNumber<C>(this, realRing.random(n));
    }


    /**
     * RealAlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random integer mod modul.
     */
    public RealAlgebraicNumber<C> random(int n, Random rnd) {
        return new RealAlgebraicNumber<C>(this, realRing.random(n, rnd));
    }


    /**
     * Parse RealAlgebraicNumber from String.
     * @param s String.
     * @return RealAlgebraicNumber from s.
     */
    public RealAlgebraicNumber<C> parse(String s) {
        return new RealAlgebraicNumber<C>(this, realRing.parse(s));
    }


    /**
     * Parse RealAlgebraicNumber from Reader.
     * @param r Reader.
     * @return next RealAlgebraicNumber from r.
     */
    public RealAlgebraicNumber<C> parse(Reader r) {
        return new RealAlgebraicNumber<C>(this, realRing.parse(r));
    }

}
