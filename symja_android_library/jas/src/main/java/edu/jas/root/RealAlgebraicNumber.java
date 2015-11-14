/*
 * $Id$
 */

package edu.jas.root;


import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.kern.PrettyPrint;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.NotInvertibleException;


/**
 * Real algebraic number class based on AlgebraicNumber. Objects of this class
 * are immutable.
 * @author Heinz Kredel
 */

public class RealAlgebraicNumber<C extends GcdRingElem<C> & Rational>
/*extends AlgebraicNumber<C>*/
implements GcdRingElem<RealAlgebraicNumber<C>>, Rational {


    /**
     * Representing AlgebraicNumber.
     */
    public final AlgebraicNumber<C> number;


    /**
     * Ring part of the data structure.
     */
    public final RealAlgebraicRing<C> ring;


    /**
     * The constructor creates a RealAlgebraicNumber object from
     * RealAlgebraicRing modul and a GenPolynomial value.
     * @param r ring RealAlgebraicRing<C>.
     * @param a value GenPolynomial<C>.
     */
    public RealAlgebraicNumber(RealAlgebraicRing<C> r, GenPolynomial<C> a) {
        number = new AlgebraicNumber<C>(r.algebraic, a);
        ring = r;
    }


    /**
     * The constructor creates a RealAlgebraicNumber object from
     * RealAlgebraicRing modul and a AlgebraicNumber value.
     * @param r ring RealAlgebraicRing<C>.
     * @param a value AlgebraicNumber<C>.
     */
    public RealAlgebraicNumber(RealAlgebraicRing<C> r, AlgebraicNumber<C> a) {
        number = a;
        ring = r;
    }


    /**
     * The constructor creates a RealAlgebraicNumber object from a GenPolynomial
     * object module.
     * @param r ring RealAlgebraicRing<C>.
     */
    public RealAlgebraicNumber(RealAlgebraicRing<C> r) {
        this(r, r.algebraic.getZERO());
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public RealAlgebraicRing<C> factory() {
        return ring;
    }


    /**
     * Copy this.
     * @see edu.jas.structure.Element#copy()
     */
    @Override
    public RealAlgebraicNumber<C> copy() {
        return new RealAlgebraicNumber<C>(ring, number);
    }


    /**
     * Return a BigRational approximation of this Element.
     * @return a BigRational approximation of this.
     * @see edu.jas.arith.Rational#getRational()
     */
    public BigRational getRational() {
        return magnitude();
    }


    /**
     * Is RealAlgebraicNumber zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return number.isZERO();
    }


    /**
     * Is RealAlgebraicNumber one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return number.isONE();
    }


    /**
     * Is RealAlgebraicNumber unit.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        return number.isUnit();
    }


    /**
     * Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (PrettyPrint.isTrue()) {
            return "{ " + number.toString() + " }";
        }
        return "Real" + number.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return number.toScript();
    }


    /**
     * Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    @Override
    public String toScriptFactory() {
        // Python case
        return factory().toScript();
    }


    /**
     * RealAlgebraicNumber comparison.
     * @param b RealAlgebraicNumber.
     * @return real sign(this-b).
     */
    @Override
    public int compareTo(RealAlgebraicNumber<C> b) {
        int s = 0;
        if (number.ring != b.number.ring) { // avoid compareTo if possible
            s = number.ring.modul.compareTo(b.number.ring.modul);
            System.out.println("s_mod = " + s);
        }
        if (s != 0) {
            return s;
        }
        s = this.subtract(b).signum(); // TODO
        //System.out.println("s_real = " + s);
        return s;
    }


    /**
     * RealAlgebraicNumber comparison.
     * @param b AlgebraicNumber.
     * @return polynomial sign(this-b).
     */
    public int compareTo(AlgebraicNumber<C> b) {
        int s = number.compareTo(b);
        System.out.println("s_algeb = " + s);
        return s;
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
        if (!(b instanceof RealAlgebraicNumber)) {
            return false;
        }
        RealAlgebraicNumber<C> a = (RealAlgebraicNumber<C>) b;
        if (!ring.equals(a.ring)) {
            return false;
        }
        return number.equals(a.number);
    }


    /**
     * Hash code for this RealAlgebraicNumber.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * number.val.hashCode() + ring.hashCode();
    }


    /**
     * RealAlgebraicNumber absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public RealAlgebraicNumber<C> abs() {
        if (this.signum() < 0) {
            return new RealAlgebraicNumber<C>(ring, number.negate());
        }
        return this;
    }


    /**
     * RealAlgebraicNumber summation.
     * @param S RealAlgebraicNumber.
     * @return this+S.
     */
    public RealAlgebraicNumber<C> sum(RealAlgebraicNumber<C> S) {
        return new RealAlgebraicNumber<C>(ring, number.sum(S.number));
    }


    /**
     * RealAlgebraicNumber summation.
     * @param c coefficient.
     * @return this+c.
     */
    public RealAlgebraicNumber<C> sum(GenPolynomial<C> c) {
        return new RealAlgebraicNumber<C>(ring, number.sum(c));
    }


    /**
     * RealAlgebraicNumber summation.
     * @param c polynomial.
     * @return this+c.
     */
    public RealAlgebraicNumber<C> sum(C c) {
        return new RealAlgebraicNumber<C>(ring, number.sum(c));
    }


    /**
     * RealAlgebraicNumber negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public RealAlgebraicNumber<C> negate() {
        return new RealAlgebraicNumber<C>(ring, number.negate());
    }


    /**
     * RealAlgebraicNumber signum. <b>Note: </b> Modifies ring.root eventually.
     * @see edu.jas.structure.RingElem#signum()
     * @return real signum(this).
     */
    public int signum() {
        Interval<C> v = ring.engine.invariantSignInterval(ring.root, ring.algebraic.modul, number.val);
        ring.setRoot(v);
        return ring.engine.realIntervalSign(v, ring.algebraic.modul, number.val);
    }


    /**
     * RealAlgebraicNumber half interval.
     */
    public void halfInterval() {
        Interval<C> v = ring.engine.halfInterval(ring.root, ring.algebraic.modul);
        //System.out.println("old v = " + ring.root + ", new v = " + v);
        ring.setRoot(v);
    }


    /**
     * RealAlgebraicNumber magnitude.
     * @return |this|.
     */
    public BigRational magnitude() {
        Interval<C> v = ring.engine.invariantMagnitudeInterval(ring.root, ring.algebraic.modul, number.val,
                        ring.getEps());
        //System.out.println("old v = " + ring.root + ", new v = " + v);
        ring.setRoot(v);
        C ev = ring.engine.realIntervalMagnitude(v, ring.algebraic.modul, number.val); //, ring.eps);
        //if ((Object) ev instanceof Rational) { // always true by type parameter
        BigRational er = ev.getRational();
        return er;
        //}
        //throw new RuntimeException("Rational expected, but was " + ev.getClass());
    }


    /**
     * RealAlgebraicNumber magnitude.
     * @return |this| as big decimal.
     */
    public BigDecimal decimalMagnitude() {
        return new BigDecimal(magnitude());
    }


    /**
     * RealAlgebraicNumber subtraction.
     * @param S RealAlgebraicNumber.
     * @return this-S.
     */
    public RealAlgebraicNumber<C> subtract(RealAlgebraicNumber<C> S) {
        return new RealAlgebraicNumber<C>(ring, number.subtract(S.number));
    }


    /**
     * RealAlgebraicNumber division.
     * @param S RealAlgebraicNumber.
     * @return this/S.
     */
    public RealAlgebraicNumber<C> divide(RealAlgebraicNumber<C> S) {
        return multiply(S.inverse());
    }


    /**
     * RealAlgebraicNumber inverse.
     * @see edu.jas.structure.RingElem#inverse()
     * @throws NotInvertibleException if the element is not invertible.
     * @return S with S = 1/this if defined.
     */
    public RealAlgebraicNumber<C> inverse() {
        return new RealAlgebraicNumber<C>(ring, number.inverse());
    }


    /**
     * RealAlgebraicNumber remainder.
     * @param S RealAlgebraicNumber.
     * @return this - (this/S)*S.
     */
    public RealAlgebraicNumber<C> remainder(RealAlgebraicNumber<C> S) {
        return new RealAlgebraicNumber<C>(ring, number.remainder(S.number));
    }


    /**
     * Quotient and remainder by division of this by S.
     * @param S a RealAlgebraicNumber
     * @return [this/S, this - (this/S)*S].
     */
    public RealAlgebraicNumber<C>[] quotientRemainder(RealAlgebraicNumber<C> S) {
        return new RealAlgebraicNumber[] { divide(S), remainder(S) };
    }


    /**
     * RealAlgebraicNumber multiplication.
     * @param S RealAlgebraicNumber.
     * @return this*S.
     */
    public RealAlgebraicNumber<C> multiply(RealAlgebraicNumber<C> S) {
        return new RealAlgebraicNumber<C>(ring, number.multiply(S.number));
    }


    /**
     * RealAlgebraicNumber multiplication.
     * @param c coefficient.
     * @return this*c.
     */
    public RealAlgebraicNumber<C> multiply(C c) {
        return new RealAlgebraicNumber<C>(ring, number.multiply(c));
    }


    /**
     * RealAlgebraicNumber multiplication.
     * @param c polynomial.
     * @return this*c.
     */
    public RealAlgebraicNumber<C> multiply(GenPolynomial<C> c) {
        return new RealAlgebraicNumber<C>(ring, number.multiply(c));
    }


    /**
     * RealAlgebraicNumber monic.
     * @return this with monic value part.
     */
    public RealAlgebraicNumber<C> monic() {
        return new RealAlgebraicNumber<C>(ring, number.monic());
    }


    /**
     * RealAlgebraicNumber greatest common divisor.
     * @param S RealAlgebraicNumber.
     * @return gcd(this,S).
     */
    public RealAlgebraicNumber<C> gcd(RealAlgebraicNumber<C> S) {
        return new RealAlgebraicNumber<C>(ring, number.gcd(S.number));
    }


    /**
     * RealAlgebraicNumber extended greatest common divisor.
     * @param S RealAlgebraicNumber.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    @SuppressWarnings("unchecked")
    public RealAlgebraicNumber<C>[] egcd(RealAlgebraicNumber<C> S) {
        AlgebraicNumber<C>[] aret = number.egcd(S.number);
        RealAlgebraicNumber<C>[] ret = new RealAlgebraicNumber[3];
        ret[0] = new RealAlgebraicNumber<C>(ring, aret[0]);
        ret[1] = new RealAlgebraicNumber<C>(ring, aret[1]);
        ret[2] = new RealAlgebraicNumber<C>(ring, aret[2]);
        return ret;
    }

}
