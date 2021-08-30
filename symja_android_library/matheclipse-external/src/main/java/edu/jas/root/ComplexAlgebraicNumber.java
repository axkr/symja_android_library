/*
 * $Id$
 */

package edu.jas.root;


import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.kern.PrettyPrint;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.NotInvertibleException;


/**
 * Complex algebraic number class based on AlgebraicNumber. Objects of this
 * class are immutable.
 * @author Heinz Kredel
 */

public class ComplexAlgebraicNumber<C extends GcdRingElem<C> & Rational>
                /*extends AlgebraicNumber<C>*/
                implements GcdRingElem<ComplexAlgebraicNumber<C>> {


    /**
     * Representing AlgebraicNumber.
     */
    public final AlgebraicNumber<Complex<C>> number;


    /**
     * Ring part of the data structure.
     */
    public final ComplexAlgebraicRing<C> ring;


    /**
     * The constructor creates a ComplexAlgebraicNumber object from
     * ComplexAlgebraicRing modul and a GenPolynomial value.
     * @param r ring ComplexAlgebraicRing<C>.
     * @param a value GenPolynomial<C>.
     */
    public ComplexAlgebraicNumber(ComplexAlgebraicRing<C> r, GenPolynomial<Complex<C>> a) {
        number = new AlgebraicNumber<Complex<C>>(r.algebraic, a);
        ring = r;
    }


    /**
     * The constructor creates a ComplexAlgebraicNumber object from
     * ComplexAlgebraicRing modul and a AlgebraicNumber value.
     * @param r ring ComplexAlgebraicRing<C>.
     * @param a value AlgebraicNumber<C>.
     */
    public ComplexAlgebraicNumber(ComplexAlgebraicRing<C> r, AlgebraicNumber<Complex<C>> a) {
        number = a;
        ring = r;
    }


    /**
     * The constructor creates a ComplexAlgebraicNumber object from a
     * GenPolynomial object module.
     * @param r ring ComplexAlgebraicRing<C>.
     */
    public ComplexAlgebraicNumber(ComplexAlgebraicRing<C> r) {
        this(r, r.algebraic.getZERO());
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public ComplexAlgebraicRing<C> factory() {
        return ring;
    }


    /**
     * Copy this.
     * @see edu.jas.structure.Element#copy()
     */
    @Override
    public ComplexAlgebraicNumber<C> copy() {
        return new ComplexAlgebraicNumber<C>(ring, number);
    }


    /**
     * Is ComplexAlgebraicNumber zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return number.isZERO();
    }


    /**
     * Is ComplexAlgebraicNumber one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return number.isONE();
    }


    /**
     * Is ComplexAlgebraicNumber unit.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        return number.isUnit();
    }


    /**
     * Is ComplexAlgebraicNumber a root of unity.
     * @return true if |this**i| == 1, for some 0 &lt; i &le; deg(modul), else
     *         false.
     */
    public boolean isRootOfUnity() {
        return number.isRootOfUnity();
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
        return "Complex" + number.toString();
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
     * ComplexAlgebraicNumber comparison.
     * @param b ComplexAlgebraicNumber.
     * @return sign(this-b).
     */
    @Override
    public int compareTo(ComplexAlgebraicNumber<C> b) {
        int s = 0;
        if (number.ring != b.number.ring) { // avoid compareTo if possible
            s = number.ring.modul.compareTo(b.number.ring.modul);
            System.out.println("s_mod = " + s);
        }
        if (s != 0) {
            return s;
        }
        s = number.compareTo(b.number); // TODO
        //System.out.println("s_real = " + s);
        return s;
    }


    /**
     * ComplexAlgebraicNumber comparison.
     * @param b AlgebraicNumber.
     * @return polynomial sign(this-b).
     */
    public int compareTo(AlgebraicNumber<Complex<C>> b) {
        int s = number.compareTo(b);
        //System.out.println("s_algeb = " + s);
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
        if (!(b instanceof ComplexAlgebraicNumber)) {
            return false;
        }
        ComplexAlgebraicNumber<C> a = (ComplexAlgebraicNumber<C>) b;
        if (!ring.equals(a.ring)) {
            return false;
        }
        return number.equals(a.number);
    }


    /**
     * Hash code for this ComplexAlgebraicNumber.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * number.val.hashCode() + ring.hashCode();
    }


    /**
     * ComplexAlgebraicNumber absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public ComplexAlgebraicNumber<C> abs() {
        if (this.signum() < 0) {
            return new ComplexAlgebraicNumber<C>(ring, number.negate());
        }
        return this;
    }


    /**
     * ComplexAlgebraicNumber summation.
     * @param S ComplexAlgebraicNumber.
     * @return this+S.
     */
    public ComplexAlgebraicNumber<C> sum(ComplexAlgebraicNumber<C> S) {
        return new ComplexAlgebraicNumber<C>(ring, number.sum(S.number));
    }


    /**
     * ComplexAlgebraicNumber summation.
     * @param c complex polynomial.
     * @return this+c.
     */
    public ComplexAlgebraicNumber<C> sum(GenPolynomial<Complex<C>> c) {
        return new ComplexAlgebraicNumber<C>(ring, number.sum(c));
    }


    /**
     * ComplexAlgebraicNumber summation.
     * @param c algebraic number.
     * @return this+c.
     */
    public ComplexAlgebraicNumber<C> sum(AlgebraicNumber<Complex<C>> c) {
        return new ComplexAlgebraicNumber<C>(ring, number.sum(c));
    }


    /**
     * ComplexAlgebraicNumber summation.
     * @param c coefficient.
     * @return this+c.
     */
    public ComplexAlgebraicNumber<C> sum(Complex<C> c) {
        return new ComplexAlgebraicNumber<C>(ring, number.sum(c));
    }


    /**
     * ComplexAlgebraicNumber negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public ComplexAlgebraicNumber<C> negate() {
        return new ComplexAlgebraicNumber<C>(ring, number.negate());
    }


    /**
     * ComplexAlgebraicNumber subtraction.
     * @param S ComplexAlgebraicNumber.
     * @return this-S.
     */
    public ComplexAlgebraicNumber<C> subtract(ComplexAlgebraicNumber<C> S) {
        return new ComplexAlgebraicNumber<C>(ring, number.subtract(S.number));
    }


    /**
     * ComplexAlgebraicNumber division.
     * @param S ComplexAlgebraicNumber.
     * @return this/S.
     */
    public ComplexAlgebraicNumber<C> divide(ComplexAlgebraicNumber<C> S) {
        return multiply(S.inverse());
    }


    /**
     * ComplexAlgebraicNumber inverse.
     * @see edu.jas.structure.RingElem#inverse()
     * @throws NotInvertibleException if the element is not invertible.
     * @return S with S = 1/this if defined.
     */
    public ComplexAlgebraicNumber<C> inverse() {
        return new ComplexAlgebraicNumber<C>(ring, number.inverse());
    }


    /**
     * ComplexAlgebraicNumber remainder.
     * @param S ComplexAlgebraicNumber.
     * @return this - (this/S)*S.
     */
    public ComplexAlgebraicNumber<C> remainder(ComplexAlgebraicNumber<C> S) {
        return new ComplexAlgebraicNumber<C>(ring, number.remainder(S.number));
    }


    /**
     * Quotient and remainder by division of this by S.
     * @param S a ComplexAlgebraicNumber
     * @return [this/S, this - (this/S)*S].
     */
    @SuppressWarnings("unchecked")
    public ComplexAlgebraicNumber<C>[] quotientRemainder(ComplexAlgebraicNumber<C> S) {
        return new ComplexAlgebraicNumber[] { divide(S), remainder(S) };
    }


    /**
     * ComplexAlgebraicNumber multiplication.
     * @param S ComplexAlgebraicNumber.
     * @return this*S.
     */
    public ComplexAlgebraicNumber<C> multiply(ComplexAlgebraicNumber<C> S) {
        return new ComplexAlgebraicNumber<C>(ring, number.multiply(S.number));
    }


    /**
     * ComplexAlgebraicNumber multiplication.
     * @param c coefficient.
     * @return this*c.
     */
    public ComplexAlgebraicNumber<C> multiply(Complex<C> c) {
        return new ComplexAlgebraicNumber<C>(ring, number.multiply(c));
    }


    /**
     * ComplexAlgebraicNumber multiplication.
     * @param c polynomial.
     * @return this*c.
     */
    public ComplexAlgebraicNumber<C> multiply(GenPolynomial<Complex<C>> c) {
        return new ComplexAlgebraicNumber<C>(ring, number.multiply(c));
    }


    /**
     * ComplexAlgebraicNumber monic.
     * @return this with monic value part.
     */
    public ComplexAlgebraicNumber<C> monic() {
        return new ComplexAlgebraicNumber<C>(ring, number.monic());
    }


    /**
     * ComplexAlgebraicNumber greatest common divisor.
     * @param S ComplexAlgebraicNumber.
     * @return gcd(this,S).
     */
    public ComplexAlgebraicNumber<C> gcd(ComplexAlgebraicNumber<C> S) {
        return new ComplexAlgebraicNumber<C>(ring, number.gcd(S.number));
    }


    /**
     * ComplexAlgebraicNumber extended greatest common divisor.
     * @param S ComplexAlgebraicNumber.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    @SuppressWarnings("unchecked")
    public ComplexAlgebraicNumber<C>[] egcd(ComplexAlgebraicNumber<C> S) {
        AlgebraicNumber<Complex<C>>[] aret = number.egcd(S.number);
        ComplexAlgebraicNumber<C>[] ret = new ComplexAlgebraicNumber[3];
        ret[0] = new ComplexAlgebraicNumber<C>(ring, aret[0]);
        ret[1] = new ComplexAlgebraicNumber<C>(ring, aret[1]);
        ret[2] = new ComplexAlgebraicNumber<C>(ring, aret[2]);
        return ret;
    }


    /**
     * ComplexAlgebraicNumber signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        ring.ensureEngine();
        try {
            Rectangle<C> v = ring.engine.invariantRectangle(ring.root, ring.algebraic.modul, number.val);
            ring.setRoot(v);
            Complex<C> c = v.getCenter();
            return c.signum();
        } catch (InvalidBoundaryException e) { // should not happen
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * ComplexAlgebraicNumber magnitude.
     * @return |this| as complex rational number.
     */
    public Complex<BigRational> magnitude() {
        ring.ensureEngine();
        try {
            Rectangle<C> v = ring.engine.invariantMagnitudeRectangle(ring.root, ring.algebraic.modul,
                            number.val, ring.getEps());
            ring.setRoot(v);
            //System.out.println("new v = " + v);
            Complex<C> ev = ring.engine.complexRectangleMagnitude(v, ring.algebraic.modul, number.val); //, ring.eps);
            BigRational er = ev.getRe().getRational();
            BigRational ei = ev.getIm().getRational();
            ComplexRing<BigRational> cr = new ComplexRing<BigRational>(er.factory());
            return new Complex<BigRational>(cr, er, ei);
        } catch (InvalidBoundaryException e) { // should not happen
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * ComplexAlgebraicNumber magnitude.
     * @return |this| as complex big decimal.
     */
    public Complex<BigDecimal> decimalMagnitude() {
        Complex<BigRational> cr = magnitude();
        ComplexRing<BigDecimal> dr = new ComplexRing<BigDecimal>(BigDecimal.ZERO);
        return new Complex<BigDecimal>(dr, new BigDecimal(cr.getRe()), new BigDecimal(cr.getIm()));
    }

}
