/*
 * $Id$
 */

package edu.jas.arith;


import java.io.Reader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import edu.jas.kern.StringUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.StarRingElem;


/**
 * BigComplex class based on BigDecimal implementing the RingElem respectively
 * the StarRingElem interface. Objects of this class are immutable.
 * @author Heinz Kredel
 */
public final class BigDecimalComplex implements StarRingElem<BigDecimalComplex>,
                GcdRingElem<BigDecimalComplex>, RingFactory<BigDecimalComplex> {


    /**
     * Real part of the data structure.
     */
    public final BigDecimal re;


    /**
     * Imaginary part of the data structure.
     */
    public final BigDecimal im;


    private final static Random random = new Random();


    private static final Logger logger = Logger.getLogger(BigDecimalComplex.class);


    /**
     * The constructor creates a BigDecimalComplex object from two BigDecimal
     * objects real and imaginary part.
     * @param r real part.
     * @param i imaginary part.
     */
    public BigDecimalComplex(BigDecimal r, BigDecimal i) {
        re = r;
        im = i;
    }


    /**
     * The constructor creates a BigDecimalComplex object from a BigDecimal
     * object as real part, the imaginary part is set to 0.
     * @param r real part.
     */
    public BigDecimalComplex(BigDecimal r) {
        this(r, BigDecimal.ZERO);
    }


    /**
     * The constructor creates a BigDecimalComplex object from a long element as
     * real part, the imaginary part is set to 0.
     * @param r real part.
     */
    public BigDecimalComplex(long r) {
        this(new BigDecimal(r), BigDecimal.ZERO);
    }


    /**
     * The constructor creates a BigDecimalComplex object with real part 0 and
     * imaginary part 0.
     */
    public BigDecimalComplex() {
        this(BigDecimal.ZERO);
    }


    /**
     * The constructor creates a BigDecimalComplex object from a String
     * representation.
     * @param s string of a BigDecimalComplex.
     * @throws NumberFormatException
     */
    public BigDecimalComplex(String s) throws NumberFormatException {
        if (s == null || s.length() == 0) {
            re = BigDecimal.ZERO;
            im = BigDecimal.ZERO;
            return;
        }
        s = s.trim();
        int i = s.indexOf("i");
        if (i < 0) {
            re = new BigDecimal(s);
            im = BigDecimal.ZERO;
            return;
        }
        //logger.warn("String constructor not done");
        String sr = "";
        if (i > 0) {
            sr = s.substring(0, i);
        }
        String si = "";
        if (i < s.length()) {
            si = s.substring(i + 1, s.length());
        }
        //int j = sr.indexOf("+");
        re = new BigDecimal(sr.trim());
        im = new BigDecimal(si.trim());
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public BigDecimalComplex factory() {
        return this;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<BigDecimalComplex> generators() {
        List<BigDecimalComplex> g = new ArrayList<BigDecimalComplex>(2);
        g.add(getONE());
        g.add(getIMAG());
        return g;
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return false;
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public BigDecimalComplex copy() {
        return new BigDecimalComplex(re, im);
    }


    /**
     * Copy BigDecimalComplex element c.
     * @param c BigDecimalComplex.
     * @return a copy of c.
     */
    public BigDecimalComplex copy(BigDecimalComplex c) {
        return new BigDecimalComplex(c.re, c.im);
    }


    /**
     * Get the zero element.
     * @return 0 as BigDecimalComplex.
     */
    public BigDecimalComplex getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as BigDecimalComplex.
     */
    public BigDecimalComplex getONE() {
        return ONE;
    }


    /**
     * Get the i element.
     * @return i as BigDecimalComplex.
     */
    public BigDecimalComplex getIMAG() {
        return I;
    }


    /**
     * Query if this ring is commutative.
     * @return true.
     */
    public boolean isCommutative() {
        return true;
    }


    /**
     * Query if this ring is associative.
     * @return true.
     */
    public boolean isAssociative() {
        return true;
    }


    /**
     * Query if this ring is a field.
     * @return true.
     */
    public boolean isField() {
        return true;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return java.math.BigInteger.ZERO;
    }


    /**
     * Get a BigDecimalComplex element from a BigInteger.
     * @param a BigInteger.
     * @return a BigDecimalComplex.
     */
    public BigDecimalComplex fromInteger(BigInteger a) {
        return new BigDecimalComplex(new BigDecimal(a));
    }


    /**
     * Get a BigDecimalComplex element from a long.
     * @param a long.
     * @return a BigDecimalComplex.
     */
    public BigDecimalComplex fromInteger(long a) {
        return new BigDecimalComplex(new BigDecimal(a));
    }


    /**
     * The constant 0.
     */
    public static final BigDecimalComplex ZERO = new BigDecimalComplex();


    /**
     * The constant 1.
     */
    public static final BigDecimalComplex ONE = new BigDecimalComplex(BigDecimal.ONE);


    /**
     * The constant i.
     */
    public static final BigDecimalComplex I = new BigDecimalComplex(BigDecimal.ZERO, BigDecimal.ONE);


    /**
     * Get the real part.
     * @return re.
     */
    public BigDecimal getRe() {
        return re;
    }


    /**
     * Get the imaginary part.
     * @return im.
     */
    public BigDecimal getIm() {
        return im;
    }


    /**
     * Get the String representation.
     */
    @Override
    public String toString() {
        String s = re.toString();
        //int i = im.compareTo(BigDecimal.ZERO);
        //logger.info("compareTo "+im+" ? 0 = "+i);
        if (im.isZERO()) {
            return s;
        }
        s += "i" + im;
        return s;
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case: re or re+im*i 
        // was (re,im) or (re,) 
        StringBuffer s = new StringBuffer();
        boolean iz = im.isZERO();
        if (iz) {
            s.append(re.toScript());
            return s.toString();
        }
        boolean rz = re.isZERO();
        if (rz) {
            if (!im.isONE()) {
                if (im.signum() > 0) {
                    s.append(im.toScript() + "*");
                } else {
                    s.append("-");
                    BigDecimal ii = im.negate();
                    if (!ii.isONE()) {
                        s.append(ii.toScript() + "*");
                    }
                }
            }
        } else {
            s.append(re.toScript());
            if (im.signum() > 0) {
                s.append("+");
                if (!im.isONE()) {
                    s.append(im.toScript() + "*");
                }
            } else {
                s.append("-");
                BigDecimal ii = im.negate();
                if (!ii.isONE()) {
                    s.append(ii.toScript() + "*");
                }
            }
        }
        s.append("I");
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    @Override
    public String toScriptFactory() {
        // Python case
        return "CD()";
    }


    /**
     * Complex number zero.
     * @param A is a complex number.
     * @return If A is 0 then true is returned, else false.
     */
    public static boolean isCZERO(BigDecimalComplex A) {
        if (A == null) {
            return false;
        }
        return A.isZERO();
    }


    /**
     * Is Complex number zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return re.isZERO() && im.isZERO();
    }


    /**
     * Complex number one.
     * @param A is a complex number.
     * @return If A is 1 then true is returned, else false.
     */
    public static boolean isCONE(BigDecimalComplex A) {
        if (A == null) {
            return false;
        }
        return A.isONE();
    }


    /**
     * Is Complex number one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return re.isONE() && im.isZERO();
    }


    /**
     * Is Complex imaginary one.
     * @return If this is i then true is returned, else false.
     */
    public boolean isIMAG() {
        return re.isZERO() && im.isONE();
    }


    /**
     * Is Complex unit element.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        return (!isZERO());
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof BigDecimalComplex)) {
            return false;
        }
        BigDecimalComplex bc = (BigDecimalComplex) b;
        //return re.equals(bc.re) && im.equals(bc.im);
        return re.compareTo(bc.re) == 0 && im.compareTo(bc.im) == 0;
    }


    /**
     * Hash code for this BigDecimalComplex.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * re.hashCode() + im.hashCode();
    }


    /**
     * Since complex numbers are unordered, we use lexicographical order of re
     * and im.
     * @return 0 if this is equal to b; 1 if re &gt; b.re, or re == b.re and im
     *         &gt; b.im; -1 if re &lt; b.re, or re == b.re and im &lt; b.im
     */
    @Override
    public int compareTo(BigDecimalComplex b) {
        int s = re.compareTo(b.re);
        //System.out.println("compareTo(a.re,b.re) = " + s);
        if (s != 0) {
            return s;
        }
        s = im.compareTo(b.im);
        //System.out.println("compareTo(a.im,b.im) = " + s);
        return s;
    }


    /**
     * Since complex numbers are unordered, we use lexicographical order of re
     * and im.
     * @return 0 if this is equal to 0; 1 if re &gt; 0, or re == 0 and im &gt;
     *         0; -1 if re &lt; 0, or re == 0 and im &lt; 0
     * @see edu.jas.structure.RingElem#signum()
     */
    public int signum() {
        int s = re.signum();
        if (s != 0) {
            return s;
        }
        return im.signum();
    }


    /* arithmetic operations: +, -, -
     */

    /**
     * Complex number summation.
     * @param B a BigDecimalComplex number.
     * @return this+B.
     */
    public BigDecimalComplex sum(BigDecimalComplex B) {
        return new BigDecimalComplex(re.sum(B.re), im.sum(B.im));
    }


    /**
     * Complex number sum.
     * @param A and B are complex numbers.
     * @return A+B.
     */
    public static BigDecimalComplex CSUM(BigDecimalComplex A, BigDecimalComplex B) {
        if (A == null) {
            return null;
        }
        return A.sum(B);
    }


    /**
     * Complex number difference.
     * @param A and B are complex numbers.
     * @return A-B.
     */
    public static BigDecimalComplex CDIF(BigDecimalComplex A, BigDecimalComplex B) {
        if (A == null) {
            return null;
        }
        return A.subtract(B);
    }


    /**
     * Complex number subtract.
     * @param B a BigDecimalComplex number.
     * @return this-B.
     */
    public BigDecimalComplex subtract(BigDecimalComplex B) {
        return new BigDecimalComplex(re.subtract(B.re), im.subtract(B.im));
    }


    /**
     * Complex number negative.
     * @param A is a complex number.
     * @return -A
     */
    public static BigDecimalComplex CNEG(BigDecimalComplex A) {
        if (A == null) {
            return null;
        }
        return A.negate();
    }


    /**
     * Complex number negative.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public BigDecimalComplex negate() {
        return new BigDecimalComplex(re.negate(), im.negate());
    }


    /**
     * Complex number conjugate.
     * @param A is a complex number.
     * @return the complex conjugate of A.
     */
    public static BigDecimalComplex CCON(BigDecimalComplex A) {
        if (A == null) {
            return null;
        }
        return A.conjugate();
    }


    /* arithmetic operations: conjugate, absolut value 
     */

    /**
     * Complex number conjugate.
     * @return the complex conjugate of this.
     */
    public BigDecimalComplex conjugate() {
        return new BigDecimalComplex(re, im.negate());
    }


    /**
     * Complex number norm.
     * @see edu.jas.structure.StarRingElem#norm()
     * @return ||this||.
     */
    public BigDecimalComplex norm() {
        // this.conjugate().multiply(this);
        BigDecimal v = re.multiply(re);
        if (!im.isZERO()) {
            v = v.sum(im.multiply(im));
        }
        return new BigDecimalComplex(v);
    }


    /**
     * Complex number absolute value.
     * @see edu.jas.structure.RingElem#abs()
     * @return |this|.
     */
    public BigDecimalComplex abs() {
        if (im.isZERO()) {
            return new BigDecimalComplex(re.abs());
        }
        BigDecimalComplex n = norm();
        BigDecimal d = Roots.sqrt(n.re);
        if (logger.isDebugEnabled()) {
            logger.debug("sqrt(re) = " + d);
        }
        return new BigDecimalComplex(d);
    }


    /**
     * Complex number absolute value.
     * @param A is a complex number.
     * @return the absolute value of A, a rational number. Note: The square root
     *         is not jet implemented.
     */
    public static BigDecimal CABS(BigDecimalComplex A) {
        if (A == null) {
            return null;
        }
        return A.abs().re;
    }


    /**
     * Complex number product.
     * @param A and B are complex numbers.
     * @return A*B.
     */
    public static BigDecimalComplex CPROD(BigDecimalComplex A, BigDecimalComplex B) {
        if (A == null) {
            return null;
        }
        return A.multiply(B);
    }


    /* arithmetic operations: *, inverse, / 
     */


    /**
     * Complex number product.
     * @param B is a complex number.
     * @return this*B.
     */
    public BigDecimalComplex multiply(BigDecimalComplex B) {
        return new BigDecimalComplex(re.multiply(B.re).subtract(im.multiply(B.im)), re.multiply(B.im).sum(
                        im.multiply(B.re)));
    }


    /**
     * Complex number inverse.
     * @param A is a non-zero complex number.
     * @return S with S*A = 1.
     */
    public static BigDecimalComplex CINV(BigDecimalComplex A) {
        if (A == null) {
            return null;
        }
        return A.inverse();
    }


    /**
     * Complex number inverse.
     * @return S with S*this = 1.
     * @see edu.jas.structure.RingElem#inverse()
     */
    public BigDecimalComplex inverse() {
        BigDecimal a = norm().re.inverse();
        return new BigDecimalComplex(re.multiply(a), im.multiply(a.negate()));
    }


    /**
     * Complex number inverse.
     * @param S is a complex number.
     * @return 0.
     */
    public BigDecimalComplex remainder(BigDecimalComplex S) {
        if (S.isZERO()) {
            throw new ArithmeticException("division by zero");
        }
        return ZERO;
    }


    /**
     * Complex number quotient.
     * @param A and B are complex numbers, B non-zero.
     * @return A/B.
     */
    public static BigDecimalComplex CQ(BigDecimalComplex A, BigDecimalComplex B) {
        if (A == null) {
            return null;
        }
        return A.divide(B);
    }


    /**
     * Complex number divide.
     * @param B is a complex number, non-zero.
     * @return this/B.
     */
    public BigDecimalComplex divide(BigDecimalComplex B) {
        return this.multiply(B.inverse());
    }


    /**
     * Quotient and remainder by division of this by S.
     * @param S a complex number
     * @return [this/S, this - (this/S)*S].
     */
    public BigDecimalComplex[] quotientRemainder(BigDecimalComplex S) {
        return new BigDecimalComplex[] { divide(S), ZERO };
    }


    /**
     * Complex number, random. Random rational numbers A and B are generated
     * using random(n). Then R is the complex number with real part A and
     * imaginary part B.
     * @param n such that 0 &le; A, B &le; (2<sup>n</sup>-1).
     * @return R.
     */
    public BigDecimalComplex random(int n) {
        return random(n, random);
    }


    /**
     * Complex number, random. Random rational numbers A and B are generated
     * using random(n). Then R is the complex number with real part A and
     * imaginary part B.
     * @param n such that 0 &le; A, B &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return R.
     */
    public BigDecimalComplex random(int n, Random rnd) {
        BigDecimal r = BigDecimal.ONE.random(n, rnd);
        BigDecimal i = BigDecimal.ONE.random(n, rnd);
        return new BigDecimalComplex(r, i);
    }


    /**
     * Complex number, random. Random rational numbers A and B are generated
     * using random(n). Then R is the complex number with real part A and
     * imaginary part B.
     * @param n such that 0 &le; A, B &le; (2<sup>n</sup>-1).
     * @return R.
     */
    public static BigDecimalComplex CRAND(int n) {
        return ONE.random(n, random);
    }


    /**
     * Parse complex number from string.
     * @param s String.
     * @return BigDecimalComplex from s.
     */
    public BigDecimalComplex parse(String s) {
        return new BigDecimalComplex(s);
    }


    /**
     * Parse complex number from Reader.
     * @param r Reader.
     * @return next BigDecimalComplex from r.
     */
    public BigDecimalComplex parse(Reader r) {
        return parse(StringUtil.nextString(r));
    }


    /**
     * Complex number greatest common divisor.
     * @param S BigDecimalComplex.
     * @return gcd(this,S).
     */
    public BigDecimalComplex gcd(BigDecimalComplex S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        return ONE;
    }


    /**
     * BigDecimalComplex extended greatest common divisor.
     * @param S BigDecimalComplex.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    public BigDecimalComplex[] egcd(BigDecimalComplex S) {
        BigDecimalComplex[] ret = new BigDecimalComplex[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if (S == null || S.isZERO()) {
            ret[0] = this;
            return ret;
        }
        if (this.isZERO()) {
            ret[0] = S;
            return ret;
        }
        BigDecimalComplex half = fromInteger(2).inverse();
        ret[0] = ONE;
        ret[1] = this.inverse().multiply(half);
        ret[2] = S.inverse().multiply(half);
        return ret;
    }

}
