/*
 * $Id$
 */

package edu.jas.poly;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.QuotPair;
import edu.jas.structure.RingElem;


/**
 * Local element based on RingElem pairs. Objects of this class are (nearly)
 * immutable.
 * @author Heinz Kredel
 */
public class Local<C extends RingElem<C>> implements RingElem<Local<C>>, QuotPair<C> {


    private static final Logger logger = LogManager.getLogger(Local.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Local class factory data structure.
     */
    protected final LocalRing<C> ring;


    /**
     * Numerator part of the element data structure.
     */
    protected final C num;


    /**
     * Denominator part of the element data structure.
     */
    protected final C den;


    /**
     * Flag to remember if this local element is a unit. -1 is unknown, 1 is
     * unit, 0 not a unit.
     */
    protected int isunit = -1; // initially unknown


    /**
     * The constructor creates a Local object from a ring factory.
     * @param r ring factory.
     */
    public Local(LocalRing<C> r) {
        this(r, r.ring.getZERO());
    }


    /**
     * The constructor creates a Local object from a ring factory and a
     * numerator element. The denominator is assumed to be 1.
     * @param r ring factory.
     * @param n numerator.
     */
    public Local(LocalRing<C> r, C n) {
        this(r, n, r.ring.getONE(), true);
    }


    /**
     * The constructor creates a Local object from a ring factory and a
     * numerator and denominator element.
     * @param r ring factory.
     * @param n numerator.
     * @param d denominator.
     */
    public Local(LocalRing<C> r, C n, C d) {
        this(r, n, d, false);
    }


    /**
     * The constructor creates a Local object from a ring factory and a
     * numerator and denominator element.
     * @param r ring factory.
     * @param n numerator.
     * @param d denominator.
     * @param isred true if gcd(n,d) == 1, else false.
     */
    @SuppressWarnings("unchecked")
    protected Local(LocalRing<C> r, C n, C d, boolean isred) {
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
        C p = d.remainder(ring.ideal);
        if (p == null || p.isZERO()) {
            throw new IllegalArgumentException("denominator may not be in ideal");
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
            logger.warn("gcd = ????: " + n.getClass() + ", " + d.getClass());
            num = n;
            den = d;
        }
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public LocalRing<C> factory() {
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
     * Is Local a constant. Not implemented.
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
    public Local<C> copy() {
        return new Local<C>(ring, num, den, true);
    }


    /**
     * Is Local zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return num.isZERO();
    }


    /**
     * Is Local one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return num.equals(den);
    }


    /**
     * Is Local unit.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if (isunit > 0) {
            return true;
        }
        if (isunit == 0) {
            return false;
        }
        // not jet known
        if (num.isZERO()) {
            isunit = 0;
            return false;
        }
        C p = num.remainder(ring.ideal);
        boolean u = (p != null && !p.isZERO());
        if (u) {
            isunit = 1;
        } else {
            isunit = 0;
        }
        return (u);
    }


    /**
     * Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Local[ " + num.toString() + " / " + den.toString() + " ]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return "Local( " + num.toScript() + " , " + den.toScript() + " )";
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
     * Local comparison.
     * @param b Local.
     * @return sign(this-b).
     */
    @Override
    public int compareTo(Local<C> b) {
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
        if (!(b instanceof Local)) {
            return false;
        }
        Local<C> a = (Local<C>) b;
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
     * Local absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public Local<C> abs() {
        return new Local<C>(ring, num.abs(), den, true);
    }


    /**
     * Local summation.
     * @param S Local.
     * @return this+S.
     */
    public Local<C> sum(Local<C> S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        C n = num.multiply(S.den);
        n = n.sum(den.multiply(S.num));
        C d = den.multiply(S.den);
        return new Local<C>(ring, n, d, false);
    }


    /**
     * Local negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Local<C> negate() {
        return new Local<C>(ring, num.negate(), den, true);
    }


    /**
     * Local signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return num.signum();
    }


    /**
     * Local subtraction.
     * @param S Local.
     * @return this-S.
     */
    public Local<C> subtract(Local<C> S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        C n = num.multiply(S.den);
        n = n.subtract(den.multiply(S.num));
        C d = den.multiply(S.den);
        return new Local<C>(ring, n, d, false);
    }


    /**
     * Local division.
     * @param S Local.
     * @return this/S.
     */
    public Local<C> divide(Local<C> S) {
        return multiply(S.inverse());
    }


    /**
     * Local inverse.
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this if defined.
     */
    public Local<C> inverse() {
        if (isONE()) {
            return this;
        }
        if (isUnit()) {
            return new Local<C>(ring, den, num, true);
        }
        throw new ArithmeticException("element not invertible " + this);
    }


    /**
     * Local remainder.
     * @param S Local.
     * @return this - (this/S)*S.
     */
    public Local<C> remainder(Local<C> S) {
        if (num.isZERO()) {
            throw new ArithmeticException("element not invertible " + this);
        }
        if (S.isUnit()) {
            return ring.getZERO();
        }
        throw new UnsupportedOperationException("remainder not implemented" + S);
    }


    /**
     * Quotient and remainder by division of this by S.
     * @param S a Local
     * @return [this/S, this - (this/S)*S].
     */
    @SuppressWarnings("unchecked")
    public Local<C>[] quotientRemainder(Local<C> S) {
        return new Local[] { divide(S), remainder(S) };
    }


    /**
     * Local multiplication.
     * @param S Local.
     * @return this*S.
     */
    public Local<C> multiply(Local<C> S) {
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
        return new Local<C>(ring, n, d, false);
    }


    /**
     * Greatest common divisor. <b>Note: </b>Not implemented, throws
     * UnsupportedOperationException.
     * @param b other element.
     * @return gcd(this,b).
     */
    public Local<C> gcd(Local<C> b) {
        throw new UnsupportedOperationException("gcd not implemented " + this.getClass().getName());
    }


    /**
     * Extended greatest common divisor. <b>Note: </b>Not implemented, throws
     * UnsupportedOperationException.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public Local<C>[] egcd(Local<C> b) {
        throw new UnsupportedOperationException("egcd not implemented " + this.getClass().getName());
    }

}
