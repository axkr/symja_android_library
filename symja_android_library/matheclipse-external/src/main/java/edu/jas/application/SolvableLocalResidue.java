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
 * SolvableLocalResidue, that is a (left) rational function, based on pairs of
 * GenSolvablePolynomial with GcdRingElem interface. Objects of this class are
 * immutable.
 * @author Heinz Kredel
 */
public class SolvableLocalResidue<C extends GcdRingElem<C>> implements GcdRingElem<SolvableLocalResidue<C>>,
                QuotPair<GenPolynomial<C>> {


    // Can not extend SolvableLocal or SolvableQuotient because of 
    // different constructor semantics.


    private static final Logger logger = LogManager.getLogger(SolvableLocalResidue.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * SolvableLocalResidue class factory data structure.
     */
    public final SolvableLocalResidueRing<C> ring;


    /**
     * Numerator part of the element data structure.
     */
    public final GenSolvablePolynomial<C> num;


    /**
     * Denominator part of the element data structure.
     */
    public final GenSolvablePolynomial<C> den;


    /**
     * The constructor creates a SolvableLocalResidue object from a ring
     * factory.
     * @param r ring factory.
     */
    public SolvableLocalResidue(SolvableLocalResidueRing<C> r) {
        this(r, r.ring.getZERO());
    }


    /**
     * The constructor creates a SolvableLocalResidue object from a ring factory
     * and a numerator polynomial. The denominator is assumed to be 1.
     * @param r ring factory.
     * @param n numerator solvable polynomial.
     */
    public SolvableLocalResidue(SolvableLocalResidueRing<C> r, GenSolvablePolynomial<C> n) {
        this(r, n, r.ring.getONE(), false); // false because of normalform
    }


    /**
     * The constructor creates a SolvableLocalResidue object from a ring factory
     * and a numerator and denominator solvable polynomial.
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     */
    public SolvableLocalResidue(SolvableLocalResidueRing<C> r, GenSolvablePolynomial<C> n,
                    GenSolvablePolynomial<C> d) {
        this(r, n, d, false);
    }


    /**
     * The constructor creates a SolvableLocalResidue object from a ring factory
     * and a numerator and denominator polynomial.
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     * @param isred <em>unused at the moment</em>.
     */
    protected SolvableLocalResidue(SolvableLocalResidueRing<C> r, GenSolvablePolynomial<C> n,
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
        GenSolvablePolynomial<C> p = ring.ideal.normalform(d);
        if (p.isZERO()) {
            throw new IllegalArgumentException("denominator may not be in ideal, d = " + d);
        }
        //d = p; // not always working
        GenSolvablePolynomial<C> nr = ring.ideal.normalform(n); // leftNF
        if (nr.isZERO()) {
            num = nr;
            den = ring.ring.getONE();
            return;
        }
        //logger.info("constructor: n = {}, NF(n) = {}", n, nr);
        //n = nr; // not always working, failed
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
    public SolvableLocalResidueRing<C> factory() {
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
    public SolvableLocalResidue<C> copy() {
        return new SolvableLocalResidue<C>(ring, num, den, true);
    }


    /**
     * Is SolvableLocalResidue zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return num.isZERO();
    }


    /**
     * Is SolvableLocalResidue one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return num.compareTo(den) == 0;
    }


    /**
     * Is SolvableLocalResidue a unit.
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
     * Is Quotient a constant.
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
        return "SolvableLocalResidue[ " + num.toString() + " | " + den.toString() + " ]";
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
     * SolvableLocalResidue comparison.
     * @param b SolvableLocalResidue.
     * @return sign(this-b).
     */
    @Override
    public int compareTo(SolvableLocalResidue<C> b) {
        if (b == null || b.isZERO()) {
            return this.signum();
        }
        if (this.isZERO()) {
            return -b.signum();
        }
        return this.subtract(b).signum();
        // GenSolvablePolynomial<C> n, p, q;
        // if ( den.compareTo(b.den) == 0 ) {
        //     n = (GenSolvablePolynomial<C>) num.subtract(b.num);
        //     //\\ p = ring.ideal.normalform(n);
        //     //logger.info("p.signum() = {}", p.signum());
        //     return p.signum();
        // }
        // GenSolvablePolynomial<C> r, s;
        // // if (den.isONE()) { }
        // // if (b.den.isONE()) { }
        // GenSolvablePolynomial<C>[] oc = ring.engine.leftOreCond(den,b.den);
        // if (debug) {
        //     logger.info("oc[0] den =<>= oc[1] b.den: ({}", oc[0] + ") ({}", den + ") = ({}", oc[1]
        //                 + ") ({}", b.den + ")");
        // }
        // q = oc[0].multiply(den); 
        // q = ring.ideal.normalform(q);
        // int t = q.signum(); //oc[0].signum() * den.signum(); // sign only
        // r = oc[0].multiply(num);
        // s = oc[1].multiply(b.num);
        // p = (GenSolvablePolynomial<C>) r.subtract(s);
        // //\\ p = ring.ideal.normalform(p);
        // //logger.info("p.signum() = {}", p.signum());
        // if ( t == 0 ) {
        //     throw new RuntimeException("can not happen: denominator is zero: this " + this + ", b = " + b);
        // } 
        // return t * p.signum();
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof SolvableLocalResidue)) {
            return false;
        }
        SolvableLocalResidue<C> a = null;
        try {
            a = (SolvableLocalResidue<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
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
     * SolvableLocalResidue absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public SolvableLocalResidue<C> abs() {
        return new SolvableLocalResidue<C>(ring, (GenSolvablePolynomial<C>) num.abs(), den, true);
    }


    /**
     * SolvableLocalResidue summation.
     * @param S SolvableLocalResidue.
     * @return this+S.
     */
    public SolvableLocalResidue<C> sum(SolvableLocalResidue<C> S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        GenSolvablePolynomial<C> n, d, n1, n2;
        if (den.isONE() && S.den.isONE()) {
            n = (GenSolvablePolynomial<C>) num.sum(S.num);
            return new SolvableLocalResidue<C>(ring, n, den, false); // true
        }
        /* wrong:
        if (den.isONE()) { }
        if (S.den.isONE()) { }
        */
        if (den.compareTo(S.den) == 0) { // correct ?
            n = (GenSolvablePolynomial<C>) num.sum(S.num);
            return new SolvableLocalResidue<C>(ring, n, den, false);
        }
        // general case
        GenSolvablePolynomial<C>[] oc = ring.engine.leftOreCond(den, S.den);
        if (debug) {
            logger.info("oc[0] den =sum= oc[1] S.den: ({}) ({}) = ({}) ({})", oc[0], den, oc[1], S.den);
        }
        d = oc[0].multiply(den);
        n1 = oc[0].multiply(num);
        n2 = oc[1].multiply(S.num);
        n = (GenSolvablePolynomial<C>) n1.sum(n2);
        //logger.info("n = {}, d = {}", n, d);
        return new SolvableLocalResidue<C>(ring, n, d, false);
    }


    /**
     * SolvableLocalResidue negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public SolvableLocalResidue<C> negate() {
        return new SolvableLocalResidue<C>(ring, (GenSolvablePolynomial<C>) num.negate(), den, true);
    }


    /**
     * SolvableLocalResidue signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        // assume sign(den) > 0
        return num.signum();
    }


    /**
     * SolvableLocalResidue subtraction.
     * @param S SolvableLocalResidue.
     * @return this-S.
     */
    public SolvableLocalResidue<C> subtract(SolvableLocalResidue<C> S) {
        return sum(S.negate());
    }


    /**
     * SolvableLocalResidue division.
     * @param S SolvableLocalResidue.
     * @return this/S.
     */
    public SolvableLocalResidue<C> divide(SolvableLocalResidue<C> S) {
        return multiply(S.inverse());
    }


    /**
     * SolvableLocalResidue inverse.
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this.
     */
    public SolvableLocalResidue<C> inverse() {
        if (num.isZERO()) {
            throw new ArithmeticException("element not invertible " + this);
        }
        return new SolvableLocalResidue<C>(ring, den, num, false); // true
    }


    /**
     * SolvableLocalResidue remainder.
     * @param S SolvableLocalResidue.
     * @return this - (this/S)*S.
     */
    public SolvableLocalResidue<C> remainder(SolvableLocalResidue<C> S) {
        if (S.isZERO()) {
            throw new ArithmeticException("element not invertible " + S);
        }
        return ring.getZERO();
    }


    /**
     * SolvableLocalResidue multiplication.
     * @param S SolvableLocalResidue.
     * @return this*S.
     */
    public SolvableLocalResidue<C> multiply(SolvableLocalResidue<C> S) {
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
            return new SolvableLocalResidue<C>(ring, n, den, false); // true
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
        n = oc[1].multiply(S.num);
        d = oc[0].multiply(den);
        return new SolvableLocalResidue<C>(ring, n, d, false);
    }


    /**
     * SolvableLocalResidue multiplication by GenSolvablePolynomial.
     * @param b GenSolvablePolynomial<C>.
     * @return this*b.
     */
    public SolvableLocalResidue<C> multiply(GenSolvablePolynomial<C> b) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (num.isZERO()) {
            return this;
        }
        if (b.isONE()) {
            return this;
        }
        SolvableLocalResidue<C> B = new SolvableLocalResidue<C>(ring, b);
        return multiply(B);
    }


    /**
     * SolvableLocalResidue multiplication by coefficient.
     * @param b coefficient.
     * @return this*b.
     */
    public SolvableLocalResidue<C> multiply(C b) {
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
     * SolvableLocalResidue multiplication by exponent.
     * @param e exponent vector.
     * @return this*b.
     */
    public SolvableLocalResidue<C> multiply(ExpVector e) {
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
     * SolvableLocalResidue monic.
     * @return this with monic value part.
     */
    public SolvableLocalResidue<C> monic() {
        if (num.isZERO()) {
            return this;
        }
        return this;
    }


    /**
     * Greatest common divisor.
     * @param b other element.
     * @return gcd(this,b).
     */
    public SolvableLocalResidue<C> gcd(SolvableLocalResidue<C> b) {
        if (b == null || b.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return b;
        }
        return ring.getONE();
    }


    /**
     * Extended greatest common divisor.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    @SuppressWarnings("unchecked")
    public SolvableLocalResidue<C>[] egcd(SolvableLocalResidue<C> b) {
        SolvableLocalResidue<C>[] ret = (SolvableLocalResidue<C>[]) new SolvableLocalResidue[3];
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
