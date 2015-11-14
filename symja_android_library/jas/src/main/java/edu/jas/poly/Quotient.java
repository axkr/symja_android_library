/*
 * $Id$
 */

package edu.jas.poly;


import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.QuotPair;
import edu.jas.structure.RingElem;


/**
 * Quotient element based on RingElem pairs. Objects of this class are
 * immutable.
 * @author Heinz Kredel
 */
public class Quotient<C extends RingElem<C>> implements RingElem<Quotient<C>>, QuotPair<C> {


    private static final Logger logger = Logger.getLogger(Quotient.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Quotient class factory data structure.
     */
    public final QuotientRing<C> ring;


    /**
     * Numerator part of the element data structure.
     */
    public final C num;


    /**
     * Denominator part of the element data structure.
     */
    public final C den;


    /**
     * The constructor creates a Quotient object from a ring factory.
     * @param r ring factory.
     */
    public Quotient(QuotientRing<C> r) {
        this(r, r.ring.getZERO());
    }


    /**
     * The constructor creates a Quotient object from a ring factory and a
     * numerator element. The denominator is assumed to be 1.
     * @param r ring factory.
     * @param n numerator.
     */
    public Quotient(QuotientRing<C> r, C n) {
        this(r, n, r.ring.getONE(), true);
    }


    /**
     * The constructor creates a Quotient object from a ring factory and a
     * numerator and denominator element.
     * @param r ring factory.
     * @param n numerator.
     * @param d denominator.
     */
    public Quotient(QuotientRing<C> r, C n, C d) {
        this(r, n, d, false);
    }


    /**
     * The constructor creates a Quotient object from a ring factory and a
     * numerator and denominator element.
     * @param r ring factory.
     * @param n numerator.
     * @param d denominator.
     * @param isred true if gcd(n,d) == 1, else false.
     */
    @SuppressWarnings("unchecked")
    protected Quotient(QuotientRing<C> r, C n, C d, boolean isred) {
        if (d == null || d.isZERO()) {
            throw new IllegalArgumentException("denominator may not be zero");
        }
        ring = r;
        if (d.signum() < 0) {
            n = n.negate();
            d = d.negate();
        }
        if (isred) {
            num = n;
            den = d;
            return;
        }
        // must reduce to lowest terms
        if (n instanceof GcdRingElem && d instanceof GcdRingElem) {
            GcdRingElem ng = (GcdRingElem) n;
            GcdRingElem dg = (GcdRingElem) d;
            C gcd = (C) ng.gcd(dg);
            if (debug) {
                logger.info("gcd = " + gcd);
            }
            //RingElem<C> gcd = ring.ring.getONE();
            if (gcd.isONE()) {
                num = n;
                den = d;
            } else {
                num = n.divide(gcd);
                den = d.divide(gcd);
            }
            // } else { // univariate polynomial?
        } else {
            logger.warn("gcd = ????");
            num = n;
            den = d;
        }
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public QuotientRing<C> factory() {
        return ring;
    }


    /**
     * Numerator.
     * @see edu.jas.structure.QuotPair#numerator()
     */
    public C numerator() {
        return num;
    }


    /**
     * Denominator.
     * @see edu.jas.structure.QuotPair#denominator()
     */
    public C denominator() {
        return den;
    }


    /**
     * Is Quotient a constant. Not implemented.
     * @throws UnsupportedOperationException.
     */
    public boolean isConstant() {
        throw new UnsupportedOperationException("isConstant not implemented");
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public Quotient<C> copy() {
        return new Quotient<C>(ring, num, den, true);
    }


    /**
     * Is Quotient zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return num.isZERO();
    }


    /**
     * Is Quotient one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return num.equals(den);
    }


    /**
     * Is Quotient unit.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if (num.isZERO()) {
            return false;
        }
        return true;
    }


    /**
     * Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Quotient[ " + num.toString() + " / " + den.toString() + " ]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return "Quotient( " + num.toScript() + " , " + den.toScript() + " )";
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
     * Quotient comparison.
     * @param b Quotient.
     * @return sign(this-b).
     */
    @Override
    public int compareTo(Quotient<C> b) {
        if (b == null || b.isZERO()) {
            return this.signum();
        }
        C r = num.multiply(b.den);
        C s = den.multiply(b.num);
        C x = r.subtract(s);
        return x.signum();
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
        if (!(b instanceof Quotient)) {
            return false;
        }
        Quotient<C> a = (Quotient<C>) b;
        return (0 == compareTo(a));
    }


    /**
     * Hash code for this local.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = ring.hashCode();
        h = 37 * h + num.hashCode();
        h = 37 * h + den.hashCode();
        return h;
    }


    /**
     * Quotient absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public Quotient<C> abs() {
        return new Quotient<C>(ring, num.abs(), den, true);
    }


    /**
     * Quotient summation.
     * @param S Quotient.
     * @return this+S.
     */
    public Quotient<C> sum(Quotient<C> S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        C n = num.multiply(S.den);
        n = n.sum(den.multiply(S.num));
        C d = den.multiply(S.den);
        return new Quotient<C>(ring, n, d, false);
    }


    /**
     * Quotient negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Quotient<C> negate() {
        return new Quotient<C>(ring, num.negate(), den, true);
    }


    /**
     * Quotient signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return num.signum();
    }


    /**
     * Quotient subtraction.
     * @param S Quotient.
     * @return this-S.
     */
    public Quotient<C> subtract(Quotient<C> S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        C n = num.multiply(S.den);
        n = n.subtract(den.multiply(S.num));
        C d = den.multiply(S.den);
        return new Quotient<C>(ring, n, d, false);
    }


    /**
     * Quotient division.
     * @param S Quotient.
     * @return this/S.
     */
    public Quotient<C> divide(Quotient<C> S) {
        return multiply(S.inverse());
    }


    /**
     * Quotient inverse.
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this.
     */
    public Quotient<C> inverse() {
        return new Quotient<C>(ring, den, num, true);
    }


    /**
     * Quotient remainder.
     * @param S Quotient.
     * @return this - (this/S)*S.
     */
    public Quotient<C> remainder(Quotient<C> S) {
        if (num.isZERO()) {
            throw new ArithmeticException("element not invertible " + this);
        }
        return ring.getZERO();
    }


    /**
     * Quotient and remainder by division of this by S.
     * @param S a Quotient
     * @return [this/S, this - (this/S)*S].
     */
    public Quotient<C>[] quotientRemainder(Quotient<C> S) {
        return new Quotient[] { divide(S), remainder(S) };
    }


    /**
     * Quotient multiplication.
     * @param S Quotient.
     * @return this*S.
     */
    public Quotient<C> multiply(Quotient<C> S) {
        if (S == null || S.isZERO()) {
            return S;
        }
        if (num.isZERO()) {
            return this;
        }
        if (S.isONE()) {
            return this;
        }
        if (this.isONE()) {
            return S;
        }
        C n = num.multiply(S.num);
        C d = den.multiply(S.den);
        return new Quotient<C>(ring, n, d, false);
    }


    /**
     * Quotient monic.
     * @return this with monic value part.
     */
    public Quotient<C> monic() {
        logger.info("monic not implemented");
        return this;
    }


    /**
     * Greatest common divisor. <b>Note:</b> If not defined, throws
     * UnsupportedOperationException.
     * @param b other element.
     * @return gcd(this,b).
     */
    public Quotient<C> gcd(Quotient<C> b) {
        if (b == null || b.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return b;
        }
        if (num instanceof GcdRingElem && den instanceof GcdRingElem && b.num instanceof GcdRingElem
                        && b.den instanceof GcdRingElem) {
            return ring.getONE();
        }
        throw new UnsupportedOperationException("gcd not implemented " + num.getClass().getName());
    }


    /**
     * Extended greatest common divisor. <b>Note:</b> If not defined, throws
     * UnsupportedOperationException.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public Quotient<C>[] egcd(Quotient<C> b) {
        @SuppressWarnings("cast")
        Quotient<C>[] ret = (Quotient<C>[]) new Quotient[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if (b == null || b.isZERO()) {
            ret[0] = this;
            return ret;
        }
        if (this.isZERO()) {
            ret[0] = b;
            return ret;
        }
        if (num instanceof GcdRingElem && den instanceof GcdRingElem && b.num instanceof GcdRingElem
                        && b.den instanceof GcdRingElem) {
            Quotient<C> two = ring.fromInteger(2);
            ret[0] = ring.getONE();
            ret[1] = (this.multiply(two)).inverse();
            ret[2] = (b.multiply(two)).inverse();
            return ret;
        }
        throw new UnsupportedOperationException("egcd not implemented " + num.getClass().getName());
    }

}
