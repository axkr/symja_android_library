/*
 * $Id$
 */

package edu.jas.arith;


import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.StarRingElem;


/**
 * BigQuaternion class based on BigRational implementing the RingElem interface
 * and with the familiar MAS static method names. Objects of this class are
 * immutable. The integer quaternion methods are implemented after
 * https://de.wikipedia.org/wiki/Hurwitzquaternion see also
 * https://en.wikipedia.org/wiki/Hurwitz_quaternion
 * @author Heinz Kredel
 */

public /*final*/ class BigQuaternion implements StarRingElem<BigQuaternion>, GcdRingElem<BigQuaternion> {


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
     * Corresponding BigQuaternion ring.
     */
    public final BigQuaternionRing ring;


    protected final static Random random = new Random();


    private static final Logger logger = LogManager.getLogger(BigQuaternion.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor for a BigQuaternion from BigRationals.
     * @param fac BigQuaternionRing.
     * @param r BigRational.
     * @param i BigRational.
     * @param j BigRational.
     * @param k BigRational.
     */
    public BigQuaternion(BigQuaternionRing fac, BigRational r, BigRational i, BigRational j, BigRational k) {
        ring = fac;
        re = r;
        im = i;
        jm = j;
        km = k;
    }


    /**
     * Constructor for a BigQuaternion from BigRationals.
     * @param fac BigQuaternionRing.
     * @param r BigRational.
     * @param i BigRational.
     * @param j BigRational.
     */
    public BigQuaternion(BigQuaternionRing fac, BigRational r, BigRational i, BigRational j) {
        this(fac, r, i, j, BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion from BigRationals.
     * @param fac BigQuaternionRing.
     * @param r BigRational.
     * @param i BigRational.
     */
    public BigQuaternion(BigQuaternionRing fac, BigRational r, BigRational i) {
        this(fac, r, i, BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion from BigRationals.
     * @param fac BigQuaternionRing.
     * @param r BigRational.
     */
    public BigQuaternion(BigQuaternionRing fac, BigRational r) {
        this(fac, r, BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion from BigComplex.
     * @param fac BigQuaternionRing.
     * @param r BigComplex.
     */
    public BigQuaternion(BigQuaternionRing fac, BigComplex r) {
        this(fac, r.re, r.im);
    }


    /**
     * Constructor for a BigQuaternion from long.
     * @param fac BigQuaternionRing.
     * @param r long.
     */
    public BigQuaternion(BigQuaternionRing fac, long r) {
        this(fac, new BigRational(r), BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion with no arguments.
     * @param fac BigQuaternionRing.
     */
    public BigQuaternion(BigQuaternionRing fac) {
        this(fac, BigRational.ZERO);
    }


    /**
     * The BigQuaternion string constructor accepts the following formats: empty
     * string, "rational", or "rat i rat j rat k rat" with no blanks around i, j
     * or k if used as polynoial coefficient.
     * @param fac BigQuaternionRing.
     * @param s String.
     * @throws NumberFormatException
     */
    public BigQuaternion(BigQuaternionRing fac, String s) throws NumberFormatException {
        ring = fac;
        if (s == null || s.length() == 0) {
            re = BigRational.ZERO;
            im = BigRational.ZERO;
            jm = BigRational.ZERO;
            km = BigRational.ZERO;
            return;
        }
        //System.out.println("init: s = " + s);
        s = s.trim();
        int r = s.indexOf("i") + s.indexOf("j") + s.indexOf("k");
        if (r == -3) {
            re = new BigRational(s);
            im = BigRational.ZERO;
            jm = BigRational.ZERO;
            km = BigRational.ZERO;
            return;
        }

        s = s.replaceAll("~", "-"); // when used with GenPolynomialTokenizer
        int i = s.indexOf("i");
        String sr = "";
        if (i > 0) {
            sr = s.substring(0, i);
        } else if (i < 0) {
            throw new NumberFormatException("BigQuaternion missing i: " + s);
        }
        String si = "";
        if (i < s.length()) {
            s = s.substring(i + 1, s.length());
        }
        int j = s.indexOf("j");
        if (j > 0) {
            si = s.substring(0, j);
        } else if (j < 0) {
            throw new NumberFormatException("BigQuaternion missing j: " + s);
        }
        String sj = "";
        if (j < s.length()) {
            s = s.substring(j + 1, s.length());
        }
        int k = s.indexOf("k");
        if (k > 0) {
            sj = s.substring(0, k);
        } else if (k < 0) {
            throw new NumberFormatException("BigQuaternion missing k: " + s);
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
    public BigQuaternionRing factory() {
        return ring;
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public BigQuaternion copy() {
        return new BigQuaternion(ring, re, im, jm, km);
    }


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
        StringBuffer sb = new StringBuffer(re.toString());
        int i = im.compareTo(BigRational.ZERO);
        int j = jm.compareTo(BigRational.ZERO);
        int k = km.compareTo(BigRational.ZERO);
        if (debug) {
            logger.debug("compareTo " + im + " ? 0 = " + i);
            logger.debug("compareTo " + jm + " ? 0 = " + j);
            logger.debug("compareTo " + km + " ? 0 = " + k);
        }
        if (i == 0 && j == 0 && k == 0) {
            return sb.toString();
        }
        sb.append("i" + im);
        sb.append("j" + jm);
        sb.append("k" + km);
        String s = sb.toString();
        //s = s.replaceAll("-","~"); 
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
        return ring.toScript();
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
        //if (ring.integral) { not meaningful to test
        //    System.out.println("*** entier isUnit case not implemented ***");
        //}
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
        if (!(b instanceof BigQuaternion)) {
            return false;
        }
        BigQuaternion B = (BigQuaternion) b;
        // ring == B.ring ?
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
     * @return 0 if this is equal to 0; 1 if re &gt; 0, or re == 0 and im &gt;
     *         0, or ...; -1 if re &lt; 0, or re == 0 and im &lt; 0, or ...
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
        return new BigQuaternion(ring, re.sum(B.re), im.sum(B.im), jm.sum(B.jm), km.sum(B.km));
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
        return new BigQuaternion(ring, re.subtract(B.re), im.subtract(B.im), jm.subtract(B.jm),
                        km.subtract(B.km));
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
        return new BigQuaternion(ring, re.negate(), im.negate(), jm.negate(), km.negate());
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
        return new BigQuaternion(ring, re, im.negate(), jm.negate(), km.negate());
    }


    /**
     * Quaternion number norm.
     * @see edu.jas.structure.StarRingElem#norm()
     * @return ||this||.
     */
    public BigQuaternion norm() {
        // this.multiply(this.conjugate());
        BigRational v = re.multiply(re);
        v = v.sum(im.multiply(im));
        v = v.sum(jm.multiply(jm));
        v = v.sum(km.multiply(km));
        return new BigQuaternion(ring, v);
    }


    /**
     * Quaternion number absolute value.
     * @see edu.jas.structure.RingElem#abs()
     * @return |this|.
     */
    public BigQuaternion abs() {
        BigQuaternion n = norm();
        BigRational r = Roots.sqrt(n.re);
        //logger.error("abs() square root missing");
        return new BigQuaternion(ring, r);
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
        return new BigQuaternion(ring, r, i, j, k);
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

        return new BigQuaternion(ring, r, i, j, k);
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
        return new BigQuaternion(ring, re.multiply(a), im.negate().multiply(a), jm.negate().multiply(a),
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
        if (ring.integral) {
            //System.out.println(
            //       "*** entier right remainder(" + this + ", " + S + "): " + ring + " ***");
            BigQuaternionInteger c = new BigQuaternionInteger(ring, this);
            BigQuaternionInteger d = new BigQuaternionInteger(ring, S);
            return c.rightRemainder(d);
        }
        return ring.getZERO();
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
    @Override
    public BigQuaternion rightDivide(BigQuaternion b) {
        if (ring.integral) {
            //System.out.println("*** entier right divide(" + this + ", " + b + "): " + ring + " ***");
            BigQuaternionInteger c = new BigQuaternionInteger(ring, this);
            BigQuaternionInteger d = new BigQuaternionInteger(ring, b);
            return c.rightDivide(d);
        }
        return this.multiply(b.inverse());
    }


    /**
     * BigQuaternion left divide.
     * @param b BigQuaternion.
     * @return b**(-1) * this.
     */
    @Override
    public BigQuaternion leftDivide(BigQuaternion b) {
        if (ring.integral) {
            //System.out.println("*** entier left divide(" + this + ", " + b + "): " + ring + " ***");
            BigQuaternionInteger c = new BigQuaternionInteger(ring, this);
            BigQuaternionInteger d = new BigQuaternionInteger(ring, b);
            return c.leftDivide(d);
        }
        return b.inverse().multiply(this);
    }


    /**
     * BigQuaternion divide.
     * @param b BigRational.
     * @return this/b.
     */
    public BigQuaternion divide(BigRational b) {
        BigRational bi = b.inverse();
        return new BigQuaternion(ring, re.multiply(bi), im.multiply(bi), jm.multiply(bi), km.multiply(bi));
    }


    /**
     * Quotient and remainder by division of this by S.
     * @param S a quaternion number
     * @return [this*S**(-1), this - (this*S**(-1))*S].
     */
    public BigQuaternion[] quotientRemainder(BigQuaternion S) {
        if (ring.integral) {
            //System.out.println(
            //     "*** entier left quotient remainder(" + this + ", " + S + "): " + ring + " ***");
            BigQuaternionInteger c = new BigQuaternionInteger(ring, this);
            BigQuaternionInteger d = new BigQuaternionInteger(ring, S);
            return c.rightQuotientAndRemainder(d);
        }
        return new BigQuaternion[] { divide(S), ring.getZERO() };
    }


    /**
     * Quaternion number greatest common divisor.
     * @param S BigQuaternion.
     * @return gcd(this,S).
     */
    public BigQuaternion gcd(BigQuaternion S) {
        return leftGcd(S);
    }


    /**
     * Quaternion number greatest common divisor.
     * @param S BigQuaternion.
     * @return leftCcd(this,S).
     */
    public BigQuaternion leftGcd(BigQuaternion S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        if (ring.integral) {
            //System.out.println("*** entier left gcd(" + this + ", " + S + "): " + ring + " ***");
            BigQuaternionInteger a = new BigQuaternionInteger(ring, this);
            BigQuaternionInteger b = new BigQuaternionInteger(ring, S);
            return a.leftGcd(b);
        }
        return ring.getONE();
    }


    /**
     * Quaternion number greatest common divisor.
     * @param S BigQuaternion.
     * @return rightCcd(this,S).
     */
    public BigQuaternion rightGcd(BigQuaternion S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        if (ring.integral) {
            //System.out.println("*** entier right gcd(" + this + ", " + S + "): " + ring + " ***");
            BigQuaternionInteger a = new BigQuaternionInteger(ring, this);
            BigQuaternionInteger b = new BigQuaternionInteger(ring, S);
            return a.rightGcd(b);
        }
        return ring.getONE();
    }


    /**
     * BigQuaternion extended greatest common divisor.
     * @param S BigQuaternion.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    public BigQuaternion[] egcd(BigQuaternion S) {
        if (ring.integral) {
            System.out.println("*** entier egcd case not implemented ***");
        }
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
        BigQuaternion half = new BigQuaternion(ring, new BigRational(1, 2));
        ret[0] = ring.getONE();
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
        return new BigQuaternion(ring, r, i, j, k);
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
        return new BigQuaternion(ring, r, i, j, k);
    }


    /**
     * BigQuaternion round to next Lipschitz integer. BigQuaternion with all
     * integer components.
     * @return Lipschitz integer of this.
     */
    public BigQuaternionInteger roundToLipschitzian() {
        BigRational half = BigRational.HALF;
        BigRational r = new BigRational(re.sum(half).floor());
        BigRational i = new BigRational(im.sum(half).floor());
        BigRational j = new BigRational(jm.sum(half).floor());
        BigRational k = new BigRational(km.sum(half).floor());
        return new BigQuaternionInteger(ring, r, i, j, k);
    }


    /**
     * BigQuaternion round to next Hurwitz integer. BigQuaternion with all
     * integer or all 1/2 times integer components.
     * @return Hurwitz integer near this.
     */
    public BigQuaternionInteger roundToHurwitzian() {
        if (isEntier()) {
            //System.out.println("*** short cut to round ***");
            return new BigQuaternionInteger(ring, this);
        }
        BigQuaternionInteger g = this.roundToLipschitzian();
        BigQuaternion d = ring.getZERO();
        //BigRational half = BigRational.HALF;
        BigQuaternion s = this.subtract(g).norm();
        //System.out.println("s = " + s.toScript());
        //if (s.re.compareTo(half) < 0) { // wrong
        List<BigQuaternion> units = ring.unitsOfHurwitzian();
        BigQuaternion t = null;
        for (BigQuaternion ue : units) {
            //t = this.subtract(g).sum(ue).norm(); // bug
            t = this.subtract(g.sum(ue)).norm();
            if (t.re.compareTo(s.re) < 0) {
                s = t;
                d = ue;
            }
        }
        //System.out.println("ring = " + ring);
        g = new BigQuaternionInteger(ring, g.sum(d));
        return g;
    }

}
