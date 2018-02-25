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

import de.lab4inf.math.Complex;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.Operand;
import de.lab4inf.math.util.Randomizer;

import static de.lab4inf.math.util.Accuracy.isSimilar;
import static java.lang.Math.PI;
import static java.lang.Math.atan2;

/**
 * Implementation of a complex number.
 * Some of the methods are suitable for Groovy operator overloading.
 *
 * @author nwulff
 * @version $Id: ComplexNumber.java,v 1.19 2014/11/18 21:50:21 nwulff Exp $
 * @since 17.10.2005
 */
public final class ComplexNumber extends L4MObject implements Complex/* , Serializable */ {
    /**
     * Complex unity constant.
     */
    public static final ComplexNumber ONE = new ComplexNumber(1);
    /**
     * Complex negativ unity constant.
     */
    public static final ComplexNumber MINUS_ONE = new ComplexNumber(-1);
    /**
     * Complex zero constant.
     */
    public static final ComplexNumber ZERO = new ComplexNumber(0);
    /**
     * Complex imaginary constant.
     */
    public static final ComplexNumber IMAG = new ComplexNumber(0, 1);
    /**
     * msg for divide by zero error .
     */
    private static final String DIVIDE_BY_ZERO = "divide by zero";
    /**
     * real and imaginary part.
     */
    private final double x, y;
    /**
     * cached hashCode.
     */
    private final int hashCode;
    /**
     * internal cached string value.
     */
    private String asString;

    /**
     * Default (zero) constructor.
     */
    public ComplexNumber() {
        this(0, 0);
    }

    /**
     * Constructor with real part.
     *
     * @param real double real part
     */
    public ComplexNumber(final double real) {
        this(real, 0);
    }

    /**
     * Constructor for complex number.
     *
     * @param real double real part.
     * @param imag double imaginary part.
     */
    public ComplexNumber(final double real, final double imag) {
        this.x = real;
        this.y = imag;
        hashCode = Double.valueOf(x).hashCode() ^ Double.valueOf(y).hashCode();
    }

    /**
     * Copy constructor.
     *
     * @param z Complex to copy from.
     */
    public ComplexNumber(final Complex z) {
        this(z.real(), z.imag());
    }

    /**
     * Internal helper to calculate the absolute value.
     *
     * @param x real part
     * @param y imaginary part
     * @return abs(z)
     */
    private static double abs(final double x, final double y) {
        final double ax = Math.abs(x);
        final double ay = Math.abs(y);
        double a, d;
        if (ax == 0.0 && ay == 0.0) {
            return 0.0;
        } else if (ax >= ay) {
            d = y / x;
            a = ax;
        } else {
            d = x / y;
            a = ay;
        }
        return a * Math.sqrt(1.0 + d * d);
    }

    /**
     * Complex addition.
     *
     * @param u Complex argument one
     * @param v Complex argument two
     * @return Complex the result
     */
    public static Complex plus(final Complex u, final Complex v) {
        return u.plus(v);
    }

    /**
     * Complex addition.
     *
     * @param u Complex argument one
     * @param v real argument two
     * @return Complex the result
     */
    public static Complex plus(final Complex u, final double v) {
        return u.plus(v);
    }

    /**
     * Complex addition.
     *
     * @param u real argument one
     * @param v Complex argument two
     * @return Complex the result
     */
    public static Complex plus(final double u, final Complex v) {
        return v.plus(u);
    }

    /**
     * Complex substraction.
     *
     * @param u Complex argument one
     * @param v Complex argument two
     * @return Complex the result
     */
    public static Complex minus(final Complex u, final Complex v) {
        return u.minus(v);
    }

    /**
     * Complex substraction.
     *
     * @param u Complex argument one
     * @param v real argument two
     * @return Complex the result
     */
    public static Complex minus(final Complex u, final double v) {
        return u.minus(v);
    }

    /**
     * Complex substraction.
     *
     * @param u real argument one
     * @param v Complex argument two
     * @return Complex the result
     */
    public static ComplexNumber minus(final double u, final Complex v) {
        return new ComplexNumber(u - v.real(), v.imag());
    }

    /**
     * Complex multiplication.
     *
     * @param u Complex argument one
     * @param v Complex argument two
     * @return Complex the result
     */
    public static Complex multiply(final Complex u, final Complex v) {
        return u.multiply(v);
    }

    /**
     * Multiplication of a real by a complex number.
     *
     * @param u double argument one
     * @param v Complex argument two
     * @return Complex u*v
     */
    public static Complex multiply(final double u, final Complex v) {
        return v.multiply(u);
    }

    /**
     * Multiplication of a complex by a real number.
     *
     * @param u Complex argument two
     * @param v double argument one
     * @return Complex u*v
     */
    public static Complex multiply(final Complex u, final double v) {
        return u.multiply(v);
    }

    /**
     * Complex division.
     *
     * @param u Complex argument one
     * @param v Complex argument two
     * @return Complex the result
     */
    public static Complex div(final Complex u, final Complex v) {
        return u.div(v);
    }

    /**
     * Division of a real by a complex number.
     *
     * @param u double the real number
     * @param v Complex the divisor
     * @return Complex u/v
     */
    public static ComplexNumber div(final double u, final Complex v) {
        return new ComplexNumber(u).div(v);
    }

    /**
     * Division of a complex by a real number.
     *
     * @param u Complex the divident
     * @param v double the divisor
     * @return Complex u/v
     */
    public static Complex div(final Complex u, final double v) {
        return u.div(v);
    }

    /**
     * Calculate the complex square root.
     *
     * @param z the argument
     * @return sqrt(z)
     */
    public static ComplexNumber sqrt(final Complex z) {
        ComplexNumber u;
        final double re = Math.abs(z.real());
        final double im = Math.abs(z.imag());
        double w;

        if (re >= im) {
            final double t = im / re;
            w = Math.sqrt(re) * Math.sqrt((1.0 + hypot(1, t)) / 2);
        } else {
            final double t = re / im;
            w = Math.sqrt(im) * Math.sqrt((t + hypot(1, t)) / 2);
        }

        if (z.real() >= 0.0) {
            final double ai = z.imag();
            u = new ComplexNumber(w, ai / (2.0 * w));
        } else {
            final double ai = z.imag();
            double vi = w;
            if (ai < 0) {
                vi = -w;
            }
            u = new ComplexNumber(ai / (2.0 * vi), vi);
        }
        return u;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Complex#newComplex(double, double)
     */
    @Override
    public Complex newComplex(final double real, final double imag) {
        return new ComplexNumber(real, imag);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Factory#create()
     */
    @Override
    public Complex create() {
        return new ComplexNumber();
    }

    /**
     * Test if this complex is zero.
     *
     * @return boolean indicating true if zero.
     */
    @Override
    public boolean isZero() {
        return isSimilar(x, 0) && isSimilar(y, 0);
    }

    /**
     * Test if this complex is one.
     *
     * @return boolean indicating true if one.
     */
    @Override
    public boolean isOne() {
        return isSimilar(x, 1) && isSimilar(y, 0);
    }

    /**
     * Test if the imaginary part is not zero.
     *
     * @return boolean indicating true if imaginary part not zero.
     */
    @Override
    public boolean isComplex() {
        return !isSimilar(y, 0);
    }

    /**
     * Test if this is a real value, e.g. the imaginary part is zero.
     *
     * @return boolean indicating true if imaginary part not zero.
     */
    @Override
    public boolean isReal() {
        return isSimilar(y, 0);
    }

    /**
     * Equals method suitable for complex.
     *
     * @param o Object to compare to
     * @return boolean indicating if o values equal
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof de.lab4inf.math.Complex) {
            final Complex z = (Complex) o;
            // return x==z.real()&&y==z.imag();
            return isSimilar(x, z.real()) && isSimilar(y, z.imag());
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
        return hashCode;
    }

    /*
     * @Override public String toString()
     */
    @Override
    public String toString() {
        final String fmt = "%6.4g";
        if (null == asString) {
            final StringBuffer buf = new StringBuffer();
            if ((x == y) && (x == 0)) {
                buf.append('0');
            } else {
                if (x != 0) {
                    buf.append(String.format(fmt, x));
                }
                if (y > 0) {
                    if (x != 0)
                        buf.append("+j");
                    else
                        buf.append("j");
                    buf.append(String.format(fmt, y));
                } else if (y < 0) {
                    buf.append("-j");
                    buf.append(String.format(fmt, -y));
                }
            }
            asString = buf.toString().trim();
        }
        return asString;
    }

    /**
     * Return the conjugate complex.
     *
     * @return Complex the Conjugate
     */
    @Override
    public ComplexNumber conj() {
        return new ComplexNumber(x, -y);
    }

    /**
     * Calculate the invers.
     *
     * @return Complex the invers.
     */
    @Override
    public ComplexNumber invers() {
        if (isZero()) {
            throw new IllegalArgumentException("This Complex is zero");
        }
        final double abs2 = abs2();
        return new ComplexNumber(x / abs2, -y / abs2);
    }

    /**
     * the real part.
     *
     * @return double real part.
     */
    @Override
    public double real() {
        return x;
    }

    /**
     * the imaginary part.
     *
     * @return double imaginary part.
     */
    @Override
    public double imag() {
        return y;
    }

    /**
     * the absolute value squared.
     *
     * @return double x*x + y*y
     */
    @Override
    public double abs2() {
        return x * x + y * y;
    }

    /**
     * the absolute value of this complex.
     *
     * @return double sqrt(x*x+y*y)
     */
    @Override
    public Complex abs() {
        return new ComplexNumber(abs(x, y), 0);
    }

    /**
     * True if either the real or the imaginary part is not a number.
     *
     * @return boolean indicating the NaN status
     */
    public boolean isNaN() {
        return Double.isNaN(x) || Double.isNaN(y);
    }

    /**
     * True if either the real or the imaginary part is infinite.
     *
     * @return boolean indicating the infinite status
     */
    public boolean isInfinite() {
        return Double.isInfinite(x) || Double.isInfinite(y);
    }

    /**
     * Complex addition.
     *
     * @param v Complex to add
     * @return Complex the result
     */
    @Override
    @Operand(symbol = "+")
    public ComplexNumber plus(final Complex v) {
        return new ComplexNumber(x + v.real(), y + v.imag());
    }

    /**
     * Complex addition with a real.
     *
     * @param v double to add
     * @return Complex the result
     */
    @Override
    @Operand(symbol = "+")
    public ComplexNumber plus(final double v) {
        return new ComplexNumber(x + v, y);
    }

    /**
     * Complex Subtraction.
     *
     * @param v Complex to subtract
     * @return Complex the result
     */
    @Override
    @Operand(symbol = "-")
    public ComplexNumber minus(final Complex v) {
        return new ComplexNumber(x - v.real(), y - v.imag());
    }

    /**
     * Complex Subtraction with a real.
     *
     * @param v double to subtract
     * @return Complex the result
     */
    @Override
    @Operand(symbol = "-")
    public ComplexNumber minus(final double v) {
        return new ComplexNumber(x - v, y);
    }

    /**
     * Complex multiplication.
     *
     * @param v Complex to multiply
     * @return Complex the result
     */
    @Override
    @Operand(symbol = "*")
    public ComplexNumber multiply(final Complex v) {
        return new ComplexNumber(x * v.real() - y * v.imag(), x * v.imag() + y * v.real());
    }

    /**
     * Conjugate complex multiplication.
     *
     * @param v Complex to multiply
     * @return Complex the result
     */
    @Override
    public ComplexNumber cmultiply(final Complex v) {
        return new ComplexNumber(x * v.real() + y * v.imag(), x * v.imag() - y * v.real());
    }

    /**
     * Multiplication of this complex number by a real.
     *
     * @param scale double multiplier
     * @return Complex the result
     */
    @Override
    @Operand(symbol = "*")
    public ComplexNumber multiply(final double scale) {
        return new ComplexNumber(x * scale, y * scale);
    }

    /**
     * Rescalation of this complex number by a real.
     *
     * @param scale double divisor
     * @return Complex the result
     */
    @Override
    @Operand(symbol = "/")
    public ComplexNumber div(final double scale) {
        if (scale == 0) {
            throw new IllegalArgumentException(DIVIDE_BY_ZERO);
        }
        return new ComplexNumber(x / scale, y / scale);
    }

    /**
     * Complex division.
     *
     * @param v Complex to divide with
     * @return Complex the result
     */
    @Override
    @Operand(symbol = "/")
    public ComplexNumber div(final Complex v) {
        final double abs2 = v.abs2();
        if (abs2 == 0) {
            throw new IllegalArgumentException(DIVIDE_BY_ZERO);
        }
        final ComplexNumber ret = new ComplexNumber((x * v.real() + y * v.imag()) / abs2, (-x * v.imag() + y * v.real()) / abs2);
        return ret;
    }

    /**
     * The angle in radian.
     *
     * @return double the angle
     */
    @Override
    public double rad() {
        double rad = 0;
        if (!isZero())
            if (x != 0) {
                rad = atan2(y, x);
            } else {
                if (y > 0)
                    rad = PI / 2;
                else
                    rad = -PI / 2;
            }
        // if (rad<0) {
        // rad += 2*PI;
        // }
        return rad;
    }

    /**
     * The angle in degree.
     *
     * @return double the angle
     */
    @Override
    public double deg() {
        return rad() * 180.0 / PI;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#doubleValue()
     */
    @Override
    public double doubleValue() {
        if (isComplex()) {
            throw new IllegalAccessError("complex number is not real");
        }
        return x;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#lt(java.lang.Object)
     */
    @Override
    public boolean lt(final Complex that) {
        return abs2() < that.abs2();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#leq(java.lang.Object)
     */
    @Override
    public boolean leq(final Complex that) {
        return abs2() <= that.abs2();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#eq(java.lang.Object)
     */
    @Override
    public boolean eq(final Complex that) {
        return this.equals(that);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#gt(java.lang.Object)
     */
    @Override
    public boolean gt(final Complex that) {
        return abs2() > that.abs2();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Orderable#geq(java.lang.Object)
     */
    @Override
    public boolean geq(final Complex that) {
        return abs2() >= that.abs2();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#getZero()
     */
    @Override
    public Complex getZero() {
        return ZERO;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#getOne()
     */
    @Override
    public Complex getOne() {
        return ONE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#getMinusOne()
     */
    @Override
    public Complex getMinusOne() {
        return MINUS_ONE;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Complex o) {
        int ret = 0;
        double a = abs2();
        double b = o.abs2();
        if (a < b) {
            ret = -1;
        } else if (a > b) {
            ret = 1;
        } else {
            a = rad();
            b = o.rad();
            if (a < b) {
                ret = -1;
            } else if (a > b) {
                ret = 1;
            }
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#sqrt()
     */
    @Override
    public Complex sqrt() {
        return sqrt(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#rnd()
     */
    @Override
    public Complex rnd() {
        return new ComplexNumber(Randomizer.rndBox(-1, 1), Randomizer.rndBox(-1, 1));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#create(double)
     */
    @Override
    public Complex create(final double r) {
        return new ComplexNumber(r);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Numeric#difference(de.lab4inf.math.Numeric)
     */
    @Override
    public double difference(final Complex that) {
        return this.minus(that).abs().doubleValue();
    }
}
 