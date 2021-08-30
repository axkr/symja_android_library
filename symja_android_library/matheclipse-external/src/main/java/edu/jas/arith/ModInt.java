/*
 * $Id$
 */

package edu.jas.arith;


import edu.jas.structure.GcdRingElem;
import edu.jas.structure.NotInvertibleException;


/**
 * ModInt class with RingElem interface. Objects of this class are immutable.
 * @author Heinz Kredel
 * @see ModInteger
 */

public final class ModInt implements GcdRingElem<ModInt>, Modular {


    /**
     * ModIntRing reference.
     */
    public final ModIntRing ring;


    /**
     * Value part of the element data structure.
     */
    public final int val;


    /**
     * The constructor creates a ModInt object from a ModIntRing and a value
     * part.
     * @param m ModIntRing.
     * @param a math.BigInteger.
     */
    public ModInt(ModIntRing m, java.math.BigInteger a) {
        this(m, a.mod(m.getModul()).intValue());
    }


    /**
     * The constructor creates a ModInt object from a ModIntRing and a int value
     * part.
     * @param m ModIntRing.
     * @param a int.
     */
    public ModInt(ModIntRing m, int a) {
        ring = m;
        int v = a % ring.modul;
        val = (v >= 0 ? v : v + ring.modul);
    }


    /**
     * The constructor creates a ModInt object from a ModIntRing and a long
     * value part.
     * @param m ModIntRing.
     * @param a long.
     */
    public ModInt(ModIntRing m, long a) {
        ring = m;
        int v = (int) (a % (long)ring.modul);
        val = (v >= 0 ? v : v + ring.modul);
    }


    /**
     * The constructor creates a ModInt object from a ModIntRing and a Int value
     * part.
     * @param m ModIntRing.
     * @param a Int.
     */
    public ModInt(ModIntRing m, Integer a) {
        this(m, a.intValue());
    }


    /**
     * The constructor creates a ModInt object from a ModIntRing and a Long
     * value part.
     * @param m ModIntRing.
     * @param a long.
     */
    public ModInt(ModIntRing m, Long a) {
        this(m, a.longValue());
    }


    /**
     * The constructor creates a ModInt object from a ModIntRing and a String
     * value part.
     * @param m ModIntRing.
     * @param s String.
     */
    public ModInt(ModIntRing m, String s) {
        this(m, Integer.valueOf(s.trim()));
    }


    /**
     * The constructor creates a 0 ModInt object from a given ModIntRing.
     * @param m ModIntRing.
     */
    public ModInt(ModIntRing m) {
        this(m, 0);
    }


    /**
     * Get the value part.
     * @return val.
     */
    public int getVal() {
        return val;
    }


    /**
     * Get the module part.
     * @return modul.
     */
    public int getModul() {
        return ring.modul;
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public ModIntRing factory() {
        return ring;
    }


    /**
     * Get the symmetric value part.
     * @return val with -modul/2 &le; val &lt; modul/2.
     */
    public int getSymmetricVal() {
        if ((val + val) > ring.modul) {
            // val > m/2 as 2*val > m, make symmetric to 0
            return val - ring.modul;
        }
        return val;
    }


    /**
     * Return a BigInteger from this Element.
     * @return a BigInteger of this.
     */
    public BigInteger getInteger() {
        return new BigInteger(val);
    }


    /**
     * Return a symmetric BigInteger from this Element.
     * @return a symmetric BigInteger of this.
     */
    public BigInteger getSymmetricInteger() {
        int v = val;
        if ((val + val) > ring.modul) {
            // val > m/2 as 2*val > m, make symmetric to 0
            v = val - ring.modul;
        }
        return new BigInteger(v);
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public ModInt copy() {
        return new ModInt(ring, val);
    }


    /**
     * Is ModInt number zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return val == 0;
    }


    /**
     * Is ModInt number one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return val == 1L;
    }


    /**
     * Is ModInt number a unit.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if (isZERO()) {
            return false;
        }
        if (ring.isField()) {
            return true;
        }
        int g = gcd(ring.modul, val);
        return (g == 1L || g == -1L);
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Integer.toString(val);
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
        return factory().toScript();
    }


    /**
     * ModInt comparison.
     * @param b ModInt.
     * @return sign(this-b).
     */
    @Override
    public int compareTo(ModInt b) {
        int v = b.val;
        if (ring != b.ring) {
            v = v % ring.modul;
        }
        if (val > v) {
            return 1;
        }
        return (val < v ? -1 : 0);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof ModInt)) {
            return false;
        }
        return (0 == compareTo((ModInt) b));
    }


    /**
     * Hash code for this ModInt.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return val;
    }


    /**
     * ModInt absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public ModInt abs() {
        return new ModInt(ring, (val < 0 ? -val : val));
    }


    /**
     * ModInt negative.
     * @see edu.jas.structure.RingElem#negate()
     * @return -this.
     */
    public ModInt negate() {
        return new ModInt(ring, -val);
    }


    /**
     * ModInt signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        if (val > 0) {
            return 1;
        }
        return (val < 0 ? -1 : 0);
    }


    /**
     * ModInt subtraction.
     * @param S ModInt.
     * @return this-S.
     */
    public ModInt subtract(ModInt S) {
        return new ModInt(ring, val - S.val);
    }


    /**
     * ModInt divide.
     * @param S ModInt.
     * @return this/S.
     */
    public ModInt divide(ModInt S) {
        try {
            return multiply(S.inverse());
        } catch (NotInvertibleException e) {
            try {
                if ((val % S.val) == 0) {
                    return new ModInt(ring, val / S.val);
                }
                throw new NotInvertibleException(e.getCause());
            } catch (ArithmeticException a) {
                throw new NotInvertibleException(a.getCause());
            }
        }
    }


    /**
     * ModInt inverse.
     * @see edu.jas.structure.RingElem#inverse()
     * @throws NotInvertibleException if the element is not invertible.
     * @return S with S=1/this if defined.
     */
    public ModInt inverse() /*throws NotInvertibleException*/ {
        try {
            return new ModInt(ring, modInverse(val, ring.modul));
        } catch (ArithmeticException e) {
            int g = gcd(val, ring.modul);
            int f = ring.modul / g;
            throw new ModularNotInvertibleException(e, new BigInteger(ring.modul), new BigInteger(g),
                            new BigInteger(f));
        }
    }


    /**
     * ModInt remainder.
     * @param S ModInt.
     * @return remainder(this,S).
     */
    public ModInt remainder(ModInt S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException("division by zero");
        }
        if (S.isONE()) {
            return ring.getZERO();
        }
        if (S.isUnit()) {
            return ring.getZERO();
        }
        return new ModInt(ring, val % S.val);
    }


    /**
     * ModInt multiply.
     * @param S ModInt.
     * @return this*S.
     */
    public ModInt multiply(ModInt S) {
        return new ModInt(ring, val * S.val);
    }


    /**
     * ModInt summation.
     * @param S ModInt.
     * @return this+S.
     */
    public ModInt sum(ModInt S) {
        return new ModInt(ring, val + S.val);
    }


    /**
     * ModInteger greatest common divisor.
     * @param S ModInteger.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    public ModInt gcd(ModInt S) {
        if (S.isZERO()) {
            return this;
        }
        if (isZERO()) {
            return S;
        }
        if (isUnit() || S.isUnit()) {
            return ring.getONE();
        }
        return new ModInt(ring, gcd(val, S.val));
    }


    /**
     * ModInteger extended greatest common divisor.
     * @param S ModInteger.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    public ModInt[] egcd(ModInt S) {
        ModInt[] ret = new ModInt[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if (S == null || S.isZERO()) {
            ret[0] = this;
            return ret;
        }
        if (isZERO()) {
            ret[0] = S;
            return ret;
        }
        if (isUnit() || S.isUnit()) {
            ret[0] = ring.getONE();
            if (isUnit() && S.isUnit()) {
                //ModInt half = (new ModInt(ring, 2L)).inverse();
                //ret[1] = this.inverse().multiply(half);
                //ret[2] = S.inverse().multiply(half);
                // (1-1*this)/S
                ret[1] = ring.getONE();
                ModInt x = ret[0].subtract(ret[1].multiply(this));
                ret[2] = x.divide(S);
                return ret;
            }
            if (isUnit()) {
                // oder inverse(S-1)?
                ret[1] = this.inverse();
                ret[2] = ring.getZERO();
                return ret;
            }
            // if ( s.isUnit() ) {
            // oder inverse(this-1)?
            ret[1] = ring.getZERO();
            ret[2] = S.inverse();
            return ret;
            //}
        }
        //System.out.println("this = " + this + ", S = " + S);
        int q = this.val;
        int r = S.val;
        int c1 = 1; // BigInteger.ONE.val;
        int d1 = 0; // BigInteger.ZERO.val;
        int c2 = 0; // BigInteger.ZERO.val;
        int d2 = 1; // BigInteger.ONE.val;
        int x1;
        int x2;
        while (r != 0) {
            //qr = q.divideAndRemainder(r);
            int a = q / r;
            int b = q % r;
            q = a;
            x1 = c1 - q * d1;
            x2 = c2 - q * d2;
            c1 = d1;
            c2 = d2;
            d1 = x1;
            d2 = x2;
            q = r;
            r = b;
        }
        //System.out.println("q = " + q + "\n c1 = " + c1 + "\n c2 = " + c2);
        ret[0] = new ModInt(ring, q);
        ret[1] = new ModInt(ring, c1);
        ret[2] = new ModInt(ring, c2);
        return ret;
    }


    /**
     * Int greatest common divisor.
     * @param T int.
     * @param S int.
     * @return gcd(T,S).
     */
    public int gcd(int T, int S) {
        if (S == 0) {
            return T;
        }
        if (T == 0) {
            return S;
        }
        int a = T;
        int b = S;
        while (b != 0) {
            int r = a % b;
            a = b;
            b = r;
        }
        return a;
    }


    /**
     * Int half extended greatest common divisor.
     * @param T int.
     * @param S int.
     * @return [ gcd(T,S), a ] with a*T + b*S = gcd(T,S).
     */
    public int[] hegcd(int T, int S) {
        int[] ret = new int[2];
        if (S == 0) {
            ret[0] = T;
            ret[1] = 1;
            return ret;
        }
        if (T == 0) {
            ret[0] = S;
            ret[1] = 0;
            return ret;
        }
        //System.out.println("hegcd, T = " + T + ", S = " + S);
        int a = T;
        int b = S;
        int a1 = 1;
        int b1 = 0;
        while (b != 0) {
            int q = a / b;
            int r = a % b;
            a = b;
            b = r;
            int r1 = a1 - q * b1;
            a1 = b1;
            b1 = r1;
        }
        if (a1 < 0) {
            a1 += S;
        }
        ret[0] = a;
        ret[1] = a1;
        return ret;
    }


    /**
     * Int modular inverse.
     * @param T int.
     * @param m int.
     * @return a with with a*T = 1 mod m.
     */
    public int modInverse(int T, int m) {
        if (T == 0) {
            throw new NotInvertibleException("zero is not invertible");
        }
        int[] hegcd = hegcd(T, m);
        int a = hegcd[0];
        if (!(a == 1L || a == -1L)) { // gcd != 1
            throw new ModularNotInvertibleException("element not invertible, gcd != 1", new BigInteger(m),
                            new BigInteger(a), new BigInteger(m / a));
        }
        int b = hegcd[1];
        if (b == 0) { // when m divides this, e.g. m.isUnit()
            throw new NotInvertibleException("element not invertible, divisible by modul");
        }
        if (b < 0) {
            b += m;
        }
        return b;
    }


    /**
     * Returns the number of bits in the representation of this ModInt,
     * including a sign bit.
     * @return number of bits in the representation of this ModInt, including a
     *         sign bit.
     */
    public int bitLength() {
        return (int) BigInteger.bitLength(val);
    }

}
