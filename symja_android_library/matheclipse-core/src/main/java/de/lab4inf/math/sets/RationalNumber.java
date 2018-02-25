/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package de.lab4inf.math.sets;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import de.lab4inf.math.L4MLogger;
import de.lab4inf.math.Operand;
import de.lab4inf.math.Rational;
import de.lab4inf.math.util.Accuracy;
import de.lab4inf.math.util.Randomizer;

import static de.lab4inf.math.Constants.L4MLOGGER;
import static java.lang.Math.floor;

/**
 * Approximation of the real numbers by
 * a rational number r=a/b. The rational
 * numbers can have arbitrary precision,
 * allowing accurate computations.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: RationalNumber.java,v 1.9 2014/11/18 21:50:21 nwulff Exp $
 * @since 22.11.2006
 */
public class RationalNumber extends Number implements Rational {
    /**
     * default accuracy to use.
     */
    public static final double EPS = 1.E-40;
    /**
     * zero (0.0) as rational number.
     */
    public static final RationalNumber ZERO = new RationalNumber(0);
    /**
     * one half (0.5) as rational number.
     */
    public static final RationalNumber HALF = new RationalNumber(1, 2);
    /**
     * one (1.0) as rational number.
     */
    public static final RationalNumber ONE = new RationalNumber(1);
    /**
     * minus one (-1.0) as rational number.
     */
    public static final RationalNumber MINUS_ONE = new RationalNumber(-1);
    /**
     * two (2.0) as rational number.
     */
    public static final RationalNumber TWO = new RationalNumber(2);
    /**
     * PI with 100 digits as rational number.
     */
    public static final RationalNumber PI = new RationalNumber(new BigInteger(
            "290059477841212181737978420557654767128189402403451"
                    + "5237231796026308354288358265194997650692411040761554492153" + "8509045111980032"), new BigInteger(
            "923287993781660018891577792607872839441723954406"
                    + "09061374454200329478784181736733986393144506209415130324" + "94837587807456247875"));
    /**
     * PI/2 with as rational number.
     */
    public static final RationalNumber PIH = PI.halved();
    /**
     * serialization constant.
     */
    private static final long serialVersionUID = -5244668687918373221L;
    /**
     * constant for -1.
     */
    private static final BigInteger MINUS_ONE_INTEGER = BigInteger.valueOf(-1L);
    static L4MLogger logger = L4MLogger.getLogger(L4MLOGGER);
    private BigInteger numerator, divider;
    private double eps = EPS;

    /**
     * Construct the zero Rational.
     */
    public RationalNumber() {
        this(BigInteger.ZERO, BigInteger.ONE);
    }

    /**
     * Construct a Rational with given numerator and divider=1.
     *
     * @param numerator of the rational
     */
    public RationalNumber(final long numerator) {
        this(BigInteger.valueOf(numerator), BigInteger.ONE);
    }

    /**
     * Construct a Rational with given numerator and divider.
     *
     * @param numerator of the rational
     * @param divider   of the rational
     */
    public RationalNumber(final long numerator, final long divider) {
        this(BigInteger.valueOf(numerator), BigInteger.valueOf(divider));
    }

    /**
     * Construct a Rational with given numerator and divider=1.
     *
     * @param numerator of the rational
     */
    public RationalNumber(final BigInteger numerator) {
        this(numerator, BigInteger.ONE);
    }

    /**
     * Copy constructor.
     *
     * @param rational to copy
     */
    public RationalNumber(final RationalNumber rational) {
        this(rational.numerator, rational.divider);
    }

    /**
     * Construct a Rational form a real double value.
     * Will use approximately 15 digits precision.
     *
     * @param real to use
     */
    public RationalNumber(final double real) {
        final double deps = 2.E-15; // minimal double precision
        final int maxd = 16;
        final boolean sign = real < 0;
        BigInteger num, div = BigInteger.ONE;
        double r, y, x = real;
        int digits = 1;
        if (sign)
            x = -x;
        y = floor(x);
        r = x - y;
        num = bint(y);
        while (r > deps && ++digits <= maxd) {
            div = div.multiply(BigInteger.TEN);
            x = 10 * r;
            y = floor(x);
            num = num.multiply(BigInteger.TEN).add(bint(y));
            r = x - y;
        }

        if (sign)
            num = num.negate();
        divider = div;
        numerator = num;
        gcdTruncation();
    }

    /**
     * Construct a Rational with given numerator and divider.
     *
     * @param numerator of the rational
     * @param divider   of the rational
     */
    public RationalNumber(final BigInteger numerator, final BigInteger divider) {
        if (numerator == null) {
            throw new IllegalArgumentException("numerator is null pointer");
        }
        if (divider == null || BigInteger.ZERO.equals(divider)) {
            throw new IllegalArgumentException("divider is zero");
        }
        if (divider.compareTo(BigInteger.ZERO) < 1) {
            // if (divider.doubleValue()<0) {
            this.numerator = numerator.multiply(MINUS_ONE_INTEGER);
            this.divider = divider.multiply(MINUS_ONE_INTEGER);
        } else {
            this.numerator = new BigInteger(numerator.toByteArray());
            this.divider = new BigInteger(divider.toByteArray());
        }
        gcdTruncation();
    }

    /**
     * Return the absolute value of x.
     *
     * @param x the rational to calculate abs for
     * @return abs(x)
     */
    public static RationalNumber abs(final RationalNumber x) {
        return x.abs();
    }

    /**
     * Return the absolute value of x as double.
     *
     * @param x the rational to calculate abs for
     * @return abs(x)
     */
    public static double dabs(final RationalNumber x) {
        return x.dabs();
    }

    /**
     * Calculate x modulo y.
     *
     * @param x the dividend
     * @param y the modulus or divider
     * @return x % y
     */
    @Operand(symbol = "%")
    public static RationalNumber mod(final RationalNumber x, final RationalNumber y) {
        return x.mod(y);
    }

    /**
     * Return the difference between x and y as double.
     *
     * @param x first argument
     * @param y second argumet
     * @return difference as double
     */
    public static double diff(final RationalNumber x, final RationalNumber y) {
        return x.minus(y).doubleValue();
    }

    /**
     * Calculate the sqrt of x as rational.
     *
     * @param x rational to find the root for
     * @return sqrt(x)
     */
    public static RationalNumber sqrt(final RationalNumber x) {
        return x.sqrt();
    }

    /**
     * Calculate the exponential of x as rational.
     *
     * @param x rational to calculate the exponential
     * @return exp(x)
     */
    public static RationalNumber exp(final RationalNumber x) {
        return x.exp();
    }

    /**
     * Calculate the natural logarithm of x as rational.
     *
     * @param x rational to calculate the logarithm of
     * @return ln(x)
     */
    public static RationalNumber ln(final RationalNumber x) {
        return x.ln();
    }

    /**
     * Calculate the sine of x as rational.
     *
     * @param x the argument
     * @return sin(x)
     */
    public static RationalNumber sin(final RationalNumber x) {
        return x.sin();
    }

    /**
     * Calculate the cosine of x as rational.
     *
     * @param x the argument
     * @return cos(x)
     */
    public static RationalNumber cos(final RationalNumber x) {
        return x.cos();
    }

    /**
     * Calculate the tangent of x as rational.
     *
     * @param x the argument
     * @return tan(x)
     */
    public static RationalNumber tan(final RationalNumber x) {
        return x.tan();
    }

    /**
     * Calculate the cotangent of x as rational.
     *
     * @param x the argument
     * @return cotan(x)
     */
    public static RationalNumber cotan(final RationalNumber x) {
        return x.cotan();
    }

    /**
     * Helper to make a BigInteger from long
     *
     * @param x long to convert
     * @return BigInteger(x)
     */
    protected final BigInteger bint(final long x) {
        return BigInteger.valueOf(x);
    }

    /**
     * Helper to make a BigInteger from the integer part of a double
     *
     * @param x double to convert
     * @return BigInteger(x)
     */
    protected final BigInteger bint(final double x) {
        return bint((long) x);
    }

    /**
     * Do a GCD truncation of numerator and divider.
     */
    private void gcdTruncation() {
        BigInteger gcd = this.numerator.gcd(this.divider);
        if (gcd.doubleValue() < 0) {
            gcd = new BigInteger(-1, gcd.toByteArray());
        }

        if (gcd.compareTo(BigInteger.ONE) > 0) {
            this.numerator = this.numerator.divide(gcd);
            this.divider = this.divider.divide(gcd);
        }
        if (this.divider.compareTo(BigInteger.ZERO) < 1) {
            // if (this.divider.doubleValue()<0) {
            throw new IllegalStateException("divder " + divider);
        }
    }

    /**
     * Signal if the rational is negative.
     *
     * @return negative flag
     */
    public boolean isNegative() {
        return numerator.compareTo(BigInteger.ZERO) < 0;
    }

    /**
     * Signal if the rational is zero.
     *
     * @return zero flag
     */
    @Override
    public boolean isZero() {
        return numerator.equals(BigInteger.ZERO);
    }

    /**
     * Signal if the rational is one.
     *
     * @return one flag
     */
    @Override
    public boolean isOne() {
        return numerator.equals(BigInteger.ONE) && divider.equals(BigInteger.ONE);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Number#intValue()
     */
    @Override
    public int intValue() {
        if (divider.equals(BigInteger.ONE)) {
            return numerator.intValue();
        }
        return (int) doubleValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Number#longValue()
     */
    @Override
    public long longValue() {
        if (divider.equals(BigInteger.ONE)) {
            return numerator.longValue();
        }
        return (long) doubleValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Number#floatValue()
     */
    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Number#doubleValue()
     */
    @Override
    public double doubleValue() {
        double y;
        final BigDecimal n = new BigDecimal(numerator);
        final BigDecimal d = new BigDecimal(divider);
        final BigDecimal r = n.divide(d, MathContext.DECIMAL128);
        y = r.doubleValue();
        return y;
    }

    /**
     * Return the absolute value of this Rational.
     *
     * @return abs(this)
     */
    @Override
    public RationalNumber abs() {
        if (this.isNegative())
            return new RationalNumber(numerator.multiply(MINUS_ONE_INTEGER), divider);
        return this;
    }

    /**
     * Return the absolute value of this Rational squared.
     *
     * @return abs(this)**2
     */
    @Override
    public RationalNumber abs2() {
        return this.multiply(this);
    }

    /**
     * Return the absolute value of this Rational as double.
     *
     * @return abs(this)
     */
    public double dabs() {
        return Math.abs(doubleValue());
    }

    /**
     * Calculate this modulo x.
     *
     * @param x the modulus or divider
     * @return this % x
     */
    @Operand(symbol = "%")
    public RationalNumber mod(final RationalNumber x) {
        RationalNumber q = this.div(x);
        final BigDecimal n = new BigDecimal(q.numerator);
        final BigDecimal d = new BigDecimal(q.divider);
        final BigInteger p = n.divide(d, MathContext.DECIMAL128).toBigInteger();
        q = x.multiply(p);
        q = this.minus(q);
        if (p.compareTo(BigInteger.ZERO) < 0) {
            q = q.plus(x);
        }
        return q;
    }

    /**
     * Add the parameter x.
     *
     * @param x rational to add
     * @return this+x
     */
    @Override
    @Operand(symbol = "+")
    public RationalNumber plus(final Rational x) {
        BigInteger u = divider.multiply(x.numerator());
        BigInteger v = numerator.multiply(x.divider());
        u = u.add(v);
        v = divider.multiply(x.divider());
        final RationalNumber y = new RationalNumber(u, v);
        return y;
    }

    /**
     * Subtract the parameter x.
     *
     * @param x rational to subtract
     * @return this-x
     */
    @Override
    @Operand(symbol = "-")
    public RationalNumber minus(final Rational x) {
        BigInteger v = divider.multiply(x.numerator());
        BigInteger u = numerator.multiply(x.divider());
        u = u.subtract(v);
        v = divider.multiply(x.divider());
        final RationalNumber y = new RationalNumber(u, v);
        return y;
    }

    /**
     * Multiply with the parameter x.
     *
     * @param x rational to multiply with
     * @return this*x
     */
    @Override
    @Operand(symbol = "*")
    public RationalNumber multiply(final Rational x) {
        final BigInteger u = numerator.multiply(x.numerator());
        final BigInteger v = divider.multiply(x.divider());
        final RationalNumber y = new RationalNumber(u, v);
        return y;
    }

    /**
     * Multiply with the parameter x.
     *
     * @param x integer to multiply with
     * @return this*x
     */
    @Operand(symbol = "*")
    public RationalNumber multiply(final BigInteger x) {
        final BigInteger u = numerator.multiply(x);
        final RationalNumber y = new RationalNumber(u, divider);
        return y;
    }

    /**
     * Multiply with the parameter x.
     *
     * @param x integer to multiply with
     * @return this*x
     */
    @Override
    @Operand(symbol = "*")
    public RationalNumber multiply(final long x) {
        return multiply(new BigInteger(Long.toString(x)));
    }

    /**
     * Multiply with the parameter x.
     *
     * @param x integer to multiply with
     * @return this*x
     */
    @Override
    @Operand(symbol = "*")
    public RationalNumber multiply(final double x) {
        final long y = Math.round(Math.floor(x));
        if (Math.abs(x - y) > Accuracy.DEPS) {
            logger.warning(String.format("scaling by none integer %f", x));
        }
        return multiply(y);
    }

    /**
     * Divide by the parameter x.
     *
     * @param x rational to divide by
     * @return this/x
     */
    @Override
    @Operand(symbol = "/")
    public RationalNumber div(final Rational x) {
        final BigInteger u = numerator.multiply(x.divider());
        final BigInteger v = divider.multiply(x.numerator());
        final RationalNumber y = new RationalNumber(u, v);
        return y;
    }

    /**
     * Divide by the parameter x.
     *
     * @param x integer to divide by
     * @return this/x
     */
    @Operand(symbol = "/")
    public RationalNumber div(final BigInteger x) {
        final BigInteger v = divider.multiply(x);
        final RationalNumber y = new RationalNumber(numerator, v);
        return y;
    }

    /**
     * Divide by the parameter x.
     *
     * @param x integer to divide by
     * @return this/x
     */
    @Override
    @Operand(symbol = "/")
    public RationalNumber div(final long x) {
        return div(new BigInteger(Long.toString(x)));
    }

    /**
     * Return this halved.
     *
     * @return this/2
     */
    public RationalNumber halved() {
        final RationalNumber y = new RationalNumber(numerator, divider.shiftLeft(1));
        return y;
    }

    /**
     * Return this doubled.
     *
     * @return 2*this
     */
    public RationalNumber twice() {
        final RationalNumber y = new RationalNumber(numerator.shiftLeft(1), divider);
        return y;
    }

    /**
     * Return the difference as double.
     *
     * @param x value to compare to
     * @return this-x as double
     */
    public double diff(final RationalNumber x) {
        return diff(this, x);
    }

    /**
     * Calculate the sqrt of this rational with high precision.
     *
     * @return sqrt(this) as rational
     */
    @Override
    public RationalNumber sqrt() {
        double delta;
        final double deps = eps;
        RationalNumber t, y = this;
        do {
            t = (y.plus(this.div(y))).halved();
            t.setEps(deps);
            delta = diff(t, y);
            y = t;
            // y.setEps(Math.abs(delta));
            // logger.info(String.format("%s eps:%.2e", y,delta));
        } while (Math.abs(delta) > deps);
        y.setEps(deps);
        return y;
    }

    /**
     * Calculate the exponential of this rational with high precision.
     *
     * @return exp(this) as rational
     */
    @Override
    public RationalNumber exp() {
        double delta;
        BigInteger fac = BigInteger.ONE;
        RationalNumber xk = this, zk, y, sum = ONE;
        long k = 1;
        do {
            zk = xk.div(fac);
            sum = (sum.plus(xk.div(fac)));
            delta = zk.doubleValue();
            fac = fac.multiply(BigInteger.valueOf(++k));
            xk = xk.multiply(this);
            y = sum;
            // y.setEps(Math.abs(delta));
            // logger.info(String.format("%s eps:%.2e", y,delta));
        } while (Math.abs(delta) > eps);
        y.setEps(eps);
        return y;
    }

    /**
     * Calculate the sine of this rational with high precision.
     *
     * @return sine(this) as rational
     */
    @Override
    public RationalNumber sin() {
        double delta;
        BigInteger fac = BigInteger.ONE;
        RationalNumber zk, y, sum = ZERO;
        final RationalNumber x0 = this; // .mod(PI);
        RationalNumber xk = x0;
        final RationalNumber xsq = xk.multiply(xk);
        long k = 1;
        boolean odd = true;
        do {
            zk = xk.div(fac);
            if (odd) {
                sum = sum.plus(zk);
            } else {
                sum = sum.minus(zk);
            }
            odd = !odd;
            delta = zk.doubleValue();
            fac = fac.multiply(BigInteger.valueOf(++k));
            fac = fac.multiply(BigInteger.valueOf(++k));
            xk = xk.multiply(xsq);
            y = sum;
            // y.setEps(Math.abs(delta));
            // logger.info(String.format("%s eps:%.2e", y,delta));
        } while (Math.abs(delta) > eps);
        y.setEps(eps);
        return y;
    }

    /**
     * Calculate the cosine of this rational with high precision.
     *
     * @return cosine(this) as rational
     */
    @Override
    public RationalNumber cos() {
        double delta;
        BigInteger fac = BigInteger.ONE;
        RationalNumber zk, y, sum = ONE;
        RationalNumber xk = this; // .mod(PI);
        final RationalNumber xsq = xk.multiply(xk);
        long k = 0;
        boolean odd = false;
        xk = ONE;
        do {
            fac = fac.multiply(BigInteger.valueOf(++k));
            fac = fac.multiply(BigInteger.valueOf(++k));
            xk = xk.multiply(xsq);
            zk = xk.div(fac);
            if (odd) {
                sum = sum.plus(zk);
            } else {
                sum = sum.minus(zk);
            }
            odd = !odd;
            delta = zk.doubleValue();

            y = sum;
            // y.setEps(Math.abs(delta));
            // logger.info(String.format("%s eps:%.2e", y,delta));
        } while (Math.abs(delta) > eps);
        y.setEps(eps);
        return y;
    }

    /**
     * Calculate the tangent of this rational with high precision.
     *
     * @return tan(this) as rational
     */
    @Override
    public RationalNumber tan() {
        final RationalNumber s = sin();
        final RationalNumber c = cos();
        final RationalNumber t = s.div(c);
        return t;
    }

    /**
     * Calculate the cotangent of this rational with high precision.
     *
     * @return cotan(this) as rational
     */
    public RationalNumber cotan() {
        final RationalNumber s = sin();
        final RationalNumber c = cos();
        final RationalNumber ct = c.div(s);
        return ct;
    }

    /**
     * Calculate the natural logarithm of this rational with high precision.
     *
     * @return ln(this) as rational
     */
    public RationalNumber ln() {
        double delta;
        long k = 1;
        final RationalNumber z = this.minus(ONE).div(this.plus(ONE)), z2 = z.multiply(z);
        RationalNumber xk = z, y, zk, sum = ZERO;
        do {
            zk = xk.div(k);
            sum = sum.plus(zk);
            // sum.setEps(eps);
            delta = zk.doubleValue();
            k += 2;
            xk = xk.multiply(z2);
            y = sum;
        } while (Math.abs(delta) > eps);
        y = y.twice();
        y.setEps(eps);
        return y;
    }

    /**
     * The epsilon precision used within
     * calculations as sqrt.
     *
     * @param eps The eps to set.
     */
    public void setEps(final double eps) {
        final double e = Math.min(1, eps);
        if (0 < e)
            this.eps = e;
    }

    /**
     * Special toString implementation using a fixed
     * digets format.
     *
     * @param digets number of decimal places to show
     * @return String representation
     */
    public String toString(final int digets) {
        int d = digets;
        int count = 0;
        BigInteger x, y, z, p, q;
        final StringBuffer sb = new StringBuffer();
        if (isNegative()) {
            sb.append("-");
            p = new BigInteger(numerator.multiply(MINUS_ONE_INTEGER).toByteArray());
        } else {
            p = new BigInteger(numerator.toByteArray());
        }
        q = new BigInteger(divider.toByteArray());
        x = p.divide(q);
        y = x;
        sb.append(x);
        sb.append(",");
        while (d-- > 0) {
            count--;
            y = y.multiply(BigInteger.TEN);
            p = p.multiply(BigInteger.TEN);
            x = p.divide(q);
            z = x.subtract(y);
            y = x;
            sb.append(z.intValue());
            if (count % 5 == 0)
                sb.append(' ');
        }
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString(false);
    }

    /**
     * ToString method with indication for full numerator/divider display.
     *
     * @param full if true numerator/divider will be displayed
     * @return string representation
     */
    public String toString(final boolean full) {
        final int digets = -(int) Math.log10(eps);
        if (full) {
            return String.format("[%s / %s]=%s", numerator, divider, toString(digets));
        }
        return String.format("%s", toString(digets));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @Operand(symbol = "==")
    public boolean equals(final Object o) {
        if (o == null)
            return false;
        if (this == o)
            return true;
        if (o.getClass() == this.getClass()) {
            final RationalNumber r = (RationalNumber) o;
            return numerator.equals(r.numerator) && divider.equals(r.divider);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return numerator.hashCode() ^ divider.hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    @Operand(symbol = "<")
    public int compareTo(final Rational x) {
        BigInteger a, b;

        a = numerator.multiply(x.divider());
        b = x.numerator().multiply(divider);

        return a.compareTo(b);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Rational#newRational(long, long)
     */
    @Override
    public Rational newRational(final long n, final long d) {
        return new RationalNumber(n, d);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Rational#newRational(long)
     */
    @Override
    public Rational newRational(final long n) {
        return new RationalNumber(n, 1L);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Rational#plus(long)
     */
    @Override
    public Rational plus(final long x) {
        return plus(newRational(x));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Rational#minus(long)
     */
    @Override
    public Rational minus(final long x) {
        return minus(newRational(x));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Rational#numerator()
     */
    @Override
    public BigInteger numerator() {
        return numerator;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Rational#divider()
     */
    @Override
    public BigInteger divider() {
        return divider;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Factory#create()
     */
    @Override
    public Rational create() {
        return new RationalNumber();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#lt(java.lang.Object)
     */
    @Override
    public boolean lt(final Rational that) {
        return this.doubleValue() < that.doubleValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#leq(java.lang.Object)
     */
    @Override
    public boolean leq(final Rational that) {
        return this.doubleValue() <= that.doubleValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#eq(java.lang.Object)
     */
    @Override
    public boolean eq(final Rational that) {
        return this.equals(that);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#gt(java.lang.Object)
     */
    @Override
    public boolean gt(final Rational that) {
        return this.doubleValue() > that.doubleValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#geq(java.lang.Object)
     */
    @Override
    public boolean geq(final Rational that) {
        return this.doubleValue() >= that.doubleValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numerable#getZero()
     */
    @Override
    public Rational getZero() {
        return ZERO;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numerable#getOne()
     */
    @Override
    public Rational getOne() {
        return ONE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#getMinusOne()
     */
    @Override
    public Rational getMinusOne() {
        return MINUS_ONE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#rnd()
     */
    @Override
    public Rational rnd() {
        return new RationalNumber(50 - Randomizer.rndInt(100), 1 + Randomizer.rndInt(10));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#create(double)
     */
    @Override
    public Rational create(final double x) {
        return new RationalNumber(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#difference(de.lab4inf.math.Numeric)
     */
    @Override
    public double difference(final Rational that) {
        return this.minus(that).doubleValue();
    }
}
 