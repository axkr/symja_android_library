/*
 * $Id$
 */

package edu.jas.application;


import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.fd.FDUtil;
import edu.jas.gbufd.PolyModUtil;
import edu.jas.kern.PrettyPrint;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.QuotPair;


/**
 * SolvableLocal ring element based on pairs of GenSolvablePolynomial with
 * GcdRingElem interface. Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class SolvableLocal<C extends GcdRingElem<C>> implements GcdRingElem<SolvableLocal<C>>,
                QuotPair<GenPolynomial<C>> {


    private static final Logger logger = LogManager.getLogger(SolvableLocal.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * SolvableLocal class factory data structure.
     */
    public final SolvableLocalRing<C> ring;


    /**
     * Numerator part of the element data structure.
     */
    public final GenSolvablePolynomial<C> num;


    /**
     * Denominator part of the element data structure.
     */
    public final GenSolvablePolynomial<C> den;


    /**
     * Flag to remember if this local element is a unit. -1 is unknown, 1 is
     * unit, 0 not a unit.
     */
    protected int isunit = -1; // initially unknown


    /**
     * The constructor creates a SolvableLocal object from a ring factory.
     * @param r ring factory.
     */
    public SolvableLocal(SolvableLocalRing<C> r) {
        this(r, r.ring.getZERO());
    }


    /**
     * The constructor creates a SolvableLocal object from a ring factory and a
     * numerator polynomial. The denominator is assumed to be 1.
     * @param r ring factory.
     * @param n numerator polynomial.
     */
    public SolvableLocal(SolvableLocalRing<C> r, GenSolvablePolynomial<C> n) {
        this(r, n, r.ring.getONE(), true);
    }


    /**
     * The constructor creates a SolvableLocal object from a ring factory and a
     * numerator and denominator polynomial.
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     */
    public SolvableLocal(SolvableLocalRing<C> r, GenSolvablePolynomial<C> n, GenSolvablePolynomial<C> d) {
        this(r, n, d, false);
    }


    /**
     * The constructor creates a SolvableLocal object from a ring factory and a
     * numerator and denominator polynomial.
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     * @param isred true if gcd(n,d) == 1, else false.
     */
    protected SolvableLocal(SolvableLocalRing<C> r, GenSolvablePolynomial<C> n, GenSolvablePolynomial<C> d,
                    boolean isred) {
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
        if (debug) {
            System.out.println("n = " + n + ", d = " + d);
        }
        GenSolvablePolynomial<C> p = ring.ideal.normalform(d);
        if (p == null || p.isZERO()) {
            throw new IllegalArgumentException("denominator may not be in ideal, d = " + d);
        }
        //d = p; can't do this
        C lc = d.leadingBaseCoefficient();
        if (!lc.isONE() && lc.isUnit()) {
            lc = lc.inverse();
            n = n.multiplyLeft(lc);
            d = d.multiplyLeft(lc);
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
        if (n.isONE()) {
            num = n;
            den = d;
            return;
        }
        // must reduce to lowest terms
        // not perfect, TODO improve
        //GenSolvablePolynomial<C>[] gcd = PolyModUtil.<C> syzGcdCofactors(r.ring, n, d);
        GenSolvablePolynomial<C>[] gcd = ring.fdengine.leftGcdCofactors(r.ring, n, d);
        if (!gcd[0].isONE()) {
            logger.info("constructor: gcd = {}", Arrays.toString(gcd)); // + ", {}", n + ", " +d);
            n = gcd[1];
            d = gcd[2];
        }
        gcd = ring.fdengine.rightGcdCofactors(r.ring, n, d);
        if (!gcd[0].isONE()) {
            logger.info("constructor: gcd = {}", Arrays.toString(gcd)); // + ", {}", n + ", " +d);
            n = gcd[1];
            d = gcd[2];
        }
        // not perfect, TODO improve
        GenSolvablePolynomial<C>[] simp = ring.engine.leftSimplifier(n, d);
        logger.info("simp: {}, {}, {}", Arrays.toString(simp), n, d);
        num = simp[0];
        den = simp[1];
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public SolvableLocalRing<C> factory() {
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
    public SolvableLocal<C> copy() {
        return new SolvableLocal<C>(ring, num, den, true);
    }


    /**
     * Is SolvableLocal zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return num.isZERO();
    }


    /**
     * Is SolvableLocal one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return num.compareTo(den) == 0;
    }


    /**
     * Is SolvableLocal unit.
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
        GenSolvablePolynomial<C> p = ring.ideal.normalform(num);
        boolean u = (p != null && !p.isZERO());
        if (u) {
            isunit = 1;
        } else {
            isunit = 0;
        }
        return u;
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
        return "SolvableLocal[ " + num.toString() + " | " + den.toString() + " ]";
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
     * SolvableLocal comparison.
     * @param b SolvableLocal.
     * @return sign(this-b).
     */
    @Override
    public int compareTo(SolvableLocal<C> b) {
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
        GenSolvablePolynomial<C> r, s;
        // if (den.isONE()) { }
        // if (b.den.isONE()) { }
        GenSolvablePolynomial<C>[] oc = ring.engine.leftOreCond(den, b.den);
        if (debug) {
            System.out.println("oc[0] den =<>= oc[1] b.den: (" + oc[0] + ") (" + den + ") = (" + oc[1]
                            + ") (" + b.den + ")");
        }
        //System.out.println("oc[0] = " + oc[0]);
        //System.out.println("oc[1] = " + oc[1]);
        r = oc[0].multiply(num);
        s = oc[1].multiply(b.num);
        return r.compareTo(s);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof SolvableLocal)) {
            return false;
        }
        SolvableLocal<C> a = null;
        try {
            a = (SolvableLocal<C>) b;
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
     * SolvableLocal absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public SolvableLocal<C> abs() {
        return new SolvableLocal<C>(ring, (GenSolvablePolynomial<C>) num.abs(), den, true);
    }


    /**
     * SolvableLocal summation.
     * @param S SolvableLocal.
     * @return this+S.
     */
    public SolvableLocal<C> sum(SolvableLocal<C> S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        GenSolvablePolynomial<C> n, d, n1, n2;
        if (den.isONE() && S.den.isONE()) {
            n = (GenSolvablePolynomial<C>) num.sum(S.num);
            return new SolvableLocal<C>(ring, n, den, true);
        }
        /* wrong:
        if (den.isONE()) { }
        if (S.den.isONE()) { }
        */
        if (den.compareTo(S.den) == 0) { // correct ?
            n = (GenSolvablePolynomial<C>) num.sum(S.num);
            return new SolvableLocal<C>(ring, n, den, false);
        }
        // general case
        GenSolvablePolynomial<C>[] oc = ring.engine.leftOreCond(den, S.den);
        if (debug) {
            System.out.println("oc[0] den =sum= oc[1] S.den: (" + oc[0] + ") (" + den + ") = (" + oc[1]
                            + ") (" + S.den + ")");
        }
        //System.out.println("oc[0] = " + oc[0]);
        //System.out.println("oc[1] = " + oc[1]);
        d = oc[0].multiply(den);
        n1 = oc[0].multiply(num);
        n2 = oc[1].multiply(S.num);
        n = (GenSolvablePolynomial<C>) n1.sum(n2);
        //System.out.println("n = " + n);
        //System.out.println("d = " + d);
        return new SolvableLocal<C>(ring, n, d, false);
    }


    /**
     * SolvableLocal negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public SolvableLocal<C> negate() {
        return new SolvableLocal<C>(ring, (GenSolvablePolynomial<C>) num.negate(), den, true);
    }


    /**
     * SolvableLocal signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return num.signum();
    }


    /**
     * SolvableLocal subtraction.
     * @param S SolvableLocal.
     * @return this-S.
     */
    public SolvableLocal<C> subtract(SolvableLocal<C> S) {
        return sum(S.negate());
    }


    /**
     * SolvableLocal division.
     * @param S SolvableLocal.
     * @return this/S.
     */
    public SolvableLocal<C> divide(SolvableLocal<C> S) {
        return multiply(S.inverse());
    }


    /**
     * SolvableLocal inverse.
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this if defined.
     */
    public SolvableLocal<C> inverse() {
        if (isONE()) {
            return this;
        }
        if (isUnit()) {
            return new SolvableLocal<C>(ring, den, num, true);
        }
        throw new ArithmeticException("element not invertible " + this);
    }


    /**
     * SolvableLocal remainder.
     * @param S SolvableLocal.
     * @return this - (this/S)*S.
     */
    public SolvableLocal<C> remainder(SolvableLocal<C> S) {
        if (S.isUnit()) {
            return ring.getZERO();
        }
        throw new UnsupportedOperationException("remainder not implemented" + S);
    }


    /**
     * SolvableLocal multiplication.
     * @param S SolvableLocal.
     * @return this*S.
     */
    public SolvableLocal<C> multiply(SolvableLocal<C> S) {
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
            return new SolvableLocal<C>(ring, n, den, true);
        }
        /* wrong:
        if (den.isONE()) { }
        if (S.den.isONE()) { }
        if ( den.compareTo(S.den) == 0 ) { }
        */
        GenSolvablePolynomial<C>[] oc = ring.engine.leftOreCond(num, S.den);
        if (debug) {
            System.out.println("oc[0] num =mult= oc[1] S.den: (" + oc[0] + ") (" + num + ") = (" + oc[1]
                            + ") (" + S.den + ")");
        }
        //System.out.println("oc[0] = " + oc[0]);
        //System.out.println("oc[1] = " + oc[1]);
        n = oc[1].multiply(S.num);
        d = oc[0].multiply(den);
        return new SolvableLocal<C>(ring, n, d, false);
    }


    /**
     * SolvableLocal multiplication by GenSolvablePolynomial.
     * @param b GenSolvablePolynomial.
     * @return this*b.
     */
    public SolvableLocal<C> multiply(GenSolvablePolynomial<C> b) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (num.isZERO()) {
            return this;
        }
        if (b.isONE()) {
            return this;
        }
        SolvableLocal<C> B = new SolvableLocal<C>(ring, b);
        return multiply(B);
    }


    /**
     * SolvableLocal multiplication by coefficient.
     * @param b coefficient.
     * @return this*b.
     */
    public SolvableLocal<C> multiply(C b) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (num.isZERO()) {
            return this;
        }
        if (b.isONE()) {
            return this;
        }
        GenSolvablePolynomial<C> B = ring.ring.getONE().multiply(b);
        return multiply(B);
    }


    /**
     * SolvableLocal multiplication by exponent.
     * @param e exponent vector.
     * @return this*b.
     */
    public SolvableLocal<C> multiply(ExpVector e) {
        if (e == null || e.isZERO()) {
            return this;
        }
        if (num.isZERO()) {
            return this;
        }
        GenSolvablePolynomial<C> B = ring.ring.getONE().multiply(e);
        return multiply(B);
    }


    /**
     * SolvableLocal monic.
     * @return this with monic value part.
     */
    public SolvableLocal<C> monic() {
        if (num.isZERO()) {
            return this;
        }
        return this;
        // non sense:
        //C lbc = num.leadingBaseCoefficient();
        //lbc = lbc.inverse();
        //GenSolvablePolynomial<C> n = num.multiply(lbc);
        //GenSolvablePolynomial<C> d = den.multiply(lbc);
        //return new SolvableLocal<C>(ring, n, d, true);
    }


    /**
     * Greatest common divisor.
     * @param b other element.
     * @return gcd(this,b).
     */
    public SolvableLocal<C> gcd(SolvableLocal<C> b) {
        throw new UnsupportedOperationException("gcd not implemented " + this.getClass().getName());
    }


    /**
     * Extended greatest common divisor. <b>Note: </b>Not implemented, throws
     * UnsupportedOperationException.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public SolvableLocal<C>[] egcd(SolvableLocal<C> b) {
        throw new UnsupportedOperationException("egcd not implemented " + this.getClass().getName());
    }

}
