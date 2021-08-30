/*
 * $Id$
 */

package edu.jas.arith;


import java.io.Reader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.kern.StringUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.StarRingElem;


/**
 * BigOctonion class based on BigRational implementing the RingElem interface
 * and with the familiar MAS static method names. Objects of this class are
 * immutable.
 * @author Heinz Kredel
 */

public final class BigOctonion
                implements StarRingElem<BigOctonion>, GcdRingElem<BigOctonion>, RingFactory<BigOctonion> {


    /**
     * First part of the data structure.
     */
    public final BigQuaternion or;


    /**
     * Second part of the data structure.
     */
    public final BigQuaternion oi;


    private final static Random random = new Random();


    private static final Logger logger = LogManager.getLogger(BigOctonion.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor for a BigOctonion from Quaternions.
     * @param r BigQuaternion.
     * @param i BigQuaternion.
     */
    public BigOctonion(BigQuaternion r, BigQuaternion i) {
        if (i == null) {
            throw new IllegalArgumentException("null i not allowed");
        }
        this.or = r;
        this.oi = i;
        //if (ZERO == null) {
        //ZERO = new BigOctonion(r.ring.ZERO, i.ring.ZERO);
        //ONE = new BigOctonion(r.ring.ONE, i.ring.ZERO);
        //I = new BigOctonion(r.ring.ZERO, i.ring.ONE);
        //}
    }


    /**
     * Constructor for a BigOctonion from BigQuaternion.
     * @param r BigQuaternion.
     */
    public BigOctonion(BigQuaternion r) {
        this(r, r.ring.ZERO);
    }


    /**
     * Constructor for a BigOctonion from BigComplex.
     * @param fac BigQuaternionRing.
     * @param r BigComplex.
     */
    public BigOctonion(BigQuaternionRing fac, BigComplex r) {
        this(new BigQuaternion(fac, r));
    }


    /**
     * Constructor for a BigOctonion from BigRational.
     * @param fac BigQuaternionRing.
     * @param r BigRational.
     */
    public BigOctonion(BigQuaternionRing fac, BigRational r) {
        this(new BigQuaternion(fac, r));
    }


    /**
     * Constructor for a BigOctonion from long.
     * @param fac BigQuaternionRing.
     * @param r long.
     */
    public BigOctonion(BigQuaternionRing fac, long r) {
        this(new BigQuaternion(fac, r));
    }


    /**
     * Constructor for a BigOctonion with no arguments.
     * @param fac BigQuaternionRing.
     */
    public BigOctonion(BigQuaternionRing fac) {
        this(new BigQuaternion(fac));
    }


    /**
     * The BigOctonion string constructor accepts the following formats: empty
     * string, "quaternion", or "quat o quat" with no blanks around o if used as
     * polynoial coefficient.
     * @param fac BigQuaternionRing.
     * @param s String.
     * @throws NumberFormatException
     */
    public BigOctonion(BigQuaternionRing fac, String s) throws NumberFormatException {
        if (s == null || s.length() == 0) {
            or = getZERO().or;
            oi = getZERO().oi;
            return;
        }
        s = s.trim();
        int o = s.indexOf("o");
        if (o == -1) {
            or = new BigQuaternion(fac, s);
            oi = getZERO().oi;
            return;
        }
        String sr = s.substring(0, o - 1);
        String so = s.substring(o + 1, s.length());
        or = new BigQuaternion(fac, sr.trim());
        oi = new BigQuaternion(fac, so.trim());
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public BigOctonion factory() {
        return this;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<BigOctonion> generators() {
        List<BigQuaternion> qg = or.ring.generators();
        List<BigOctonion> g = new ArrayList<BigOctonion>(qg.size() * 2);
        for (BigQuaternion q : qg) {
            g.add(new BigOctonion(q));
            g.add(new BigOctonion(or.ring.ZERO, q));
        }
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
    public BigOctonion copy() {
        return new BigOctonion(or, oi);
    }


    /**
     * Copy BigOctonion element c.
     * @param c BigOctonion.
     * @return a copy of c.
     */
    public BigOctonion copy(BigOctonion c) {
        if (c == null) {
            throw new IllegalArgumentException("copy of null not allowed");
        }
        return new BigOctonion(c.or, c.oi);
    }


    /**
     * Get the zero element.
     * @return 0 as BigOctonion.
     */
    public BigOctonion getZERO() {
        if (ZERO == null) {
            ZERO = new BigOctonion(or.ring.ZERO, or.ring.ZERO);
            I = new BigOctonion(or.ring.ZERO, or.ring.ONE);
        }
        return ZERO;
    }


    /**
     * Get the one element.
     * @return q as BigOctonion.
     */
    public BigOctonion getONE() {
        if (ONE == null) {
            ONE = new BigOctonion(or.ring.ONE, or.ring.ZERO);
        }
        return ONE;
    }


    /**
     * Query if this ring is commutative.
     * @return false.
     */
    public boolean isCommutative() {
        return false;
    }


    /**
     * Query if this ring is associative.
     * @return false.
     */
    public boolean isAssociative() {
        return false;
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
     * Get a BigOctonion element from a BigInteger.
     * @param a BigInteger.
     * @return a BigOctonion.
     */
    public BigOctonion fromInteger(BigInteger a) {
        return new BigOctonion(or.ring.fromInteger(a));
    }


    /**
     * Get a BigOctonion element from a long.
     * @param a long.
     * @return a BigOctonion.
     */
    public BigOctonion fromInteger(long a) {
        return new BigOctonion(or.ring.fromInteger(a));
    }


    /**
     * The constant 0.
     */
    public BigOctonion ZERO; // = new BigOctonion(or.ring);


    /**
     * The constant 1.
     */
    public BigOctonion ONE; // = new BigOctonion(or.ring.ONE);


    /**
     * The constant i.
     */
    public BigOctonion I; // = new BigOctonion(or.ring.ZERO, oi.ring.ONE);


    /**
     * Get the or part.
     * @return or.
     */
    public BigQuaternion getR() {
        return or;
    }


    /**
     * Get the oi part.
     * @return oi.
     */
    public BigQuaternion getI() {
        return oi;
    }


    /**
     * Get the string representation. Is compatible with the string constructor.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String s = or.toString();
        boolean i = oi.isZERO();
        if (debug) {
            logger.debug("compareTo " + i + " ? 0 = " + oi);
        }
        if (i) {
            return s;
        }
        s += "o" + oi;
        return s;
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        boolean i = oi.isZERO();
        if (i && or.isZERO()) {
            return "0 ";
        }
        StringBuffer s = new StringBuffer();
        if (!or.isZERO()) {
            String rs = or.toScript();
            rs = rs.replaceAll("Q", "OR");
            s.append(rs);
            s.append(" ");
        }
        if (!i) {
            if (s.length() > 0) {
                s.append("+ ");
            }
            String is = oi.toScript();
            is = is.replaceAll("Q", "OI");
            s.append(is);
        }
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
        return "Oct()";
    }


    /**
     * Is Octonion number zero.
     * @param A BigOctonion.
     * @return true if A is 0, else false.
     */
    public static boolean isOZERO(BigOctonion A) {
        if (A == null)
            return false;
        return A.isZERO();
    }


    /**
     * Is BigOctonion number zero.
     * @return true if this is 0, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return or.isZERO() && oi.isZERO();
    }


    /**
     * Is BigOctonion number one.
     * @param A is a quaternion number.
     * @return true if A is 1, else false.
     */
    public static boolean isOONE(BigOctonion A) {
        if (A == null)
            return false;
        return A.isONE();
    }


    /**
     * Is BigOctonion number one.
     * @see edu.jas.structure.RingElem#isONE()
     * @return true if this is 1, else false.
     */
    public boolean isONE() {
        return or.isONE() && oi.isZERO();
    }


    /**
     * Is BigOctonion imaginary one.
     * @return true if this is i, else false.
     */
    public boolean isIMAG() {
        return or.isZERO() && oi.isONE();
    }


    /**
     * Is BigOctonion unit element.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        return !isZERO();
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof BigOctonion))
            return false;
        BigOctonion B = (BigOctonion) b;
        return or.equals(B.or) && oi.equals(B.oi);
    }


    /**
     * Hash code for this BigOctonion.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = 41 * or.hashCode();
        h += oi.hashCode();
        return h;
    }


    /**
     * Since quaternion numbers are unordered, we use lexicographical order of
     * re, im, jm and km.
     * @param b BigOctonion.
     * @return 0 if b is equal to this, 1 if this is greater b and -1 else.
     */
    @Override
    public int compareTo(BigOctonion b) {
        int s = or.compareTo(b.or);
        if (s != 0) {
            return s;
        }
        return oi.compareTo(b.oi);
    }


    /**
     * Since quaternion numbers are unordered, we use lexicographical order of
     * re, im, jm and km.
     * @return 0 if this is equal to 0; 1 if or &gt; 0, or or == 0 and oi &gt;
     *         0; -1 if or &lt; 0, or or == 0 and oi &lt; 0.
     * @see edu.jas.structure.RingElem#signum()
     */
    public int signum() {
        int s = or.signum();
        if (s != 0) {
            return s;
        }
        return oi.signum();
    }


    /* arithmetic operations: +, -, -
     */

    /**
     * BigOctonion summation.
     * @param B BigOctonion.
     * @return this+B.
     */
    public BigOctonion sum(BigOctonion B) {
        return new BigOctonion(or.sum(B.or), oi.sum(B.oi));
    }


    /**
     * Octonion number sum.
     * @param A BigOctonion.
     * @param B BigOctonion.
     * @return A+B.
     */
    public static BigOctonion OSUM(BigOctonion A, BigOctonion B) {
        if (A == null)
            return null;
        return A.sum(B);
    }


    /**
     * Octonion number difference.
     * @param A BigOctonion.
     * @param B BigOctonion.
     * @return A-B.
     */
    public static BigOctonion ODIF(BigOctonion A, BigOctonion B) {
        if (A == null)
            return null;
        return A.subtract(B);
    }


    /**
     * BigOctonion subtraction.
     * @param B BigOctonion.
     * @return this-B.
     */
    public BigOctonion subtract(BigOctonion B) {
        return new BigOctonion(or.subtract(B.or), oi.subtract(B.oi));
    }


    /**
     * Octonion number negative.
     * @param A is a octonion number
     * @return -A.
     */
    public static BigOctonion ONEG(BigOctonion A) {
        if (A == null)
            return null;
        return A.negate();
    }


    /**
     * BigOctonion number negative.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public BigOctonion negate() {
        return new BigOctonion(or.negate(), oi.negate());
    }


    /**
     * Octonion number conjugate.
     * @param A is a quaternion number.
     * @return the quaternion conjugate of A.
     */
    public static BigOctonion OCON(BigOctonion A) {
        if (A == null)
            return null;
        return A.conjugate();
    }


    /* arithmetic operations: conjugate, absolute value 
     */

    /**
     * BigOctonion conjugate.
     * @return conjugate(this).
     */
    public BigOctonion conjugate() {
        return new BigOctonion(or.conjugate(), oi.negate());
    }


    /**
     * Octonion number norm.
     * @see edu.jas.structure.StarRingElem#norm()
     * @return ||this||.
     */
    public BigOctonion norm() {
        // this.multiply(this.conjugate());
        BigQuaternion v = or.norm();
        v = v.sum(oi.norm());
        return new BigOctonion(v);
    }


    /**
     * Octonion number absolute value.
     * @see edu.jas.structure.RingElem#abs()
     * @return |this|.
     */
    public BigOctonion abs() {
        BigOctonion n = norm();
        BigRational r = Roots.sqrt(n.or.re);
        //logger.error("abs() square root missing");
        return new BigOctonion(new BigQuaternion(n.or.ring, r));
    }


    /**
     * Octonion number absolute value.
     * @param A is a quaternion number.
     * @return the absolute value of A, a rational number. Note: The square root
     *         is not jet implemented.
     */
    public static BigRational OABS(BigOctonion A) {
        if (A == null)
            return null;
        return A.abs().or.re;
    }


    /**
     * Octonion number product.
     * @param A BigOctonion.
     * @param B BigOctonion.
     * @return A*B.
     */
    public static BigOctonion OPROD(BigOctonion A, BigOctonion B) {
        if (A == null)
            return null;
        return A.multiply(B);
    }


    /* arithmetic operations: *, inverse, / 
     */

    /**
     * BigOctonion multiply.
     * @param B BigOctonion.
     * @return this*B.
     */
    public BigOctonion multiply(BigOctonion B) {
        // (r1,i1)(r2,i2) = ( r1 r2 - i2 i1^, r1^ i2 + r2 i1 ) Baez, jas
        // (r1,i1)(r2,i2) = ( r1 r2 - i2^ i1, i1 r2^ + i2 r1 ) Dieudonne, mas
        BigQuaternion r = or.multiply(B.or);
        r = r.subtract(B.oi.multiply(oi.conjugate()));
        BigQuaternion i = or.conjugate().multiply(B.oi);
        i = i.sum(B.or.multiply(oi));
        return new BigOctonion(r, i);
    }


    /**
     * Octonion number inverse.
     * @param A is a non-zero quaternion number.
     * @return S with S * A = 1.
     */
    public static BigOctonion OINV(BigOctonion A) {
        if (A == null)
            return null;
        return A.inverse();
    }


    /**
     * BigOctonion inverse.
     * @return S with S * this = 1.
     * @see edu.jas.structure.RingElem#inverse()
     */
    public BigOctonion inverse() {
        BigRational a = norm().or.re;
        return conjugate().divide(a);
    }


    /**
     * BigOctonion remainder.
     * @param S BigOctonion.
     * @return 0.
     */
    public BigOctonion remainder(BigOctonion S) {
        if (S.isZERO()) {
            throw new ArithmeticException("division by zero");
        }
        return ZERO;
    }


    /**
     * Octonion number quotient.
     * @param A BigOctonion.
     * @param B BigOctonion.
     * @return R/S.
     */
    public static BigOctonion OQ(BigOctonion A, BigOctonion B) {
        if (A == null)
            return null;
        return A.divide(B);
    }


    /**
     * BigOctonion divide.
     * @param b BigOctonion.
     * @return this * b**(-1).
     */
    public BigOctonion divide(BigOctonion b) {
        return rightDivide(b);
    }


    /**
     * BigOctonion right divide.
     * @param b BigOctonion.
     * @return this * b**(-1).
     */
    public BigOctonion rightDivide(BigOctonion b) {
        return this.multiply(b.inverse());
    }


    /**
     * BigOctonion left divide.
     * @param b BigOctonion.
     * @return b**(-1) * this.
     */
    public BigOctonion leftDivide(BigOctonion b) {
        return b.inverse().multiply(this);
    }


    /**
     * BigOctonion divide.
     * @param b BigRational.
     * @return this/b.
     */
    public BigOctonion divide(BigRational b) {
        BigRational bi = b.inverse();
        return new BigOctonion(or.multiply(bi), oi.multiply(bi));
    }


    /**
     * Quotient and remainder by division of this by S.
     * @param S a octonion number
     * @return [this/S, this - (this/S)*S].
     */
    public BigOctonion[] quotientRemainder(BigOctonion S) {
        return new BigOctonion[] { divide(S), ZERO };
    }


    /**
     * BigOctonion random. Random rational numbers A, B, C and D are generated
     * using random(n). Then R is the quaternion number with real part A and
     * imaginary parts B, C and D.
     * @param n such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @return R, a random BigOctonion.
     */
    public BigOctonion random(int n) {
        return random(n, random);
    }


    /**
     * BigOctonion random. Random rational numbers A, B, C and D are generated
     * using RNRAND(n). Then R is the quaternion number with real part A and
     * imaginary parts B, C and D.
     * @param n such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return R, a random BigOctonion.
     */
    public BigOctonion random(int n, Random rnd) {
        BigQuaternion rr = or.ring.random(n, rnd);
        BigQuaternion ir = oi.ring.random(n, rnd);
        return new BigOctonion(rr, ir);
    }


    /*
     * Octonion number, random. Random rational numbers A, B, C and D are
     * generated using RNRAND(n). Then R is the quaternion number with real part
     * A and imaginary parts B, C and D.
     * @param n such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @return R, a random BigOctonion.
    public static BigOctonion ORAND(int n) {
        return random(n, random);
    }
    */

    /**
     * Parse quaternion number from String.
     * @param s String.
     * @return BigOctonion from s.
     */
    public BigOctonion parse(String s) {
        return new BigOctonion(or.ring, s);
    }


    /**
     * Parse quaternion number from Reader.
     * @param r Reader.
     * @return next BigOctonion from r.
     */
    public BigOctonion parse(Reader r) {
        return parse(StringUtil.nextString(r));
    }


    /**
     * Octonion number greatest common divisor.
     * @param S BigOctonion.
     * @return gcd(this,S).
     */
    public BigOctonion gcd(BigOctonion S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        return ONE;
    }


    /**
     * BigOctonion extended greatest common divisor.
     * @param S BigOctonion.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    public BigOctonion[] egcd(BigOctonion S) {
        BigOctonion[] ret = new BigOctonion[3];
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
        BigOctonion half = new BigOctonion(or.ring, new BigRational(1, 2));
        ret[0] = ONE;
        ret[1] = this.inverse().multiply(half);
        ret[2] = S.inverse().multiply(half);
        return ret;
    }


    /**
     * Returns the number of bits in the representation of this BigOctonion,
     * including a sign bit. It is equivalent to
     * {@code or.bitLength() + oi.bitLength()}.)
     * @return number of bits in the representation of this BigOctonion,
     *         including a sign bit.
     */
    public long bitLength() {
        return or.bitLength() + oi.bitLength();
    }

}
