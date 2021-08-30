/*
 * $Id$
 */

package edu.jas.fd;


import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.kern.PrettyPrint;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.QuotPair;


/**
 * SolvableQuotient, that is a (left) rational function, based on
 * GenSolvablePolynomial with RingElem interface. Objects of this class are
 * immutable.
 * @author Heinz Kredel
 */
public class SolvableQuotient<C extends GcdRingElem<C>>
                implements GcdRingElem<SolvableQuotient<C>>, QuotPair<GenPolynomial<C>> {
    // should be QuotPair<GenSolvablePolynomial<C>


    private static final Logger logger = LogManager.getLogger(SolvableQuotient.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * SolvableQuotient class factory data structure.
     */
    public final SolvableQuotientRing<C> ring;


    /**
     * Numerator part of the element data structure.
     */
    public final GenSolvablePolynomial<C> num;


    /**
     * Denominator part of the element data structure.
     */
    public final GenSolvablePolynomial<C> den;


    /**
     * The constructor creates a SolvableQuotient object from a ring factory.
     * @param r ring factory.
     */
    public SolvableQuotient(SolvableQuotientRing<C> r) {
        this(r, r.ring.getZERO());
    }


    /**
     * The constructor creates a SolvableQuotient object from a ring factory and
     * a numerator polynomial. The denominator is assumed to be 1.
     * @param r ring factory.
     * @param n numerator solvable polynomial.
     */
    public SolvableQuotient(SolvableQuotientRing<C> r, GenSolvablePolynomial<C> n) {
        this(r, n, r.ring.getONE(), true);
    }


    /**
     * The constructor creates a SolvableQuotient object from a ring factory and
     * a numerator and denominator solvable polynomial.
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     */
    public SolvableQuotient(SolvableQuotientRing<C> r, GenSolvablePolynomial<C> n,
                    GenSolvablePolynomial<C> d) {
        this(r, n, d, false);
    }


    /**
     * The constructor creates a SolvableQuotient object from a ring factory and
     * a numerator and denominator polynomial.
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     * @param isred <em>unused at the moment</em>.
     */
    protected SolvableQuotient(SolvableQuotientRing<C> r, GenSolvablePolynomial<C> n,
                    GenSolvablePolynomial<C> d, boolean isred) {
        if (d == null || d.isZERO()) {
            throw new IllegalArgumentException("denominator may not be zero");
        }
        ring = r;
        if (d.signum() < 0) {
            n = (GenSolvablePolynomial<C>) n.negate();
            d = (GenSolvablePolynomial<C>) d.negate();
        }
        if (isred) {
            num = n;
            den = d;
            return;
        }
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
            num = (GenSolvablePolynomial<C>) ring.ring.getONE().negate();
            den = ring.ring.getONE();
            return;
        }
        if (n.isZERO()) {
            num = n;
            den = ring.ring.getONE();
            return;
        }
        //if (n.isONE() || d.isONE()) {
        if (n.isConstant() || d.isConstant()) {
            num = n;
            den = d;
            return;
        }
        // must reduce to lowest terms
        // not perfect, TODO 
        //GenSolvablePolynomial<C>[] gcd = PolyModUtil.<C> syzGcdCofactors(r.ring, n, d);
        GenSolvablePolynomial<C>[] gcd = FDUtil.<C> leftGcdCofactors(r.ring, n, d);
        if (!gcd[0].isONE()) {
            logger.info("constructor: gcd = " + Arrays.toString(gcd)); // + ", " + n + ", " +d);
            n = gcd[1];
            d = gcd[2];
        }
        gcd = FDUtil.<C> rightGcdCofactors(r.ring, n, d);
        if (!gcd[0].isONE()) {
            logger.info("constructor: gcd = " + Arrays.toString(gcd)); // + ", " + n + ", " +d);
            n = gcd[1];
            d = gcd[2];
        }
        // not perfect, TODO 
        GenSolvablePolynomial<C>[] simp = ring.engine.leftSimplifier(n, d);
        logger.info("simp: " + Arrays.toString(simp) + ", " + n + ", " + d);
        num = simp[0];
        den = simp[1];
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public SolvableQuotientRing<C> factory() {
        return ring;
    }


    /**
     * Numerator.
     * @see edu.jas.structure.QuotPair#numerator()
     */
    public GenSolvablePolynomial<C> numerator() {
        return num;
    }


    /**
     * Denominator.
     * @see edu.jas.structure.QuotPair#denominator()
     */
    public GenSolvablePolynomial<C> denominator() {
        return den;
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public SolvableQuotient<C> copy() {
        return new SolvableQuotient<C>(ring, num, den, true);
    }


    /**
     * Is SolvableQuotient zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return num.isZERO();
    }


    /**
     * Is SolvableQuotient one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return num.compareTo(den) == 0;
    }


    /**
     * Is SolvableQuotient a unit.
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
            if (!den.isONE()) {
                s += " | " + den.toString(ring.ring.getVars());
            }
            return s + " }";
        }
        return "SolvableQuotient[ " + num.toString() + " | " + den.toString() + " ]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // any scripting case
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
        return factory().toScript();
    }


    /**
     * SolvableQuotient comparison.
     * @param b SolvableQuotient.
     * @return sign(this-b).
     */
    @Override
    public int compareTo(SolvableQuotient<C> b) {
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
            return t;
        }
        if (den.compareTo(b.den) == 0) {
            return num.compareTo(b.num);
        }
        GenSolvablePolynomial<C> r, s;
        // if (den.isONE()) {
        //     r = b.den.multiply(num);
        //     s = b.num;
        //     return r.compareTo(s);
        // }
        // if (b.den.isONE()) {
        //     r = num;
        //     s = den.multiply(b.num);
        //     return r.compareTo(s);
        // }
        GenSolvablePolynomial<C>[] oc = ring.engine.leftOreCond(den, b.den);
        if (debug) {
            System.out.println("oc[0] den =<>= oc[1] b.den: (" + oc[0] + ") (" + den + ") = (" + oc[1] + ") ("
                            + b.den + ")");
        }
        r = oc[0].multiply(num);
        s = oc[1].multiply(b.num);
        //System.out.println("r = " + r);
        //System.out.println("s = " + s);
        return r.compareTo(s);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object b) {
        if (b == null) {
            return false;
        }
        if (!(b instanceof SolvableQuotient)) {
            return false;
        }
        SolvableQuotient<C> a = (SolvableQuotient<C>) b;
        if (num.equals(a.num) && den.equals(a.den)) { // short cut
            return true;
        }
        return compareTo(a) == 0;
    }


    /**
     * Hash code for this element.
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
     * SolvableQuotient right fraction. <b>Note:</b> It is not possible to
     * distinguish right from left fractions in the current implementation. So
     * it is not possible to compute with right fractions.
     * @return SolvableQuotient(a,b), where den<sup>-1</sup> num = a b
     *         <sup>-1</sup>
     */
    public SolvableQuotient<C> rightFraction() {
        if (isZERO() || isONE()) {
            return this;
        }
        GenSolvablePolynomial<C>[] oc = ring.engine.rightOreCond(num, den);
        return new SolvableQuotient<C>(ring, oc[1], oc[0], true); // reversed, true is wrong but okay
    }


    /**
     * Test if SolvableQuotient right fraction. <b>Note:</b> It is not possible
     * to distinguish right from left fractions in the current implementation.
     * So it is not possible to compute with right fractions.
     * @param s = SolvableQuotient(a,b)
     * @return true if s is a right fraction of this, i.e. den<sup>-1</sup> num
     *         = a b<sup>-1</sup>
     */
    public boolean isRightFraction(SolvableQuotient<C> s) {
        if (isZERO()) {
            return s.isZERO();
        }
        if (isONE()) {
            return s.isONE();
        }
        GenSolvablePolynomial<C> x = den.multiply(s.num);
        GenSolvablePolynomial<C> y = num.multiply(s.den);
        return x.compareTo(y) == 0;
    }


    /**
     * SolvableQuotient absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public SolvableQuotient<C> abs() {
        return new SolvableQuotient<C>(ring, (GenSolvablePolynomial<C>) num.abs(), den, true);
    }


    /**
     * SolvableQuotient summation.
     * @param S SolvableQuotient.
     * @return this+S.
     */
    public SolvableQuotient<C> sum(SolvableQuotient<C> S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        GenSolvablePolynomial<C> n, d, n1, n2;
        if (den.isONE() && S.den.isONE()) {
            n = (GenSolvablePolynomial<C>) num.sum(S.num);
            return new SolvableQuotient<C>(ring, n, den, true);
        }
        /* wrong:
        if (den.isONE()) {
            n = S.den.multiply(num);
            n = (GenSolvablePolynomial<C>) n.sum(S.num);
            return new SolvableQuotient<C>(ring, n, S.den, false);
        }
        if (S.den.isONE()) {
            n = den.multiply(S.num);
            n = (GenSolvablePolynomial<C>) n.sum(num);
            return new SolvableQuotient<C>(ring, n, den, false);
        }
        */
        if (den.compareTo(S.den) == 0) { // correct ?
            //d = den.multiply(den);
            //n1 = den.multiply(S.num);
            //n2 = S.den.multiply(num);
            n = (GenSolvablePolynomial<C>) num.sum(S.num);
            return new SolvableQuotient<C>(ring, n, den, false);
        }
        // general case
        GenSolvablePolynomial<C>[] oc = ring.engine.leftOreCond(den, S.den);
        if (debug) {
            System.out.println("oc[0] den =sum= oc[1] S.den: (" + oc[0] + ") (" + den + ") = (" + oc[1]
                            + ") (" + S.den + ")");
        }
        d = oc[0].multiply(den);
        n1 = oc[0].multiply(num);
        n2 = oc[1].multiply(S.num);
        n = (GenSolvablePolynomial<C>) n1.sum(n2);
        //System.out.println("n = " + n);
        //System.out.println("d = " + d);
        return new SolvableQuotient<C>(ring, n, d, false);
    }


    /**
     * SolvableQuotient negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public SolvableQuotient<C> negate() {
        return new SolvableQuotient<C>(ring, (GenSolvablePolynomial<C>) num.negate(), den, true);
    }


    /**
     * SolvableQuotient signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        // assume sign(den) > 0
        return num.signum();
    }


    /**
     * SolvableQuotient subtraction.
     * @param S SolvableQuotient.
     * @return this-S.
     */
    public SolvableQuotient<C> subtract(SolvableQuotient<C> S) {
        return sum(S.negate());
    }


    /**
     * SolvableQuotient division.
     * @param S SolvableQuotient.
     * @return this/S.
     */
    public SolvableQuotient<C> divide(SolvableQuotient<C> S) {
        return multiply(S.inverse());
    }


    /**
     * SolvableQuotient inverse.
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this.
     */
    public SolvableQuotient<C> inverse() {
        if (num.isZERO()) {
            throw new ArithmeticException("element not invertible " + this);
        }
        return new SolvableQuotient<C>(ring, den, num, true);
    }


    /**
     * SolvableQuotient remainder.
     * @param S SolvableQuotient.
     * @return this - (this/S)*S.
     */
    public SolvableQuotient<C> remainder(SolvableQuotient<C> S) {
        if (S.isZERO()) {
            throw new ArithmeticException("element not invertible " + S);
        }
        return ring.getZERO();
    }


    /**
     * Quotient and remainder by division of this by S.
     * @param S a SolvableQuotient
     * @return [this/S, this - (this/S)*S].
     */
    @SuppressWarnings("unchecked")
    public SolvableQuotient<C>[] quotientRemainder(SolvableQuotient<C> S) {
        return new SolvableQuotient[] { divide(S), remainder(S) };
    }


    /**
     * SolvableQuotient multiplication.
     * @param S SolvableQuotient.
     * @return this*S.
     */
    public SolvableQuotient<C> multiply(SolvableQuotient<C> S) {
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
        GenSolvablePolynomial<C> n, d;
        if (den.isONE() && S.den.isONE()) {
            n = num.multiply(S.num);
            return new SolvableQuotient<C>(ring, n, den, true);
        }
        /* wrong:
        if (den.isONE()) { 
            d = S.den;
            n = num.multiply(S.num);
            return new SolvableQuotient<C>(ring, n, d, false);
        }
        if (S.den.isONE()) { 
            d = den;
            n = num.multiply(S.num);
            return new SolvableQuotient<C>(ring, n, d, false);
        }
        */
        // if ( den.compareTo(S.den) == 0 ) { // not correct ?
        //     d = den.multiply(den);
        //     n = num.multiply(S.num);
        //     return new SolvableQuotient<C>(ring, n, d, false);
        // }
        GenSolvablePolynomial<C>[] oc = ring.engine.leftOreCond(num, S.den);
        n = oc[1].multiply(S.num);
        d = oc[0].multiply(den);
        if (debug) {
            System.out.println("oc[0] num =mult= oc[1] S.den: (" + oc[0] + ") (" + num + ") = (" + oc[1]
                            + ") (" + S.den + ")");
        }
        return new SolvableQuotient<C>(ring, n, d, false);
    }


    /**
     * SolvableQuotient multiplication by GenSolvablePolynomial.
     * @param b GenSolvablePolynomial<C>.
     * @return this*b.
     */
    public SolvableQuotient<C> multiply(GenSolvablePolynomial<C> b) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (num.isZERO()) {
            return this;
        }
        if (b.isONE()) {
            return this;
        }
        GenSolvablePolynomial<C> n = num.multiply(b);
        return new SolvableQuotient<C>(ring, n, den, false);
    }


    /**
     * SolvableQuotient multiplication by coefficient.
     * @param b coefficient.
     * @return this*b.
     */
    public SolvableQuotient<C> multiply(C b) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (num.isZERO()) {
            return this;
        }
        if (b.isONE()) {
            return this;
        }
        GenSolvablePolynomial<C> n = num.multiply(b);
        return new SolvableQuotient<C>(ring, n, den, false);
    }


    /**
     * SolvableQuotient multiplication by exponent.
     * @param e exponent vector.
     * @return this*b.
     */
    public SolvableQuotient<C> multiply(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        if (num.isZERO()) {
            return this;
        }
        GenSolvablePolynomial<C> n = num.multiply(e);
        return new SolvableQuotient<C>(ring, n, den, false);
    }


    /**
     * SolvableQuotient monic.
     * @return this with monic value part.
     */
    public SolvableQuotient<C> monic() {
        if (num.isZERO()) {
            return this;
        }
        C lbc = num.leadingBaseCoefficient();
        if (!lbc.isUnit()) {
            return this;
        }
        lbc = lbc.inverse();
        //lbc = lbc.abs();
        GenSolvablePolynomial<C> n = num.multiply(lbc);
        //GenSolvablePolynomial<C> d = (GenSolvablePolynomial<C>) den.multiply(lbc);
        return new SolvableQuotient<C>(ring, n, den, true);
    }


    /**
     * Greatest common divisor.
     * @param b other element.
     * @return gcd(this,b).
     */
    public SolvableQuotient<C> gcd(SolvableQuotient<C> b) {
        if (b == null || b.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return b;
        }
        if (this.equals(b)) {
            return this;
        }
        return ring.getONE();
    }


    /**
     * Extended greatest common divisor.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    @SuppressWarnings("unchecked")
    public SolvableQuotient<C>[] egcd(SolvableQuotient<C> b) {
        @SuppressWarnings("cast")
        SolvableQuotient<C>[] ret = (SolvableQuotient<C>[]) new SolvableQuotient[3];
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
        GenSolvablePolynomial<C> two = ring.ring.fromInteger(2);
        ret[0] = ring.getONE();
        ret[1] = (this.multiply(two)).inverse();
        ret[2] = (b.multiply(two)).inverse();
        return ret;
    }

}
