/*
 * $Id: BigQuaternion.java 5680 2017-01-01 16:45:36Z kredel $
 */

package edu.jas.arith;


import java.io.Reader;
// import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import edu.jas.kern.StringUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.StarRingElem;


/**
 * BigQuaternion class based on BigRational implementing the RingElem interface
 * and with the familiar MAS static method names. Objects of this class are
 * immutable. The integer quaternion methods are implemented after
 * https://de.wikipedia.org/wiki/Hurwitzquaternion see also
 * https://en.wikipedia.org/wiki/Hurwitz_quaternion
 * @author Heinz Kredel
 */

public final class BigQuaternion implements StarRingElem<BigQuaternion>, GcdRingElem<BigQuaternion>,
                RingFactory<BigQuaternion> {


    /**
     * Real part of the data structure.
     */
    public final BigRational re; // real part


    /**
     * Imaginary part i of the data structure.
     */
    public final BigRational im; // i imaginary part


    /**
     * Imaginary part j of the data structure.
     */
    public final BigRational jm; // j imaginary part


    /**
     * Imaginary part k of the data structure.
     */
    public final BigRational km; // k imaginary part


    /**
     * List of all 24 integral units.
     */
    public static List<BigQuaternion> entierUnits = null; //later: unitsOfHurwitzian();


    private final static Random random = new Random();


    private static final Logger logger = Logger.getLogger(BigQuaternion.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor for a BigQuaternion from BigRationals.
     * @param r BigRational.
     * @param i BigRational.
     * @param j BigRational.
     * @param k BigRational.
     */
    public BigQuaternion(BigRational r, BigRational i, BigRational j, BigRational k) {
        re = r;
        im = i;
        jm = j;
        km = k;
    }


    /**
     * Constructor for a BigQuaternion from BigRationals.
     * @param r BigRational.
     * @param i BigRational.
     * @param j BigRational.
     */
    public BigQuaternion(BigRational r, BigRational i, BigRational j) {
        this(r, i, j, BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion from BigRationals.
     * @param r BigRational.
     * @param i BigRational.
     */
    public BigQuaternion(BigRational r, BigRational i) {
        this(r, i, BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion from BigRationals.
     * @param r BigRational.
     */
    public BigQuaternion(BigRational r) {
        this(r, BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion from BigComplex.
     * @param r BigComplex.
     */
    public BigQuaternion(BigComplex r) {
        this(r.re, r.im);
    }


    /**
     * Constructor for a BigQuaternion from long.
     * @param r long.
     */
    public BigQuaternion(long r) {
        this(new BigRational(r), BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion with no arguments.
     */
    public BigQuaternion() {
        this(BigRational.ZERO);
    }


    /**
     * The BigQuaternion string constructor accepts the following formats: empty
     * string, "rational", or "rat i rat j rat k rat" with no blanks around i, j
     * or k if used as polynoial coefficient.
     * @param s String.
     * @throws NumberFormatException
     */
    public BigQuaternion(String s) throws NumberFormatException {
        if (s == null || s.length() == 0) {
            re = BigRational.ZERO;
            im = BigRational.ZERO;
            jm = BigRational.ZERO;
            km = BigRational.ZERO;
            return;
        }
        s = s.trim();
        int r = s.indexOf("i") + s.indexOf("j") + s.indexOf("k");
        if (r == -3) {
            re = new BigRational(s);
            im = BigRational.ZERO;
            jm = BigRational.ZERO;
            km = BigRational.ZERO;
            return;
        }

        int i = s.indexOf("i");
        String sr = "";
        if (i > 0) {
            sr = s.substring(0, i);
        } else if (i < 0) {
            throw new NumberFormatException("BigQuaternion missing i");
        }
        String si = "";
        if (i < s.length()) {
            s = s.substring(i + 1, s.length());
        }
        int j = s.indexOf("j");
        if (j > 0) {
            si = s.substring(0, j);
        } else if (j < 0) {
            throw new NumberFormatException("BigQuaternion missing j");
        }
        String sj = "";
        if (j < s.length()) {
            s = s.substring(j + 1, s.length());
        }
        int k = s.indexOf("k");
        if (k > 0) {
            sj = s.substring(0, k);
        } else if (k < 0) {
            throw new NumberFormatException("BigQuaternion missing k");
        }
        String sk = "";
        if (k < s.length()) {
            s = s.substring(k + 1, s.length());
        }
        sk = s;

        re = new BigRational(sr.trim());
        im = new BigRational(si.trim());
        jm = new BigRational(sj.trim());
        km = new BigRational(sk.trim());
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public BigQuaternion factory() {
        return this;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<BigQuaternion> generators() {
        List<BigQuaternion> g = new ArrayList<BigQuaternion>(4);
        g.add(getONE());
        g.add(I);
        g.add(J);
        g.add(K);
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
    public BigQuaternion copy() {
        return new BigQuaternion(re, im, jm, km);
    }


    /**
     * Copy BigQuaternion element c.
     * @param c BigQuaternion.
     * @return a copy of c.
     */
    public BigQuaternion copy(BigQuaternion c) {
        return new BigQuaternion(c.re, c.im, c.jm, c.km);
    }


    /**
     * Get the zero element.
     * @return 0 as BigQuaternion.
     */
    public BigQuaternion getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return q as BigQuaternion.
     */
    public BigQuaternion getONE() {
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
     * Get a BigQuaternion element from a BigInteger.
     * @param a BigInteger.
     * @return a BigQuaternion.
     */
    public BigQuaternion fromInteger(java.math.BigInteger a) {
        return new BigQuaternion(new BigRational(a));
    }


    /**
     * Get a BigQuaternion element from a long.
     * @param a long.
     * @return a BigQuaternion.
     */
    public BigQuaternion fromInteger(long a) {
        return new BigQuaternion(new BigRational(a));
    }


    /**
     * Get a BigQuaternion element from a long vector.
     * @param a long vector.
     * @return a BigQuaternion.
     */
    public BigQuaternion fromInteger(long[] a) {
        return new BigQuaternion(new BigRational(a[0]), new BigRational(a[1]), new BigRational(a[2]),
                        new BigRational(a[3]));
    }


    /**
     * The constant 0.
     */
    public static final BigQuaternion ZERO = new BigQuaternion();


    /**
     * The constant 1.
     */
    public static final BigQuaternion ONE = new BigQuaternion(BigRational.ONE);


    /**
     * The constant i.
     */
    public static final BigQuaternion I = new BigQuaternion(BigRational.ZERO, BigRational.ONE);


    /**
     * The constant j.
     */
    public static final BigQuaternion J = new BigQuaternion(BigRational.ZERO, BigRational.ZERO,
                    BigRational.ONE);


    /**
     * The constant k.
     */
    public static final BigQuaternion K = new BigQuaternion(BigRational.ZERO, BigRational.ZERO,
                    BigRational.ZERO, BigRational.ONE);


    /**
     * Get the real part.
     * @return re.
     */
    public BigRational getRe() {
        return re;
    }


    /**
     * Get the imaginary part im.
     * @return im.
     */
    public BigRational getIm() {
        return im;
    }


    /**
     * Get the imaginary part jm.
     * @return jm.
     */
    public BigRational getJm() {
        return jm;
    }


    /**
     * Get the imaginary part km.
     * @return km.
     */
    public BigRational getKm() {
        return km;
    }


    /**
     * Get the string representation. Is compatible with the string constructor.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String s = "" + re;
        int i = im.compareTo(BigRational.ZERO);
        int j = jm.compareTo(BigRational.ZERO);
        int k = km.compareTo(BigRational.ZERO);
        if (debug) {
            logger.debug("compareTo " + im + " ? 0 = " + i);
            logger.debug("compareTo " + jm + " ? 0 = " + j);
            logger.debug("compareTo " + km + " ? 0 = " + k);
        }
        if (i == 0 && j == 0 && k == 0)
            return s;
        s += "i" + im;
        s += "j" + jm;
        s += "k" + km;
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
        StringBuffer s = new StringBuffer();
        boolean i = im.isZERO();
        boolean j = jm.isZERO();
        boolean k = km.isZERO();
        if (i && j && k) {
            if (re.isZERO()) {
                return "0 ";
            }
            if (!re.isONE()) {
                s.append(re.toScript() + "*");
            }
            s.append("oneQ ");
            return s.toString();
        }
        if (!re.isZERO()) {
            if (!re.isONE()) {
                s.append(re.toScript() + "*");
            }
            s.append("oneQ ");
        }
        if (!i) {
            if (s.length() > 0) {
                s.append("+ ");
            }
            if (!im.isONE()) {
                s.append(im.toScript() + "*");
            }
            s.append("IQ ");
        }
        if (!j) {
            if (s.length() > 0) {
                s.append("+ ");
            }
            if (!jm.isONE()) {
                s.append(jm.toScript() + "*");
            }
            s.append("JQ ");
        }
        if (!k) {
            if (s.length() > 0) {
                s.append("+ ");
            }
            if (!km.isONE()) {
                s.append(km.toScript() + "*");
            }
            s.append("KQ ");
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
        return "Quat()";
    }


    /**
     * Is Quaternion number zero.
     * @param A BigQuaternion.
     * @return true if A is 0, else false.
     */
    public static boolean isQZERO(BigQuaternion A) {
        if (A == null)
            return false;
        return A.isZERO();
    }


    /**
     * Is BigQuaternion number zero.
     * @return true if this is 0, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return re.isZERO() && im.isZERO() && jm.isZERO() && km.isZERO();
    }


    /**
     * Is BigQuaternion number one.
     * @param A is a quaternion number.
     * @return true if A is 1, else false.
     */
    public static boolean isQONE(BigQuaternion A) {
        if (A == null)
            return false;
        return A.isONE();
    }


    /**
     * Is BigQuaternion number one.
     * @see edu.jas.structure.RingElem#isONE()
     * @return true if this is 1, else false.
     */
    public boolean isONE() {
        return re.isONE() && im.isZERO() && jm.isZERO() && km.isZERO();
    }


    /**
     * Is BigQuaternion imaginary one.
     * @return true if this is i, else false.
     */
    public boolean isIMAG() {
        return re.isZERO() && im.isONE() && jm.isZERO() && km.isZERO();
    }


    /**
     * Is BigQuaternion unit element.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        return !isZERO();
    }


    /**
     * Is BigQuaternion entier element.
     * @return If this is an integer Hurwitz element then true is returned, else
     *         false.
     */
    public boolean isEntier() {
        if (re.isEntier() && im.isEntier() && jm.isEntier() && km.isEntier()) {
            return true;
        }
        java.math.BigInteger TWO = BigInteger.TWO.val;
        return re.den.equals(TWO) && im.den.equals(TWO) && jm.den.equals(TWO) && km.den.equals(TWO);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof BigQuaternion))
            return false;
        BigQuaternion B = (BigQuaternion) b;
        return re.equals(B.re) && im.equals(B.im) && jm.equals(B.jm) && km.equals(B.km);
    }


    /**
     * Hash code for this BigQuaternion.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = re.hashCode();
        h += h * 37 + im.hashCode();
        h += h * 37 + jm.hashCode();
        h += h * 37 + km.hashCode();
        return h;
    }


    /**
     * Since quaternion numbers are unordered, we use lexicographical order of
     * re, im, jm and km.
     * @param b BigQuaternion.
     * @return 0 if b is equal to this, 1 if this is greater b and -1 else.
     */
    @Override
    public int compareTo(BigQuaternion b) {
        int s = re.compareTo(b.re);
        if (s != 0) {
            return s;
        }
        s = im.compareTo(b.im);
        if (s != 0) {
            return s;
        }
        s = jm.compareTo(b.jm);
        if (s != 0) {
            return s;
        }
        return km.compareTo(b.km);
    }


    /**
     * Since quaternion numbers are unordered, we use lexicographical order of
     * re, im, jm and km.
     * @return 0 if this is equal to 0; 1 if re > 0, or re == 0 and im > 0, or
     *         ...; -1 if re < 0, or re == 0 and im < 0, or ...
     * @see edu.jas.structure.RingElem#signum()
     */
    public int signum() {
        int s = re.signum();
        if (s != 0) {
            return s;
        }
        s = im.signum();
        if (s != 0) {
            return s;
        }
        s = jm.signum();
        if (s != 0) {
            return s;
        }
        return km.signum();
    }


    /* arithmetic operations: +, -, -
     */

    /**
     * BigQuaternion summation.
     * @param B BigQuaternion.
     * @return this+B.
     */
    public BigQuaternion sum(BigQuaternion B) {
        return new BigQuaternion(re.sum(B.re), im.sum(B.im), jm.sum(B.jm), km.sum(B.km));
    }


    /**
     * Quaternion number sum.
     * @param A BigQuaternion.
     * @param B BigQuaternion.
     * @return A+B.
     */
    public static BigQuaternion QSUM(BigQuaternion A, BigQuaternion B) {
        if (A == null)
            return null;
        return A.sum(B);
    }


    /**
     * Quaternion number difference.
     * @param A BigQuaternion.
     * @param B BigQuaternion.
     * @return A-B.
     */
    public static BigQuaternion QDIF(BigQuaternion A, BigQuaternion B) {
        if (A == null)
            return null;
        return A.subtract(B);
    }


    /**
     * BigQuaternion subtraction.
     * @param B BigQuaternion.
     * @return this-B.
     */
    public BigQuaternion subtract(BigQuaternion B) {
        return new BigQuaternion(re.subtract(B.re), im.subtract(B.im), jm.subtract(B.jm), km.subtract(B.km));
    }


    /**
     * Quaternion number negative.
     * @param A is a quaternion number
     * @return -A.
     */
    public static BigQuaternion QNEG(BigQuaternion A) {
        if (A == null)
            return null;
        return A.negate();
    }


    /**
     * BigQuaternion number negative.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public BigQuaternion negate() {
        return new BigQuaternion(re.negate(), im.negate(), jm.negate(), km.negate());
    }


    /**
     * Quaternion number conjugate.
     * @param A is a quaternion number.
     * @return the quaternion conjugate of A.
     */
    public static BigQuaternion QCON(BigQuaternion A) {
        if (A == null)
            return null;
        return A.conjugate();
    }


    /* arithmetic operations: conjugate, absolute value 
     */

    /**
     * BigQuaternion conjugate.
     * @return conjugate(this).
     */
    public BigQuaternion conjugate() {
        return new BigQuaternion(re, im.negate(), jm.negate(), km.negate());
    }


    /**
     * Quaternion number norm.
     * @see edu.jas.structure.StarRingElem#norm()
     * @return ||this||.
     */
    public BigQuaternion norm() {
        // this.conjugate().multiply(this);
        BigRational v = re.multiply(re);
        v = v.sum(im.multiply(im));
        v = v.sum(jm.multiply(jm));
        v = v.sum(km.multiply(km));
        return new BigQuaternion(v);
    }


    /**
     * Quaternion number absolute value.
     * @see edu.jas.structure.RingElem#abs()
     * @return |this|^2. Note: The square root is not jet implemented.
     */
    public BigQuaternion abs() {
        BigQuaternion n = norm();
        logger.error("abs() square root missing");
        // n = n.sqrt();
        return n;
    }


    /**
     * Quaternion number absolute value.
     * @param A is a quaternion number.
     * @return the absolute value of A, a rational number. Note: The square root
     *         is not jet implemented.
     */
    public static BigRational QABS(BigQuaternion A) {
        if (A == null)
            return null;
        return A.abs().re;
    }


    /**
     * Quaternion number product.
     * @param A BigQuaternion.
     * @param B BigQuaternion.
     * @return A*B.
     */
    public static BigQuaternion QPROD(BigQuaternion A, BigQuaternion B) {
        if (A == null)
            return null;
        return A.multiply(B);
    }


    /* arithmetic operations: *, inverse, / 
     */

    /**
     * BigQuaternion multiply with BigRational.
     * @param b BigRational.
     * @return this*b.
     */
    public BigQuaternion multiply(BigRational b) {
        BigRational r = re.multiply(b);
        BigRational i = im.multiply(b);
        BigRational j = jm.multiply(b);
        BigRational k = km.multiply(b);
        return new BigQuaternion(r, i, j, k);
    }


    /**
     * BigQuaternion multiply.
     * @param B BigQuaternion.
     * @return this*B.
     */
    public BigQuaternion multiply(BigQuaternion B) {
        BigRational r = re.multiply(B.re);
        r = r.subtract(im.multiply(B.im));
        r = r.subtract(jm.multiply(B.jm));
        r = r.subtract(km.multiply(B.km));
        BigRational i = re.multiply(B.im);
        i = i.sum(im.multiply(B.re));
        i = i.sum(jm.multiply(B.km));
        i = i.subtract(km.multiply(B.jm));

        BigRational j = re.multiply(B.jm);
        j = j.subtract(im.multiply(B.km));
        j = j.sum(jm.multiply(B.re));
        j = j.sum(km.multiply(B.im));

        BigRational k = re.multiply(B.km);
        k = k.sum(im.multiply(B.jm));
        k = k.subtract(jm.multiply(B.im));
        k = k.sum(km.multiply(B.re));

        return new BigQuaternion(r, i, j, k);
    }


    /**
     * Quaternion number inverse.
     * @param A is a non-zero quaternion number.
     * @return S with S * A = A * S = 1.
     */
    public static BigQuaternion QINV(BigQuaternion A) {
        if (A == null)
            return null;
        return A.inverse();
    }


    /**
     * BigQuaternion inverse.
     * @return S with S * this = this * S = 1.
     * @see edu.jas.structure.RingElem#inverse()
     */
    public BigQuaternion inverse() {
        BigRational a = norm().re.inverse();
        return new BigQuaternion(re.multiply(a), im.negate().multiply(a), jm.negate().multiply(a),
                        km.negate().multiply(a));
    }


    /**
     * BigQuaternion remainder.
     * @param S BigQuaternion.
     * @return 0.
     */
    public BigQuaternion remainder(BigQuaternion S) {
        if (S.isZERO()) {
            throw new ArithmeticException("division by zero");
        }
        return ZERO;
    }


    /**
     * Quaternion number quotient.
     * @param A BigQuaternion.
     * @param B BigQuaternion.
     * @return R/S.
     */
    public static BigQuaternion QQ(BigQuaternion A, BigQuaternion B) {
        if (A == null)
            return null;
        return A.divide(B);
    }


    /**
     * BigQuaternion right divide.
     * @param b BigQuaternion.
     * @return this * b**(-1).
     */
    public BigQuaternion divide(BigQuaternion b) {
        return rightDivide(b);
    }


    /**
     * BigQuaternion right divide.
     * @param b BigQuaternion.
     * @return this * b**(-1).
     */
    public BigQuaternion rightDivide(BigQuaternion b) {
        return this.multiply(b.inverse());
    }


    /**
     * BigQuaternion left divide.
     * @param b BigQuaternion.
     * @return b**(-1) * this.
     */
    public BigQuaternion leftDivide(BigQuaternion b) {
        return b.inverse().multiply(this);
    }


    /**
     * BigQuaternion divide.
     * @param b BigRational.
     * @return this/b.
     */
    public BigQuaternion divide(BigRational b) {
        BigRational bi = b.inverse();
        return new BigQuaternion(re.multiply(bi), im.multiply(bi), jm.multiply(bi), km.multiply(bi));
    }


    /**
     * Quotient and remainder by division of this by S.
     * @param S a quaternion number
     * @return [this*S**(-1), this - (this*S**(-1))*S].
     */
    public BigQuaternion[] quotientRemainder(BigQuaternion S) {
        return new BigQuaternion[] { divide(S), ZERO };
    }


    /**
     * BigQuaternion random. Random rational numbers A, B, C and D are generated
     * using random(n). Then R is the quaternion number with real part A and
     * imaginary parts B, C and D.
     * @param n such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @return R, a random BigQuaternion.
     */
    public BigQuaternion random(int n) {
        return random(n, random);
    }


    /**
     * BigQuaternion random. Random rational numbers A, B, C and D are generated
     * using RNRAND(n). Then R is the quaternion number with real part A and
     * imaginary parts B, C and D.
     * @param n such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return R, a random BigQuaternion.
     */
    public BigQuaternion random(int n, Random rnd) {
        BigRational r = BigRational.ONE.random(n, rnd);
        BigRational i = BigRational.ONE.random(n, rnd);
        BigRational j = BigRational.ONE.random(n, rnd);
        BigRational k = BigRational.ONE.random(n, rnd);
        return new BigQuaternion(r, i, j, k);
    }


    /**
     * Quaternion number, random. Random rational numbers A, B, C and D are
     * generated using RNRAND(n). Then R is the quaternion number with real part
     * A and imaginary parts B, C and D.
     * @param n such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @return R, a random BigQuaternion.
     */
    public static BigQuaternion QRAND(int n) {
        return ONE.random(n, random);
    }


    /**
     * Parse quaternion number from String.
     * @param s String.
     * @return BigQuaternion from s.
     */
    public BigQuaternion parse(String s) {
        return new BigQuaternion(s);
    }


    /**
     * Parse quaternion number from Reader.
     * @param r Reader.
     * @return next BigQuaternion from r.
     */
    public BigQuaternion parse(Reader r) {
        return parse(StringUtil.nextString(r));
    }


    /**
     * Quaternion number greatest common divisor.
     * @param S BigQuaternion.
     * @return gcd(this,S).
     */
    public BigQuaternion gcd(BigQuaternion S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        return ONE;
    }


    /**
     * BigQuaternion extended greatest common divisor.
     * @param S BigQuaternion.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    public BigQuaternion[] egcd(BigQuaternion S) {
        BigQuaternion[] ret = new BigQuaternion[3];
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
        BigQuaternion half = new BigQuaternion(new BigRational(1, 2));
        ret[0] = ONE;
        ret[1] = this.inverse().multiply(half);
        ret[2] = S.inverse().multiply(half);
        return ret;
    }


    /**
     * Returns the number of bits in the representation of this BigQuaternion,
     * including a sign bit. It is equivalent to
     * {@code re.bitLength()+im.bitLength()+jm.bitLength()+km.bitLength()}.)
     * @return number of bits in the representation of this BigQuaternion,
     *         including a sign bit.
     */
    public long bitLength() {
        return re.bitLength() + im.bitLength() + jm.bitLength() + km.bitLength();
    }


    /**
     * BigQuaternion ceiling, component wise.
     * @return ceiling of this.
     */
    public BigQuaternion ceil() {
        BigRational r = new BigRational(re.ceil());
        BigRational i = new BigRational(im.ceil());
        BigRational j = new BigRational(jm.ceil());
        BigRational k = new BigRational(km.ceil());
        return new BigQuaternion(r, i, j, k);
    }


    /**
     * BigQuaternion floor, component wise.
     * @return floor of this.
     */
    public BigQuaternion floor() {
        BigRational r = new BigRational(re.floor());
        BigRational i = new BigRational(im.floor());
        BigRational j = new BigRational(jm.floor());
        BigRational k = new BigRational(km.floor());
        return new BigQuaternion(r, i, j, k);
    }


    /**
     * BigQuaternion round to next Lipschitz integer. BigQuaternion with all
     * integer components.
     * @return Lipschitz integer of this.
     */
    public BigQuaternion roundToLipschitzian() {
        BigRational half = BigRational.HALF;
        BigRational r = new BigRational(re.sum(half).floor());
        BigRational i = new BigRational(im.sum(half).floor());
        BigRational j = new BigRational(jm.sum(half).floor());
        BigRational k = new BigRational(km.sum(half).floor());
        return new BigQuaternion(r, i, j, k);
    }


    /**
     * BigQuaternion round to next Hurwitz integer. BigQuaternion with all
     * integer or all 1/2 times integer components.
     * @return Hurwitz integer of this.
     */
    public BigQuaternion roundToHurwitzian() {
        BigQuaternion g = this.roundToLipschitzian();
        BigQuaternion d = BigQuaternion.ZERO;
        BigRational half = BigRational.HALF;
        BigQuaternion s = this.subtract(g).norm();
        //System.out.println("s = " + s.toScript());
        if (s.re.compareTo(half) <= 0) {
            //System.out.println("s <= 1/2");
            return g;
        }
        List<BigQuaternion> units = unitsOfHurwitzian();
        for (BigQuaternion ue : units) {
            BigQuaternion t = this.subtract(g).sum(ue).norm();
            if (t.re.compareTo(s.re) < 0) {
                s = t;
                d = ue;
            }
        }
        //System.out.println("s = " + s.toScript());
        g = g.sum(d);
        return g;
    }


    /**
     * BigQuaternion units of the Hurwitzian integers. BigQuaternion units with
     * all integer or all 1/2 times integer components.
     * @return list of all 24 units.
     */
    public static List<BigQuaternion> unitsOfHurwitzian() {
        if (entierUnits != null) {
            return entierUnits;
        }
        BigRational half = BigRational.HALF;
        // Lipschitz integer units
        List<BigQuaternion> units = BigQuaternion.ONE.generators();
        List<BigQuaternion> u = new ArrayList<BigQuaternion>(units);
        for (BigQuaternion ue : u) {
            units.add(ue.negate());
        }
        // Hurwitz integer units
        long[][] comb = new long[][] { { 1, 1, 1, 1 }, { -1, 1, 1, 1 }, { 1, -1, 1, 1 }, { -1, -1, 1, 1 },
                { 1, 1, -1, 1 }, { -1, 1, -1, 1 }, { 1, -1, -1, 1 }, { -1, -1, -1, 1 }, { 1, 1, 1, -1 },
                { -1, 1, 1, -1 }, { 1, -1, 1, -1 }, { -1, -1, 1, -1 }, { 1, 1, -1, -1 }, { -1, 1, -1, -1 },
                { 1, -1, -1, -1 }, { -1, -1, -1, -1 } };
        for (long[] row : comb) {
            BigQuaternion ue = BigQuaternion.ONE.fromInteger(row);
            ue = ue.multiply(half);
            units.add(ue);
        }
        //System.out.println("units = " + units);
        //for (BigQuaternion ue : units) {
        //System.out.println("unit = " + ue + ", norm = " + ue.norm());
        //}
        entierUnits = units;
        return units;
    }


    /**
     * Integral quotient and remainder by left division of this by S. This must
     * be also an integral (Hurwitz) quaternion number.
     * @param b an integral (Hurwitz) quaternion number
     * @return [round(b**(-1)) this, this - b * (round(b**(-1)) this)].
     */
    public BigQuaternion[] leftQuotientAndRemainder(BigQuaternion b) {
        if (!this.isEntier() || !b.isEntier()) {
            throw new IllegalArgumentException("entier elements required");
        }
        BigQuaternion bi = b.inverse();
        BigQuaternion m = bi.multiply(this); // left divide
        //System.out.println("m = " + m.toScript());
        BigQuaternion mh = m.roundToHurwitzian();
        //System.out.println("mh = " + mh.toScript());
        BigQuaternion n = this.subtract(b.multiply(mh));
        BigQuaternion[] ret = new BigQuaternion[2];
        ret[0] = mh;
        ret[1] = n;
        return ret;
    }


    /**
     * Integral quotient and remainder by right division of this by S. This must
     * be also an integral (Hurwitz) quaternion number.
     * @param b an integral (Hurwitz) quaternion number
     * @return [round(b**(-1)) this, this - b * (round(b**(-1)) this)].
     */
    public BigQuaternion[] rightQuotientAndRemainder(BigQuaternion b) {
        if (!this.isEntier() || !b.isEntier()) {
            throw new IllegalArgumentException("entier elements required");
        }
        BigQuaternion bi = b.inverse();
        BigQuaternion m = this.multiply(bi); // right divide
        //System.out.println("m = " + m.toScript());
        BigQuaternion mh = m.roundToHurwitzian();
        //System.out.println("mh = " + mh.toScript());
        BigQuaternion n = this.subtract(mh.multiply(b));
        BigQuaternion[] ret = new BigQuaternion[2];
        ret[0] = mh;
        ret[1] = n;
        return ret;
    }


    /**
     * Integer quaternion number left greatest common divisor.
     * @param S integer BigQuaternion.
     * @return leftGcd(this,S).
     */
    public BigQuaternion leftGcd(BigQuaternion S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        BigQuaternion q, r;
        q = this;
        r = S;
        while (!r.isZERO()) {
            BigQuaternion u = q.leftQuotientAndRemainder(r)[1];
            //System.out.println("u = " + u.toScript());
            q = r;
            r = u;
        }
        return q;
    }


    /**
     * Integer quaternion number right greatest common divisor.
     * @param S integer BigQuaternion.
     * @return rightGcd(this,S).
     */
    public BigQuaternion rightGcd(BigQuaternion S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        BigQuaternion q, r;
        q = this;
        r = S;
        while (!r.isZERO()) {
            BigQuaternion u = q.rightQuotientAndRemainder(r)[1];
            //System.out.println("u = " + u.toScript());
            q = r;
            r = u;
        }
        return q;
    }

}
