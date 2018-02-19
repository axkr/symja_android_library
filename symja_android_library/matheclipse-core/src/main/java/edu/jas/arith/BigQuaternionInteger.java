/*
 * $Id$
 */

package edu.jas.arith;


import org.apache.log4j.Logger;


/**
 * Integer BigQuaternion class based on BigRational implementing the RingElem
 * interface and with the familiar MAS static method names. Objects of this
 * class are immutable. The integer quaternion methods are implemented after
 * https://de.wikipedia.org/wiki/Hurwitzquaternion see also
 * https://en.wikipedia.org/wiki/Hurwitz_quaternion
 *
 * @author Heinz Kredel
 */

public final class BigQuaternionInteger extends BigQuaternion
// implements StarRingElem<BigQuaternion>, GcdRingElem<BigQuaternion> 
{


    private static final Logger logger = Logger.getLogger(BigQuaternionInteger.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor for a BigQuaternion from BigRationals.
     *
     * @param fac BigQuaternionRing.
     * @param r   BigRational.
     * @param i   BigRational.
     * @param j   BigRational.
     * @param k   BigRational.
     */
    public BigQuaternionInteger(BigQuaternionRing fac, BigRational r, BigRational i, BigRational j,
                                BigRational k) {
        super(fac, r, i, j, k);
    }


    /**
     * Constructor for a BigQuaternion from BigRationals.
     *
     * @param fac BigQuaternionRing.
     * @param r   BigRational.
     * @param i   BigRational.
     * @param j   BigRational.
     */
    public BigQuaternionInteger(BigQuaternionRing fac, BigRational r, BigRational i, BigRational j) {
        this(fac, r, i, j, BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion from BigRationals.
     *
     * @param fac BigQuaternionRing.
     * @param r   BigRational.
     * @param i   BigRational.
     */
    public BigQuaternionInteger(BigQuaternionRing fac, BigRational r, BigRational i) {
        this(fac, r, i, BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion from BigRationals.
     *
     * @param fac BigQuaternionRing.
     * @param r   BigRational.
     */
    public BigQuaternionInteger(BigQuaternionRing fac, BigRational r) {
        this(fac, r, BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion from BigComplex.
     *
     * @param fac BigQuaternionRing.
     * @param r   BigComplex.
     */
    public BigQuaternionInteger(BigQuaternionRing fac, BigComplex r) {
        this(fac, r.re, r.im);
    }


    /**
     * Constructor for a BigQuaternionInteger from BigQuaternion.
     *
     * @param fac BigQuaternionRing.
     * @param q   BigQuaternion.
     */
    public BigQuaternionInteger(BigQuaternionRing fac, BigQuaternion q) {
        this(fac, q.re, q.im, q.jm, q.km);
    }


    /**
     * Constructor for a BigQuaternion from long.
     *
     * @param fac BigQuaternionRing.
     * @param r   long.
     */
    public BigQuaternionInteger(BigQuaternionRing fac, long r) {
        this(fac, new BigRational(r), BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion with no arguments.
     *
     * @param fac BigQuaternionRing.
     */
    public BigQuaternionInteger(BigQuaternionRing fac) {
        this(fac, BigRational.ZERO);
    }


    /**
     * The BigQuaternion string constructor accepts the following formats: empty
     * string, "rational", or "rat i rat j rat k rat" with no blanks around i, j
     * or k if used as polynoial coefficient.
     *
     * @param fac BigQuaternionRing.
     * @param s   String.
     * @throws NumberFormatException
     */
    public BigQuaternionInteger(BigQuaternionRing fac, String s) throws NumberFormatException {
        super(fac, s);
    }

    /**
     * Quaternion number inverse.
     *
     * @param A is a non-zero quaternion number.
     * @return S with S * A = A * S = 1.
     */
    public static BigQuaternion QINV(BigQuaternion A) {
        if (A == null)
            return null;
        return A.inverse();
    }

    /**
     * Quaternion number quotient.
     *
     * @param A BigQuaternion.
     * @param B BigQuaternion.
     * @return R * B**(-1).
     */
    public static BigQuaternion QQ(BigQuaternion A, BigQuaternion B) {
        if (A == null)
            return null;
        return A.divide(B);
    }


    /* arithmetic operations: +, -, -
     */


    /* arithmetic operations: *, inverse, / 
     */

    /**
     * Get the corresponding element factory.
     *
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    @Override
    public BigQuaternionRing factory() {
        return ring;
    }

    /**
     * Clone this.
     *
     * @see Object#clone()
     */
    @Override
    public BigQuaternionInteger copy() {
        return new BigQuaternionInteger(ring, re, im, jm, km);
    }

    /**
     * BigQuaternion inverse.
     *
     * @return S with S * this = this * S = 1.
     * @see edu.jas.structure.RingElem#inverse()
     */
    @Override
    public BigQuaternion inverse() {
        if (!isUnit()) {
            logger.info("ring = " + ring);
            throw new ArithmeticException("not invertible: " + this);
        }
        return super.inverse();
    }

    /**
     * BigQuaternion remainder.
     *
     * @param S BigQuaternion.
     * @return this - this * b**(-1).
     */
    @Override
    public BigQuaternion remainder(BigQuaternion S) {
        return rightRemainder(S);
    }

    /**
     * BigQuaternion right divide.
     *
     * @param b BigQuaternion.
     * @return this * b**(-1).
     */
    @Override
    public BigQuaternion divide(BigQuaternion b) {
        return rightDivide(b);
    }


    /**
     * BigQuaternion right divide.
     *
     * @param b BigQuaternion.
     * @return this * b**(-1).
     */
    @Override
    public BigQuaternion rightDivide(BigQuaternion b) {
        return rightQuotientAndRemainder(b)[0];
    }


    /**
     * BigQuaternion left divide.
     *
     * @param b BigQuaternion.
     * @return b**(-1) * this.
     */
    @Override
    public BigQuaternion leftDivide(BigQuaternion b) {
        return leftQuotientAndRemainder(b)[0];
    }


    /**
     * BigQuaternion divide.
     *
     * @param b BigRational.
     * @return this/b.
     */
    @Override
    public BigQuaternion divide(BigRational b) {
        BigQuaternion d = super.divide(b);
        if (!d.isEntier()) {
            throw new ArithmeticException("not divisible: " + this + " / " + b);
        }
        return d;
    }


    /**
     * Quotient and remainder by division of this by S.
     *
     * @param S a quaternion number
     * @return [this*S**(-1), this - (this*S**(-1))*S].
     */
    @Override
    public BigQuaternion[] quotientRemainder(BigQuaternion S) {
        return new BigQuaternion[]{divide(S), remainder(S)};
    }


    /**
     * Quaternion number greatest common divisor.
     *
     * @param S BigQuaternion.
     * @return gcd(this, S).
     */
    @Override
    public BigQuaternion gcd(BigQuaternion S) {
        return rightGcd(S);
    }


    /**
     * BigQuaternion extended greatest common divisor.
     *
     * @param S BigQuaternion.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    @Override
    public BigQuaternion[] egcd(BigQuaternion S) {
        throw new UnsupportedOperationException("not implemented: egcd");
        /*
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
        */
    }


    /**
     * Integral quotient and remainder by left division of this by S. This must
     * be also an integral (Hurwitz) quaternion number.
     *
     * @param b an integral (Hurwitz) quaternion number
     * @return [round(b**(-1)) this, this - b * (round(b**(-1)) this)].
     */
    public BigQuaternion[] leftQuotientAndRemainder(BigQuaternion b) {
        //System.out.println("left QR = " + this + ", " + b);
        if (!this.isEntier() || !b.isEntier()) {
            throw new IllegalArgumentException("entier elements required");
        }
        BigQuaternion bi = b.inverse();
        BigQuaternion m = bi.multiply(this); // left divide
        //System.out.println("m = " + m.toScript());
        BigQuaternionInteger mh = m.roundToHurwitzian();
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
     *
     * @param b an integral (Hurwitz) quaternion number
     * @return [this round(b**(-1)), this - this (round(b**(-1)) b)].
     */
    public BigQuaternion[] rightQuotientAndRemainder(BigQuaternion b) {
        //System.out.println("right QR = " + this + ", " + b);
        if (!this.isEntier() || !b.isEntier()) {
            throw new IllegalArgumentException("entier elements required");
        }
        BigQuaternion bi = b.inverse();
        BigQuaternion m = this.multiply(bi); // right divide
        //System.out.println("m = " + m.toScript());
        BigQuaternionInteger mh = m.roundToHurwitzian();
        //System.out.println("mh = " + mh.toScript());
        BigQuaternion n = this.subtract(mh.multiply(b));
        BigQuaternion[] ret = new BigQuaternion[2];
        ret[0] = mh;
        ret[1] = n;
        return ret;
    }


    /**
     * Left remainder.
     *
     * @param a element.
     * @return r = this - (a/left) * a, where left * a = this.
     */
    @Override
    public BigQuaternion leftRemainder(BigQuaternion a) {
        return leftQuotientAndRemainder(a)[1];
    }


    /**
     * Right remainder.
     *
     * @param a element.
     * @return r = this - a * (a/right), where a * right = this.
     */
    @Override
    public BigQuaternion rightRemainder(BigQuaternion a) {
        return rightQuotientAndRemainder(a)[1];
    }


    /**
     * Integer quaternion number left greatest common divisor.
     *
     * @param S integer BigQuaternion.
     * @return leftGcd(this, S).
     */
    @Override
    public BigQuaternion leftGcd(BigQuaternion S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        BigQuaternionInteger q;
        BigQuaternion r;
        q = this;
        r = S;
        while (!r.isZERO()) {
            BigQuaternion u = q.leftQuotientAndRemainder(r)[1];
            //System.out.println("u = " + u.toScript());
            q = new BigQuaternionInteger(ring, r);
            r = u;
        }
        return q;
    }


    /**
     * Integer quaternion number right greatest common divisor.
     *
     * @param S integer BigQuaternion.
     * @return rightGcd(this, S).
     */
    @Override
    public BigQuaternion rightGcd(BigQuaternion S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        BigQuaternionInteger q;
        BigQuaternion r;
        q = this;
        r = S;
        while (!r.isZERO()) {
            BigQuaternion u = q.rightQuotientAndRemainder(r)[1];
            //System.out.println("u = " + u.toScript());
            q = new BigQuaternionInteger(ring, r);
            r = u;
        }
        return q;
    }

}
