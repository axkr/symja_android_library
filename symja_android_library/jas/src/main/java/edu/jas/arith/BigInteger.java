/*
 * $Id$
 */

package edu.jas.arith;


import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.jas.kern.StringUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.NotInvertibleException;


/**
 * BigInteger class to make java.math.BigInteger available with RingElem
 * respectively the GcdRingElem interface. Objects of this class are immutable.
 * The SAC2 static methods are also provided.
 * @author Heinz Kredel
 * @see java.math.BigInteger
 */

public final class BigInteger implements GcdRingElem<BigInteger>, RingFactory<BigInteger>,
                                         Iterable<BigInteger>, Rational {


    /**
     * The data structure.
     */
    public final java.math.BigInteger val;


    private final static Random random = new Random();


    /**
     * The constant 0.
     */
    public final static BigInteger ZERO = new BigInteger(java.math.BigInteger.ZERO);


    /**
     * The constant 1.
     */
    public final static BigInteger ONE = new BigInteger(java.math.BigInteger.ONE);


    /**
     * Constructor for BigInteger from math.BigInteger.
     * @param a java.math.BigInteger.
     */
    public BigInteger(java.math.BigInteger a) {
        val = a;
    }


    /**
     * Constructor for BigInteger from long.
     * @param a long.
     */
    public BigInteger(long a) {
        val = new java.math.BigInteger(String.valueOf(a));
    }


    /**
     * Constructor for BigInteger from String.
     * @param s String.
     */
    public BigInteger(String s) {
        val = new java.math.BigInteger(s.trim());
    }


    /**
     * Constructor for BigInteger without parameters.
     */
    public BigInteger() {
        val = java.math.BigInteger.ZERO;
    }


    /**
     * Get the value.
     * @return val java.math.BigInteger.
     */
    public java.math.BigInteger getVal() {
        return val;
    }


    /**
     * Get the value as long.
     * @return val as long.
     */
    public long longValue() {
        return val.longValue();
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public BigInteger factory() {
        return this;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<BigInteger> generators() {
        List<BigInteger> g = new ArrayList<BigInteger>(1);
        g.add(getONE());
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
    public BigInteger copy() {
        return new BigInteger(val);
    }


    /**
     * Copy BigInteger element c.
     * @param c BigInteger.
     * @return a copy of c.
     */
    public BigInteger copy(BigInteger c) {
        return new BigInteger(c.val);
    }


    /**
     * Get the zero element.
     * @return 0.
     */
    public BigInteger getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1.
     */
    public BigInteger getONE() {
        return ONE;
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
     * @return false.
     */
    public boolean isField() {
        return false;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return java.math.BigInteger.ZERO;
    }


    /**
     * Get a BigInteger element from a math.BigInteger.
     * @param a math.BigInteger.
     * @return a as BigInteger.
     */
    public BigInteger fromInteger(java.math.BigInteger a) {
        return new BigInteger(a);
    }


    /**
     * Get a BigInteger element from a math.BigInteger.
     * @param a math.BigInteger.
     * @return a as BigInteger.
     */
    public static BigInteger valueOf(java.math.BigInteger a) {
        return new BigInteger(a);
    }


    /**
     * Get a BigInteger element from long.
     * @param a long.
     * @return a as BigInteger.
     */
    public BigInteger fromInteger(long a) {
        return new BigInteger(a);
    }


    /**
     * Get a BigInteger element from long.
     * @param a long.
     * @return a as BigInteger.
     */
    public static BigInteger valueOf(long a) {
        return new BigInteger(a);
    }


    /**
     * Is BigInteger number zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return val.equals(java.math.BigInteger.ZERO);
    }


    /**
     * Is BigInteger number one.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return val.equals(java.math.BigInteger.ONE);
    }


    /**
     * Is BigInteger number unit.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        return (this.isONE() || this.negate().isONE());
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return val.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return toString();
    }


    /**
     * Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    @Override
    public String toScriptFactory() {
        // Python case
        return "ZZ()";
    }


    /**
     * Compare to BigInteger b.
     * @param b BigInteger.
     * @return 0 if this == b, 1 if this > b, -1 if this < b.
     */
    @Override
    public int compareTo(BigInteger b) {
        return val.compareTo(b.val);
    }


    /**
     * Integer comparison.
     * @param A BigInteger.
     * @param B BigInteger.
     * @return 0 if A == B, 1 if A > B, -1 if A < B.
     */
    public static int ICOMP(BigInteger A, BigInteger B) {
        if (A == null)
            return -B.signum();
        return A.compareTo(B);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof BigInteger)) {
            return false;
        }
        BigInteger bi = (BigInteger) b;
        return val.equals(bi.val);
    }


    /**
     * Hash code for this BigInteger.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return val.hashCode();
    }


    /**
     * Absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public BigInteger abs() {
        return new BigInteger(val.abs());
    }


    /**
     * Absolute value.
     * @param A BigInteger.
     * @return abs(A).
     */
    public static BigInteger IABS(BigInteger A) {
        if (A == null) {
            throw new IllegalArgumentException("null A not allowed");
        }
        return A.abs();
    }


    /* Negative value of this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public BigInteger negate() {
        return new BigInteger(val.negate());
    }


    /**
     * Negative value.
     * @param A BigInteger.
     * @return -A.
     */
    public static BigInteger INEG(BigInteger A) {
        if (A == null) {
            throw new IllegalArgumentException("null A not allowed");
        }
        return A.negate();
    }


    /**
     * signum.
     * @see edu.jas.structure.RingElem#signum()
     */
    public int signum() {
        return val.signum();
    }


    /**
     * Integer signum.
     * @param A BigInteger.
     * @return signum(A).
     */
    public static int ISIGN(BigInteger A) {
        if (A == null)
            return 0;
        return A.signum();
    }


    /**
     * BigInteger subtract.
     * @param S BigInteger.
     * @return this-S.
     */
    public BigInteger subtract(BigInteger S) {
        return new BigInteger(val.subtract(S.val));
    }


    /**
     * BigInteger subtract.
     * @param A BigInteger.
     * @param B BigInteger.
     * @return A-B.
     */
    public static BigInteger IDIF(BigInteger A, BigInteger B) {
        if (A == null)
            return B.negate();
        return A.subtract(B);
    }


    /**
     * BigInteger divide.
     * @param S BigInteger.
     * @return this/S.
     */
    public BigInteger divide(BigInteger S) {
        return new BigInteger(val.divide(S.val));
    }


    /**
     * BigInteger divide.
     * @param A BigInteger.
     * @param B BigInteger.
     * @return A/B.
     */
    public static BigInteger IQ(BigInteger A, BigInteger B) {
        if (A == null) {
            throw new IllegalArgumentException("null A not allowed");
        }
        return A.divide(B);
    }


    /**
     * Integer inverse. R is a non-zero integer. S=1/R if defined else 
     * throws not invertible exception.
     * @see edu.jas.structure.RingElem#inverse()
     */
    public BigInteger inverse() {
        if (this.isONE() || this.negate().isONE()) {
            return this;
        }
        //return ZERO;
        throw new NotInvertibleException("element not invertible " + this + " :: BigInteger");
    }


    /**
     * BigInteger remainder.
     * @param S BigInteger.
     * @return this - (this/S)*S.
     */
    public BigInteger remainder(BigInteger S) {
        return new BigInteger(val.remainder(S.val));
    }


    /**
     * BigInteger remainder.
     * @param A BigInteger.
     * @param B BigInteger.
     * @return A - (A/B)*B.
     */
    public static BigInteger IREM(BigInteger A, BigInteger B) {
        if (A == null) {
            throw new IllegalArgumentException("null A not allowed");
        }
        return A.remainder(B);
    }


    /**
     * BigInteger compute quotient and remainder. Throws an exception, if S ==
     * 0.
     * @param S BigInteger.
     * @return BigInteger[] { q, r } with this = q S + r and 0 &le; r &lt; |S|.
     */
    //@Override
    public BigInteger[] quotientRemainder(BigInteger S) {
        BigInteger[] qr = new BigInteger[2];
        java.math.BigInteger[] C = val.divideAndRemainder(S.val);
        qr[0] = new BigInteger(C[0]);
        qr[1] = new BigInteger(C[1]);
        return qr;
    }


    /**
     * BigInteger compute quotient and remainder. Throws an exception, if S ==
     * 0.
     * @param S BigInteger.
     * @return BigInteger[] { q, r } with this = q S + r and 0 &le; r &lt; |S|.
     * @deprecated use quotientRemainder()
     */
    @Deprecated
    public BigInteger[] divideAndRemainder(BigInteger S) {
        return quotientRemainder(S);
    }


    /**
     * Integer quotient and remainder. A and B are integers, B ne 0. Q is the
     * quotient, integral part of A/B, and R is the remainder A-B*Q. Throws an
     * exception, if B == 0.
     * @param A BigInteger.
     * @param B BigInteger.
     * @return BigInteger[] { q, r } with A = q B + r and 0 &le; r &lt; |B|
     */
    public static BigInteger[] IQR(BigInteger A, BigInteger B) {
        if (A == null) {
            throw new IllegalArgumentException("null A not allowed");
        }
        return A.quotientRemainder(B);
    }


    /**
     * BigInteger greatest common divisor.
     * @param S BigInteger.
     * @return gcd(this,S).
     */
    public BigInteger gcd(BigInteger S) {
        return new BigInteger(val.gcd(S.val));
    }


    /**
     * BigInteger extended greatest common divisor.
     * @param S BigInteger.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    public BigInteger[] egcd(BigInteger S) {
        BigInteger[] ret = new BigInteger[3];
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
        //System.out.println("this = " + this + ", S = " + S);
        BigInteger[] qr;
        BigInteger q = this;
        BigInteger r = S;
        BigInteger c1 = ONE;
        BigInteger d1 = ZERO;
        BigInteger c2 = ZERO;
        BigInteger d2 = ONE;
        BigInteger x1;
        BigInteger x2;
        while (!r.isZERO()) {
            qr = q.quotientRemainder(r);
            q = qr[0];
            x1 = c1.subtract(q.multiply(d1));
            x2 = c2.subtract(q.multiply(d2));
            c1 = d1;
            c2 = d2;
            d1 = x1;
            d2 = x2;
            q = r;
            r = qr[1];
        }
        //System.out.println("q = " + q + "\n c1 = " + c1 + "\n c2 = " + c2);
        if (q.signum() < 0) {
            q = q.negate();
            c1 = c1.negate();
            c2 = c2.negate();
        }
        ret[0] = q;
        ret[1] = c1;
        ret[2] = c2;
        return ret;
    }


    /**
     * BigInteger greatest common divisor.
     * @param A BigInteger.
     * @param B BigInteger.
     * @return gcd(A,B).
     */
    public static BigInteger IGCD(BigInteger A, BigInteger B) {
        if (A == null) {
            throw new IllegalArgumentException("null A not allowed");
        }
        return A.gcd(B);
    }


    /**
     * BigInteger random.
     * @param n such that 0 &le; r &le; (2<sup>n</sup>-1).
     * @return r, a random BigInteger.
     */
    public BigInteger random(int n) {
        return random(n, random);
    }


    /**
     * BigInteger random.
     * @param n such that 0 &le; r &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return r, a random BigInteger.
     */
    public BigInteger random(int n, Random rnd) {
        java.math.BigInteger r = new java.math.BigInteger(n, rnd);
        if (rnd.nextBoolean()) {
            r = r.negate();
        }
        return new BigInteger(r);
    }


    /**
     * BigInteger random.
     * @param NL such that 0 &le; r &le; (2<sup>n</sup>-1).
     * @return r, a random BigInteger.
     */
    public static BigInteger IRAND(int NL) {
        return ONE.random(NL, random);
    }


    /**
     * BigInteger multiply.
     * @param S BigInteger.
     * @return this*S.
     */
    public BigInteger multiply(BigInteger S) {
        return new BigInteger(val.multiply(S.val));
    }


    /**
     * BigInteger shift left.
     * @param n bits to shift.
     * @return this &lt;&lt; n.
     */
    public BigInteger shiftLeft(int n) {
        return new BigInteger(val.shiftLeft(n));
    }
    

    /**
     * BigInteger multiply.
     * @param A BigInteger.
     * @param B BigInteger.
     * @return A*B.
     */
    public static BigInteger IPROD(BigInteger A, BigInteger B) {
        if (A == null) {
            throw new IllegalArgumentException("null A not allowed");
        }
        return A.multiply(B);
    }


    /**
     * BigInteger summation.
     * @param S BigInteger.
     * @return this+S.
     */
    public BigInteger sum(BigInteger S) {
        return new BigInteger(val.add(S.val));
    }


    /**
     * BigInteger addition.
     * @param A BigInteger.
     * @param B BigInteger.
     * @return A+B.
     */
    public static BigInteger ISUM(BigInteger A, BigInteger B) {
        if (A == null) {
            throw new IllegalArgumentException("null A not allowed");
        }
        return A.sum(B);
    }


    /**
     * BigInteger parse from String.
     * @param s String.
     * @return Biginteger from s.
     */
    public BigInteger parse(String s) {
        return new BigInteger(s);
    }


    /**
     * BigInteger parse from Reader.
     * @param r Reader.
     * @return next Biginteger from r.
     */
    public BigInteger parse(Reader r) {
        return parse(StringUtil.nextString(r));
    }


    /**
     * Return a BigRational approximation of this Element.
     * @return a BigRational approximation of this.
     */
    public BigRational getRational() {
        return new BigRational(val);
    }


    private boolean nonNegative = true;


    /**
     * Set the iteration algorithm to all elements.
     */
    public void setAllIterator() {
        nonNegative = false;
    }


    /**
     * Set the iteration algorithm to non-negative elements.
     */
    public void setNonNegativeIterator() {
        nonNegative = true;
    }


    /**
     * Get a BigInteger iterator.
     * @return a iterator over all integers.
     */
    public Iterator<BigInteger> iterator() {
        return new BigIntegerIterator(nonNegative);
    }

}


/**
 * Big integer iterator.
 * @author Heinz Kredel
 */
class BigIntegerIterator implements Iterator<BigInteger> {


    /**
     * data structure.
     */
    java.math.BigInteger curr;


    final boolean nonNegative;


    /**
     * BigInteger iterator constructor.
     */
    public BigIntegerIterator() {
        this(false);
    }


    /**
     * BigInteger iterator constructor.
     * @param nn true for an iterator over non-negative longs, false for all
     *            elements iterator.
     */
    public BigIntegerIterator(boolean nn) {
        curr = java.math.BigInteger.ZERO;
        nonNegative = nn;
    }


    /**
     * Test for availability of a next element.
     * @return true if the iteration has more elements, else false.
     */
    public boolean hasNext() {
        return true;
    }


    /**
     * Get next integer.
     * @return next integer.
     */
    public synchronized BigInteger next() {
        BigInteger i = new BigInteger(curr);
        if (nonNegative) {
            curr = curr.add(java.math.BigInteger.ONE);
        } else if (curr.signum() > 0 && !nonNegative) {
            curr = curr.negate();
        } else {
            curr = curr.negate().add(java.math.BigInteger.ONE);
        }
        return i;
    }


    /**
     * Remove an element if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannnot remove elements");
    }
}
