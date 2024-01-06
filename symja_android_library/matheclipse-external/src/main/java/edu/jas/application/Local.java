/*
 * $Id$
 */

package edu.jas.application;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.kern.PrettyPrint;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.QuotPair;
import edu.jas.structure.RingElem;


/**
 * Local ring element based on GenPolynomial with RingElem interface. Objects of
 * this class are (nearly) immutable.
 * @author Heinz Kredel
 */
// To be fixed?: Not jet working because of monic GBs.
public class Local<C extends GcdRingElem<C>> implements RingElem<Local<C>>, QuotPair<GenPolynomial<C>> {


    private static final Logger logger = LogManager.getLogger(Local.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Local class factory data structure.
     */
    public final LocalRing<C> ring;


    /**
     * Numerator part of the element data structure.
     */
    protected final GenPolynomial<C> num;


    /**
     * Denominator part of the element data structure.
     */
    protected final GenPolynomial<C> den;


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
     * numerator polynomial. The denominator is assumed to be 1.
     * @param r ring factory.
     * @param n numerator polynomial.
     */
    public Local(LocalRing<C> r, GenPolynomial<C> n) {
        this(r, n, r.ring.getONE(), true);
    }


    /**
     * The constructor creates a Local object from a ring factory and a
     * numerator and denominator polynomial.
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     */
    public Local(LocalRing<C> r, GenPolynomial<C> n, GenPolynomial<C> d) {
        this(r, n, d, false);
    }


    /**
     * The constructor creates a Local object from a ring factory and a
     * numerator and denominator polynomial.
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     * @param isred true if gcd(n,d) == 1, else false.
     */
    protected Local(LocalRing<C> r, GenPolynomial<C> n, GenPolynomial<C> d, boolean isred) {
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
        GenPolynomial<C> p = ring.ideal.normalform(d);
        if (p == null || p.isZERO()) {
            throw new IllegalArgumentException("denominator may not be in ideal");
        }
        //d = p; can't do this
        C lc = d.leadingBaseCoefficient();
        if (!lc.isONE() && lc.isUnit()) {
            lc = lc.inverse();
            n = n.multiply(lc);
            d = d.multiply(lc);
        }
        if (n.compareTo(d) == 0) {
            num = ring.ring.getONE();
            den = ring.ring.getONE();
            return;
        }
        if (n.negate().compareTo(d) == 0) {
            num = ring.ring.getONE().negate();
            den = ring.ring.getONE();
            return;
        }
        if (n.isZERO()) {
            num = n;
            den = ring.ring.getONE();
            return;
        }
        // must reduce to lowest terms
        //GenPolynomial<C> gcd = ring.ring.getONE();
        GenPolynomial<C> gcd = ring.engine.gcd(n, d);
        if (debug) {
            logger.info("gcd = {}", gcd);
        }
        if (gcd.isONE()) {
            num = n;
            den = d;
        } else {
            // d not in ideal --> gcd not in ideal 
            //p = ring.ideal.normalform( gcd );
            //if ( p == null || p.isZERO() ) { // find nonzero factor
            //   num = n;
            //   den = d;
            //} else {
            num = n.divide(gcd);
            den = d.divide(gcd);
            //}
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
    public GenPolynomial<C> numerator() {
        return num;
    }


    /**
     * Denominator.
     * @see edu.jas.structure.QuotPair#denominator()
     */
    public GenPolynomial<C> denominator() {
        return den;
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
        GenPolynomial<C> p = ring.ideal.normalform(num);
        boolean u = (p != null && !p.isZERO());
        if (u) {
            isunit = 1;
        } else {
            isunit = 0;
        }
        return (u);
    }


    /**
     * Is Qoutient a constant.
     * @return true, if this has constant numerator and denominator, else false.
     */
    public boolean isConstant() {
        return num.isConstant() && den.isConstant();
    }


    /**
     * Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (PrettyPrint.isTrue()) {
            String s = "{ " + num.toString(ring.ring.getVars());
            if (den.isONE()) {
                return s + " }";
            }
            return s + "| " + den.toString(ring.ring.getVars()) + " }";
        }
        return "Local[ " + num.toString() + " | " + den.toString() + " ]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        if (den.isONE()) {
            return num.toScript();
        }
        return num.toScript() + " / " + den.toScript();
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
        if (this.isZERO()) {
            return -b.signum();
        }
        // assume sign(den,b.den) > 0
        int s1 = num.signum();
        int s2 = b.num.signum();
        int t = (s1 - s2) / 2;
        if (t != 0) {
            System.out.println("compareTo: t = " + t);
            return t;
        }
        if (den.compareTo(b.den) == 0) {
            return num.compareTo(b.num);
        }
        GenPolynomial<C> r = num.multiply(b.den);
        GenPolynomial<C> s = den.multiply(b.num);
        return r.compareTo(s);
        //GenPolynomial<C> x = r.subtract(s);
        //return x.signum();
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof Local)) {
            return false;
        }
        Local<C> a = null;
        try {
            a = (Local<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        return compareTo(a) == 0;
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
        GenPolynomial<C> n = num.multiply(S.den);
        n = n.sum(den.multiply(S.num));
        GenPolynomial<C> d = den.multiply(S.den);
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
        GenPolynomial<C> n = num.multiply(S.den);
        n = n.subtract(den.multiply(S.num));
        GenPolynomial<C> d = den.multiply(S.den);
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
        if (S.isUnit()) {
            return ring.getZERO();
        }
        throw new UnsupportedOperationException("remainder not implemented" + S);
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
        GenPolynomial<C> n = num.multiply(S.num);
        GenPolynomial<C> d = den.multiply(S.den);
        return new Local<C>(ring, n, d, false);
    }


    /**
     * Local multiplication by GenPolynomial.
     * @param b GenPolynomial.
     * @return this*b.
     */
    public Local<C> multiply(GenPolynomial<C> b) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (num.isZERO()) {
            return this;
        }
        if (b.isONE()) {
            return this;
        }
        GenPolynomial<C> n = num.multiply(b);
        return new Local<C>(ring, n, den, false);
    }


    /**
     * Local multiplication by coefficient.
     * @param b coefficient.
     * @return this*b.
     */
    public Local<C> multiply(C b) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (num.isZERO()) {
            return this;
        }
        if (b.isONE()) {
            return this;
        }
        GenPolynomial<C> n = num.multiply(b);
        return new Local<C>(ring, n, den, false);
    }


    /**
     * Local multiplication by exponent.
     * @param e exponent vector.
     * @return this*b.
     */
    public Local<C> multiply(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        if (num.isZERO()) {
            return this;
        }
        GenPolynomial<C> n = num.multiply(e);
        return new Local<C>(ring, n, den, false);
    }


    /**
     * Local monic.
     * @return this with monic value part.
     */
    public Local<C> monic() {
        if (num.isZERO()) {
            return this;
        }
        return this;
        // non sense:
        //C lbc = num.leadingBaseCoefficient();
        //lbc = lbc.inverse();
        //GenPolynomial<C> n = num.multiply(lbc);
        //GenPolynomial<C> d = den.multiply(lbc);
        //return new Local<C>(ring, n, d, true);
    }


    /**
     * Greatest common divisor.
     * @param b other element.
     * @return gcd(this,b).
     */
    public Local<C> gcd(Local<C> b) {
        GenPolynomial<C> x = ring.engine.gcd(num, b.num);
        GenPolynomial<C> y = ring.engine.gcd(den, b.den);
        return new Local<C>(ring, x, y, true);
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
