/*
 * $Id$
 */

package edu.jas.arith;


import java.io.Reader;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import edu.jas.kern.Scripting;
import edu.jas.kern.StringUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Immutable arbitrary-precision rational numbers. BigRational class based on
 * BigInteger and implementing the RingElem interface. BigInteger is from
 * java.math in the implementation. The SAC2 static methods are also provided.
 * @author Heinz Kredel
 */

public final class BigRational implements GcdRingElem<BigRational>, RingFactory<BigRational>, Rational,
                Iterable<BigRational> {


    /**
     * Numerator part of the data structure.
     */
    public final BigInteger num;


    /**
     * Denominator part of the data structure.
     */
    public final BigInteger den;


    /**
     * The Constant 0.
     */
    public final static BigRational ZERO = new BigRational(BigInteger.ZERO);


    /**
     * The Constant 1.
     */
    public final static BigRational ONE = new BigRational(BigInteger.ONE);


    /**
     * The Constant 1/2.
     */
    public final static BigRational HALF = new BigRational(1, 2);


    private final static Random random = new Random();


    /**
     * Constructor for a BigRational from math.BigIntegers.
     * @param n math.BigInteger.
     * @param d math.BigInteger.
     */
    protected BigRational(BigInteger n, BigInteger d) {
        // assert gcd(n,d) == 1
        num = n;
        den = d;
    }


    /**
     * Constructor for a BigRational from math.BigIntegers.
     * @param n math.BigInteger.
     */
    public BigRational(BigInteger n) {
        num = n;
        den = BigInteger.ONE; // be aware of static initialization order
    }


    /**
     * Constructor for a BigRational from jas.arith.BigIntegers.
     * @param n edu.jas.arith.BigInteger.
     */
    public BigRational(edu.jas.arith.BigInteger n) {
        this(n.getVal());
    }


    /**
     * Constructor for a BigRational from jas.arith.BigIntegers.
     * @param n edu.jas.arith.BigInteger.
     * @param d edu.jas.arith.BigInteger.
     */
    public BigRational(edu.jas.arith.BigInteger n, edu.jas.arith.BigInteger d) {
        BigInteger nu = n.getVal();
        BigInteger de = d.getVal();
        BigRational r = RNRED(nu, de);
        num = r.num;
        den = r.den;
    }


    /**
     * Constructor for a BigRational from longs.
     * @param n long.
     * @param d long.
     */
    public BigRational(long n, long d) {
        BigInteger nu = BigInteger.valueOf(n);
        BigInteger de = BigInteger.valueOf(d);
        BigRational r = RNRED(nu, de);
        num = r.num;
        den = r.den;
    }


    /**
     * Constructor for a BigRational from longs.
     * @param n long.
     */
    public BigRational(long n) {
        num = BigInteger.valueOf(n);
        den = BigInteger.ONE;
    }


    /**
     * Constructor for a BigRational with no arguments.
     */
    public BigRational() {
        num = BigInteger.ZERO;
        den = BigInteger.ONE;
    }


    /**
     * Constructor for a BigRational from String.
     * @param s String.
     * @throws NumberFormatException
     */
    public BigRational(String s) throws NumberFormatException {
        if (s == null) {
            num = BigInteger.ZERO;
            den = BigInteger.ONE;
            return;
        }
        if (s.length() == 0) {
            num = BigInteger.ZERO;
            den = BigInteger.ONE;
            return;
        }
        BigInteger n;
        BigInteger d;
        s = s.trim();
        int i = s.indexOf('/');
        if (i < 0) {
            i = s.indexOf('.');
            if (i < 0) {
                num = new BigInteger(s);
                den = BigInteger.ONE;
                return;
            }
            if (s.charAt(0) == '-') { // case -0.11111
                n = new BigInteger(s.substring(1, i));
            } else {
                n = new BigInteger(s.substring(0, i));
            }
            BigRational r = new BigRational(n);
            d = new BigInteger(s.substring(i + 1, s.length()));
            int j = s.length() - i - 1;
            //System.out.println("j = " + j);
            //System.out.println("n = " + n);
            //System.out.println("d = " + d);
            BigRational z = new BigRational(1, 10);
            z = z.power(j); //Power.<BigRational> positivePower(z, j);
            BigRational f = new BigRational(d);
            f = f.multiply(z);
            r = r.sum(f);
            if (s.charAt(0) == '-') {
                num = r.num.negate();
            } else {
                num = r.num;
            }
            den = r.den;
        } else {
            String sn = s.substring(0, i);
            String sd = s.substring(i + 1, s.length());
            BigRational r;
            if (s.indexOf(".") < 0) { // all integers
                n = new BigInteger(sn);
                d = new BigInteger(sd);
                r = RNRED(n, d);
            } else { // integers or decimal fractions
                BigRational rn = new BigRational(sn);
                BigRational rd = new BigRational(sd);
                r = rn.divide(rd);
            }
            num = r.num;
            den = r.den;
            return;
        }
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    @Override
    public BigRational factory() {
        return this;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    @Override
    public List<BigRational> generators() {
        List<BigRational> g = new ArrayList<BigRational>(1);
        g.add(getONE());
        return g;
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    @Override
    public boolean isFinite() {
        return false;
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public BigRational copy() {
        return new BigRational(num, den);
    }


    /**
     * Copy BigRational element c.
     * @param c BigRational.
     * @return a copy of c.
     */
    @Override
    public BigRational copy(BigRational c) {
        return new BigRational(c.num, c.den);
    }


    /**
     * Return a BigRational approximation of this Element.
     * @return a BigRational approximation of this.
     * @see edu.jas.arith.Rational#getRational()
     */
    @Override
    public BigRational getRational() {
        return this;
    }


    /**
     * Get the numerator.
     * @return num.
     */
    public BigInteger numerator() {
        return num;
    }


    /**
     * Get the denominator.
     * @return den.
     */
    public BigInteger denominator() {
        return den;
    }


    /**
     * Get the string representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (Scripting.getPrecision() >= 0) {
            return toString(Scripting.getPrecision());
        }
        StringBuffer s = new StringBuffer();
        s.append(num);
        if (!den.equals(BigInteger.ONE)) {
            s.append("/").append(den);
        }
        return s.toString();
    }


    /**
     * Get the decimal string representation with given precision.
     * @param n precision.
     * @return decimal approximation.
     */
    public String toString(int n) {
        if (n < 0) {
            return toString();
        }
        java.math.MathContext mc = new java.math.MathContext(n);
        BigDecimal d = new BigDecimal(this, mc);
        return d.toString();
    }


    /**
     * Get the decimal representation.
     * @return decimal.
     */
    public BigDecimal getDecimal() {
        BigDecimal d = new BigDecimal(this);
        return d;
    }


    /**
     * Get this as a <tt>double</tt>.
     * @return this as a <tt>double</tt>
     * @see java.lang.Number#doubleValue()
     */
    public double doubleValue() {
        BigDecimal d = new BigDecimal(this, MathContext.DECIMAL64);
        return d.doubleValue();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case: (num,den) or num 
        // Ruby case: num/den or num 
        StringBuffer s = new StringBuffer();
        if (den.equals(BigInteger.ONE)) {
            s.append(num.toString());
            return s.toString();
        }
        if (Scripting.getPrecision() >= 0) {
            return toString(Scripting.getPrecision());
        }
        switch (Scripting.getLang()) {
        case Python:
            s.append("(");
            s.append(num.toString());
            s.append(",");
            s.append(den.toString());
            s.append(")");
            break;
        case Ruby:
        default:
            s.append(num.toString());
            s.append("/");
            s.append(den.toString());
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
        // Python and Ruby case
        return "QQ()";
    }


    /**
     * Get the zero element.
     * @return 0 as BigRational.
     */
    @Override
    public BigRational getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as BigRational.
     */
    @Override
    public BigRational getONE() {
        return ONE;
    }


    /**
     * Query if this ring is commutative.
     * @return true.
     */
    @Override
    public boolean isCommutative() {
        return true;
    }


    /**
     * Query if this ring is associative.
     * @return true.
     */
    @Override
    public boolean isAssociative() {
        return true;
    }


    /**
     * Query if this ring is a field.
     * @return true.
     */
    @Override
    public boolean isField() {
        return true;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    @Override
    public java.math.BigInteger characteristic() {
        return BigInteger.ZERO;
    }


    /**
     * Get a BigRational element from a math.BigInteger.
     * @param a math.BigInteger.
     * @return BigRational from a.
     */
    @Override
    public BigRational fromInteger(BigInteger a) {
        return new BigRational(a);
    }


    /**
     * Get a BigRational element from a arith.BigInteger.
     * @param a arith.BigInteger.
     * @return BigRational from a.
     */
    public BigRational fromInteger(edu.jas.arith.BigInteger a) {
        return new BigRational(a);
    }


    /**
     * Get a BigRational element from a math.BigInteger.
     * @param a math.BigInteger.
     * @return BigRational from a.
     */
    public static BigRational valueOf(BigInteger a) {
        return new BigRational(a);
    }


    /**
     * Get a BigRational element from a long.
     * @param a long.
     * @return BigRational from a.
     */
    @Override
    public BigRational fromInteger(long a) {
        return new BigRational(a);
    }


    /**
     * Get a BigRational element from a long.
     * @param a long.
     * @return BigRational from a.
     */
    public static BigRational valueOf(long a) {
        return new BigRational(a);
    }


    /**
     * Is BigRational zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    @Override
    public boolean isZERO() {
        return num.signum() == 0; //equals(BigInteger.ZERO);
    }


    /**
     * Is BigRational one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    @Override
    public boolean isONE() {
        return num.equals(den);
    }


    /**
     * Is BigRational unit.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    @Override
    public boolean isUnit() {
        return !isZERO();
    }


    /**
     * Is BigRational entier.
     * @return If this is an integer then true is returned, else false.
     */
    public boolean isEntier() {
        return isZERO() || den.equals(BigInteger.ONE);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof BigRational)) {
            return false;
        }
        BigRational br = (BigRational) b;
        return num.equals(br.num) && den.equals(br.den);
    }


    /**
     * Hash code for this BigRational.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * num.hashCode() + den.hashCode();
    }


    /**
     * Rational number reduction to lowest terms.
     * @param n BigInteger.
     * @param d BigInteger.
     * @return a/b ~ n/d, gcd(a,b) = 1, b &gt; 0.
     */
    public static BigRational RNRED(BigInteger n, BigInteger d) {
        BigInteger num;
        BigInteger den;
        if (d.equals(BigInteger.ZERO)) {
            throw new RuntimeException("rational number denominator is zero");
        }
        if (n.equals(BigInteger.ZERO)) {
            num = n;
            den = BigInteger.ONE;
            return new BigRational(num, den);
        }
        if (n.equals(d)) {
            num = BigInteger.ONE;
            den = BigInteger.ONE;
            return new BigRational(num, den);
        }
        BigInteger c = n.gcd(d);
        if (c.equals(BigInteger.ONE)) {
            num = n;
            den = d;
        } else {
            num = n.divide(c);
            den = d.divide(c);
        }
        if (den.signum() < 0) {
            num = num.negate();
            den = den.negate();
        }
        return new BigRational(num, den);
    }


    /**
     * Rational number reduction to lowest terms.
     * @param n BigInteger.
     * @param d BigInteger.
     * @return a/b ~ n/d, gcd(a,b) = 1, b &gt; 0.
     */
    public static BigRational reduction(BigInteger n, BigInteger d) {
        return RNRED(n, d);
    }


    /**
     * Rational number absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    @Override
    public BigRational abs() {
        if (this.signum() >= 0) {
            return this;
        }
        return this.negate();
    }


    /**
     * Rational number absolute value.
     * @param R is a rational number.
     * @return the absolute value of R.
     */
    public static BigRational RNABS(BigRational R) {
        if (R == null)
            return null;
        return R.abs();
    }


    /**
     * Rational number comparison.
     * @param S BigRational.
     * @return SIGN(this-S).
     */
    @Override
    public int compareTo(BigRational S) {
        BigInteger J2Y;
        BigInteger J3Y;
        BigInteger R1;
        BigInteger R2;
        BigInteger S1;
        BigInteger S2;
        int J1Y;
        int SL;
        int TL;
        int RL;
        if (this.equals(ZERO)) {
            return -S.signum();
        }
        if (S.equals(ZERO)) {
            return this.signum();
        }
        R1 = num; //this.numerator(); 
        R2 = den; //this.denominator();
        S1 = S.num;
        S2 = S.den;
        RL = R1.signum();
        SL = S1.signum();
        J1Y = (RL - SL);
        TL = (J1Y / 2);
        if (TL != 0) {
            return TL;
        }
        J3Y = R1.multiply(S2);
        J2Y = R2.multiply(S1);
        TL = J3Y.compareTo(J2Y);
        return TL;
    }


    /**
     * Rational number comparison.
     * @param R BigRational.
     * @param S BigRational.
     * @return SIGN(R-S).
     */
    public static int RNCOMP(BigRational R, BigRational S) {
        if (R == null)
            return Integer.MAX_VALUE;
        return R.compareTo(S);
    }


    /**
     * Rational number denominator.
     * @param R BigRational.
     * @return R.denominator().
     */
    public static BigInteger RNDEN(BigRational R) {
        if (R == null)
            return null;
        return R.den;
    }


    /**
     * Rational number difference.
     * @param S BigRational.
     * @return this-S.
     */
    @Override
    public BigRational subtract(BigRational S) {
        return this.sum(S.negate());
    }


    /**
     * Rational number difference.
     * @param R BigRational.
     * @param S BigRational.
     * @return R-S.
     */
    public static BigRational RNDIF(BigRational R, BigRational S) {
        if (R == null)
            return S.negate();
        return R.subtract(S);
    }


    /**
     * Rational number decimal write. R is a rational number. n is a
     * non-negative integer. R is approximated by a decimal fraction D with n
     * decimal digits following the decimal point and D is written in the output
     * stream. The inaccuracy of the approximation is at most (1/2)*10**-n.
     * @param R
     * @param NL
     */
    // If ABS(D) is greater than ABS(R) then the last digit is
    // followed by a minus sign, if ABS(D) is less than ABS(R) then by a
    // plus sign. 
    public static void RNDWR(BigRational R, int NL) {
        //BigInteger num = R.num;
        //BigInteger den = R.den;
        java.math.MathContext mc = new java.math.MathContext(NL);
        BigDecimal d = new BigDecimal(R, mc);
        System.out.print(d.toString());
        return;
    }


    /**
     * Rational number from integer.
     * @param A BigInteger.
     * @return A/1.
     */
    public static BigRational RNINT(BigInteger A) {
        return new BigRational(A);
    }


    /**
     * Rational number inverse.
     * @return 1/this.
     * @see edu.jas.structure.RingElem#inverse()
     */
    @Override
    public BigRational inverse() {
        BigInteger R1 = num;
        BigInteger R2 = den;
        BigInteger S1;
        BigInteger S2;
        if (R1.signum() >= 0) {
            S1 = R2;
            S2 = R1;
        } else {
            S1 = R2.negate();
            S2 = R1.negate();
        }
        return new BigRational(S1, S2);
    }


    /**
     * Rational number inverse.
     * @param R BigRational.
     * @return 1/R.
     */
    public static BigRational RNINV(BigRational R) {
        if (R == null)
            return null;
        return R.inverse();
    }


    /**
     * Rational number negative.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    @Override
    public BigRational negate() {
        BigInteger n = num.negate();
        return new BigRational(n, den);
    }


    /**
     * Rational number negative.
     * @param R BigRational.
     * @return -R.
     */
    public static BigRational RNNEG(BigRational R) {
        if (R == null)
            return null;
        return R.negate();
    }


    /**
     * Rational number numerator.
     * @param R BigRational.
     * @return R.numerator().
     */
    public static BigInteger RNNUM(BigRational R) {
        if (R == null)
            return null;
        return R.num;
    }


    /**
     * Rational number product.
     * @param S BigRational.
     * @return this*S.
     */
    @Override
    public BigRational multiply(BigRational S) {
        BigInteger D1 = null;
        BigInteger D2 = null;
        BigInteger R1 = null;
        BigInteger R2 = null;
        BigInteger RB1 = null;
        BigInteger RB2 = null;
        BigInteger S1 = null;
        BigInteger S2 = null;
        BigInteger SB1 = null;
        BigInteger SB2 = null;
        BigRational T;
        BigInteger T1;
        BigInteger T2;
        if (this.equals(ZERO) || S.equals(ZERO)) {
            T = ZERO;
            return T;
        }
        R1 = num; //this.numerator(); 
        R2 = den; //this.denominator();
        S1 = S.num;
        S2 = S.den;
        if (R2.equals(BigInteger.ONE) && S2.equals(BigInteger.ONE)) {
            T1 = R1.multiply(S1);
            T = new BigRational(T1, BigInteger.ONE);
            return T;
        }
        if (R2.equals(BigInteger.ONE)) {
            if (R1.equals(S2)) {
                D1 = R1;
            } else {
                D1 = R1.gcd(S2);
            }
            RB1 = R1.divide(D1);
            SB2 = S2.divide(D1);
            T1 = RB1.multiply(S1);
            T = new BigRational(T1, SB2);
            return T;
        }
        if (S2.equals(BigInteger.ONE)) {
            if (S1.equals(R2)) {
                D2 = S1;
            } else {
                D2 = S1.gcd(R2);
            }
            SB1 = S1.divide(D2);
            RB2 = R2.divide(D2);
            T1 = SB1.multiply(R1);
            T = new BigRational(T1, RB2);
            return T;
        }
        if (R1.equals(S2)) {
            D1 = R1;
        } else {
            D1 = R1.gcd(S2);
        }
        RB1 = R1.divide(D1);
        SB2 = S2.divide(D1);
        if (S1.equals(R2)) {
            D2 = S1;
        } else {
            D2 = S1.gcd(R2);
        }
        SB1 = S1.divide(D2);
        RB2 = R2.divide(D2);
        T1 = RB1.multiply(SB1);
        T2 = RB2.multiply(SB2);
        T = new BigRational(T1, T2);
        return T;
    }


    /**
     * Rational number product.
     * @param R BigRational.
     * @param S BigRational.
     * @return R*S.
     */
    public static BigRational RNPROD(BigRational R, BigRational S) {
        if (R == null) {
            return R;
        }
        return R.multiply(S);
    }


    /**
     * Rational number quotient.
     * @param S BigRational.
     * @return this/S.
     */
    @Override
    public BigRational divide(BigRational S) {
        return multiply(S.inverse());
    }


    /**
     * Rational number quotient.
     * @param R BigRational.
     * @param S BigRational.
     * @return R/S.
     */
    public static BigRational RNQ(BigRational R, BigRational S) {
        if (R == null) {
            return R;
        }
        return R.divide(S);
    }


    /**
     * Rational number remainder.
     * @param S BigRational.
     * @return this-(this/S)*S
     */
    @Override
    public BigRational remainder(BigRational S) {
        if (S.isZERO()) {
            throw new ArithmeticException("division by zero");
        }
        return ZERO;
    }


    /**
     * Quotient and remainder by division of this by S.
     * @param S a rational number
     * @return [this/S, this - (this/S)*S].
     */
    @Override
    public BigRational[] quotientRemainder(BigRational S) {
        return new BigRational[] { divide(S), ZERO };
    }


    /**
     * Rational number, random. Random integers A, B and a random sign s are
     * generated using BigInteger(n,random) and random.nextBoolen(). Then R =
     * s*A/(B+1), reduced to lowest terms.
     * @param n such that 0 &le; A, B &le; (2<sup>n</sup>-1).
     * @return a random BigRational.
     */
    @Override
    public BigRational random(int n) {
        return random(n, random);
    }


    /**
     * Rational number, random. Random integers A, B and a random sign s are
     * generated using BigInteger(n,random) and random.nextBoolen(). Then R =
     * s*A/(B+1), reduced to lowest terms.
     * @param n such that 0 &le; A, B &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random BigRational.
     */
    @Override
    public BigRational random(int n, Random rnd) {
        BigInteger A;
        BigInteger B;
        A = new BigInteger(n, rnd); // always positive
        if (rnd.nextBoolean()) {
            A = A.negate();
        }
        B = new BigInteger(n, rnd); // always positive
        B = B.add(BigInteger.ONE);
        return RNRED(A, B);
    }


    /**
     * Rational number, random. Random integers A, B and a random sign s are
     * generated using BigInteger(n,random) and random.nextBoolen(). Then R =
     * s*A/(B+1), reduced to lowest terms.
     * @param NL such that 0 &le; A, B &le; (2<sup>n</sup>-1).
     * @return a random BigRational.
     */
    public static BigRational RNRAND(int NL) {
        return ONE.random(NL, random);
    }


    /**
     * Rational number sign.
     * @see edu.jas.structure.RingElem#signum()
     */
    @Override
    public int signum() {
        return num.signum();
    }


    /**
     * Rational number sign.
     * @param R BigRational.
     * @return R.signum().
     */
    public static int RNSIGN(BigRational R) {
        if (R == null) {
            return 0;
        }
        return R.signum();
    }


    /**
     * Rational number sum.
     * @param S BigRational.
     * @return this+S.
     */
    @Override
    public BigRational sum(BigRational S) {
        BigInteger D = null;
        BigInteger E, J1Y, J2Y;
        BigRational T;
        BigInteger R1 = null;
        BigInteger R2 = null;
        BigInteger RB2 = null;
        BigInteger S1 = null;
        BigInteger S2 = null;
        BigInteger SB2 = null;
        BigInteger T1;
        BigInteger T2;
        if (this.equals(ZERO)) {
            return S;
        }
        if (S.equals(ZERO)) {
            return this;
        }
        R1 = num; //this.numerator(); 
        R2 = den; //this.denominator();
        S1 = S.num;
        S2 = S.den;
        if (R2.equals(BigInteger.ONE) && S2.equals(BigInteger.ONE)) {
            T1 = R1.add(S1);
            T = new BigRational(T1, BigInteger.ONE);
            return T;
        }
        if (R2.equals(BigInteger.ONE)) {
            T1 = R1.multiply(S2);
            T1 = T1.add(S1);
            T = new BigRational(T1, S2);
            return T;
        }
        if (S2.equals(BigInteger.ONE)) {
            T1 = R2.multiply(S1);
            T1 = T1.add(R1);
            T = new BigRational(T1, R2);
            return T;
        }
        if (R2.equals(S2)) {
            D = R2;
        } else {
            D = R2.gcd(S2);
        }
        if (D.equals(BigInteger.ONE)) {
            RB2 = R2;
            SB2 = S2;
        } else {
            RB2 = R2.divide(D);
            SB2 = S2.divide(D);
        }
        J1Y = R1.multiply(SB2);
        J2Y = RB2.multiply(S1);
        T1 = J1Y.add(J2Y);
        if (T1.equals(BigInteger.ZERO)) {
            return ZERO;
        }
        if (!D.equals(BigInteger.ONE)) {
            if (T1.equals(D)) {
                E = D;
            } else {
                E = T1.gcd(D);
            }
            if (!E.equals(BigInteger.ONE)) {
                T1 = T1.divide(E);
                R2 = R2.divide(E);
            }
        }
        T2 = R2.multiply(SB2);
        T = new BigRational(T1, T2);
        return T;
    }


    /**
     * Rational number sum.
     * @param R BigRational.
     * @param S BigRational.
     * @return R+S.
     */
    public static BigRational RNSUM(BigRational R, BigRational S) {
        if (R == null) {
            return S;
        }
        return R.sum(S);
    }


    /**
     * Parse rational number from String.
     * @param s String.
     * @return BigRational from s.
     */
    @Override
    public BigRational parse(String s) {
        return new BigRational(s);
    }


    /**
     * Parse rational number from Reader.
     * @param r Reader.
     * @return next BigRational from r.
     */
    @Override
    public BigRational parse(Reader r) {
        return parse(StringUtil.nextString(r));
    }


    /**
     * {@link java.math.BigInteger} number least common multiple.
     * 
     * @param i0
     * @param i1
     * @return
     */
    private static BigInteger lcm(final BigInteger i0, final BigInteger i1) {
      if (i0.signum()==0 && i1.signum()==0) {
        return BigInteger.ZERO;
      }
      BigInteger a = i0.abs();
      BigInteger b = i1.abs();
      BigInteger gcd = i0.gcd(b);
      BigInteger lcm = (a.multiply(b)).divide(gcd);
      return lcm;
    }

    /**
     * Rational number greatest common divisor.
     * 
     * @param S BigRational.
     * @return gcd(this,S).
     */
    @Override
    public BigRational gcd(BigRational S) {
      if (S == null || S.isZERO()) {
        return this;
      }
      if (this.isZERO()) {
        return S;
      }
      BigInteger p = num.gcd(S.num);
      BigInteger q = lcm(den, S.den);
      return new BigRational(p, q);
    }

    /**
     * Rational number least common multiple.
     * 
     * @param S BigRational.
     * @return lcm(this,S).
     */
    public BigRational lcm(BigRational S) {
      if (S == null || S.isZERO()) {
        return ZERO;
      }
      if (this.isZERO()) {
        return ZERO;
      }
      return new BigRational(lcm(num, S.num), den.gcd(S.den));
    }

    /**
     * BigRational extended greatest common divisor.
     * @param S BigRational.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    @Override
    public BigRational[] egcd(BigRational S) {
        BigRational[] ret = new BigRational[3];
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
        BigRational half = new BigRational(1, 2);
        ret[0] = ONE;
        ret[1] = this.inverse().multiply(half);
        ret[2] = S.inverse().multiply(half);
        return ret;
    }


    /**
     * BigRational ceiling.
     * @return ceiling of this.
     */
    public BigInteger ceil() {
        if (isEntier()) {
            return num;
        }
        BigInteger[] qr = num.divideAndRemainder(den);
        //System.out.println("ceil: " + this + ", q = " + qr[0] + ", r = " +qr[1]);
        BigInteger q = qr[0];
        if (qr[1].signum() > 0) {
            q = q.add(BigInteger.ONE);
        }
        return q;
    }


    /**
     * BigRational floor.
     * @return floor of this.
     */
    public BigInteger floor() {
        if (isEntier()) {
            return num;
        }
        BigInteger[] qr = num.divideAndRemainder(den);
        //System.out.println("floor: " + this + ", q = " + qr[0] + ", r = " +qr[1]);
        BigInteger q = qr[0];
        if (qr[1].signum() < 0) {
            q = q.subtract(BigInteger.ONE);
        }
        return q;
    }


    /**
     * Returns the number of bits in the representation of this BigRational,
     * including a sign bit. For positive BigRational, this is equivalent to
     * {@code num.bitLength()+den.bitLength()}.)
     * @return number of bits in the representation of this BigRational,
     *         including a sign bit.
     */
    public long bitLength() {
        long n = num.bitLength();
        if (num.signum() < 0) {
            n++;
        }
        n++;
        n += den.bitLength();
        // den.signum() > 0
        n++;
        return n;
    }


    private boolean nonNegative = true;


    private boolean duplicates = true;


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
     * Set the iteration algorithm to no duplicate elements.
     */
    public void setNoDuplicatesIterator() {
        duplicates = false;
    }


    /**
     * Set the iteration algorithm to allow duplicate elements.
     */
    public void setDuplicatesIterator() {
        duplicates = true;
    }


    /**
     * Get a BigRational iterator.
     * @return a iterator over all rationals.
     */
    @Override
    public Iterator<BigRational> iterator() {
        if (duplicates) {
            return new BigRationalIterator(nonNegative);
        }
        return new BigRationalUniqueIterator(new BigRationalIterator(nonNegative));
    }


    /**
     * Get a BigRational iterator with no duplicates.
     * @return a iterator over all rationals without duplicates.
     */
    public Iterator<BigRational> uniqueIterator() {
        return new BigRationalUniqueIterator(new BigRationalIterator(nonNegative));
    }

}


/**
 * Big rational iterator. Uses Cantors diagonal enumeration.
 * @author Heinz Kredel
 */
class BigRationalIterator implements Iterator<BigRational> {


    /**
     * data structure.
     */
    BigRational curr;


    edu.jas.arith.BigInteger den;


    edu.jas.arith.BigInteger num;


    Iterator<edu.jas.arith.BigInteger> denit;


    Iterator<edu.jas.arith.BigInteger> numit;


    List<edu.jas.arith.BigInteger> denlist;


    List<edu.jas.arith.BigInteger> numlist;


    Iterator<edu.jas.arith.BigInteger> denlistit;


    Iterator<edu.jas.arith.BigInteger> numlistit;


    final boolean nonNegative;


    protected long level;


    /**
     * BigRational iterator constructor.
     */
    public BigRationalIterator() {
        this(false);
    }


    /**
     * BigRational iterator constructor.
     * @param nn indicator for a non-negative iterator, if true, false for an
     *            all iterator
     */
    public BigRationalIterator(boolean nn) {
        nonNegative = nn;
        curr = edu.jas.arith.BigRational.ZERO;
        level = 0;
        den = new edu.jas.arith.BigInteger(); // ZERO
        num = edu.jas.arith.BigInteger.ONE.copy();
        if (nonNegative) {
            den.setNonNegativeIterator();
        } else {
            den.setAllIterator();
        }
        num.setNonNegativeIterator();
        denit = den.iterator();
        numit = num.iterator();
        denlist = new ArrayList<edu.jas.arith.BigInteger>();
        numlist = new ArrayList<edu.jas.arith.BigInteger>();
        edu.jas.arith.BigInteger unused = denit.next(); // skip zero denominator
        unused = numit.next();
        if (unused == null) { // use for findbugs
            System.out.println("unused is null");
        }
        denlist.add(denit.next());
        numlist.add(numit.next());
        denlistit = denlist.iterator();
        numlistit = numlist.iterator();
    }


    /**
     * Test for availability of a next element.
     * @return true if the iteration has more elements, else false.
     */
    @Override
    public boolean hasNext() {
        return true;
    }


    /**
     * Get next rational.
     * @return next rational.
     */
    @Override
    public synchronized BigRational next() {
        BigRational r = curr;
        if (denlistit.hasNext() && numlistit.hasNext()) {
            BigInteger d = denlistit.next().val;
            BigInteger n = numlistit.next().val;
            //System.out.println(d + "//" + n);
            curr = BigRational.reduction(d, n);
            return r;
        }
        level++;
        if (level % 2 == 1) {
            Collections.reverse(denlist);
        } else {
            Collections.reverse(numlist);
        }
        denlist.add(denit.next());
        numlist.add(numit.next());
        if (level % 2 == 0) {
            Collections.reverse(denlist);
        } else {
            Collections.reverse(numlist);
        }
        //System.out.println("denlist = " + denlist);
        //System.out.println("numlist = " + numlist);
        denlistit = denlist.iterator();
        numlistit = numlist.iterator();
        BigInteger d = denlistit.next().val;
        BigInteger n = numlistit.next().val;
        //System.out.println(d + "//" + n);
        curr = BigRational.reduction(d, n);
        return r;
    }


    /**
     * Remove an element if allowed.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("cannot remove elements");
    }
}


/**
 * Big rational unique iterator. Uses Cantors diagonal enumeration, produces
 * distinct elements.
 * @author Heinz Kredel
 */
class BigRationalUniqueIterator implements Iterator<BigRational> {


    /**
     * data structure.
     */
    final Set<BigRational> unique;


    final Iterator<BigRational> ratit;


    /**
     * BigRational iterator constructor.
     */
    public BigRationalUniqueIterator() {
        this(BigRational.ONE.iterator());
    }


    /**
     * BigRational iterator constructor.
     * @param rit backing rational iterator of non unique elements
     */
    public BigRationalUniqueIterator(Iterator<BigRational> rit) {
        ratit = rit;
        unique = new HashSet<BigRational>();
    }


    /**
     * Test for availability of a next element.
     * @return true if the iteration has more elements, else false.
     */
    @Override
    public synchronized boolean hasNext() {
        return ratit.hasNext();
    }


    /**
     * Get next rational.
     * @return next rational.
     */
    @Override
    public synchronized BigRational next() {
        // TODO: use curr = BigRational.reduction(d, n);
        // with curr.d == d to avoid unique
        BigRational r = ratit.next();
        while (unique.contains(r)) {
            //System.out.println("duplicate " + r);
            r = ratit.next();
        }
        unique.add(r);
        return r;
    }


    /**
     * Remove an element if allowed.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("cannot remove elements");
    }
}
