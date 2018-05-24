/*
 * Copyright 2003-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This code is modified for the MathEclipse project
 */

package org.matheclipse.core.expression;

import org.matheclipse.parser.client.math.MathUtils;

/**
 * Static implementations of common
 * {@link org.matheclipse.parser.client.math.Complex}-valued functions.  Included
 * are trigonometric, exponential, log, power and square root functions.
 *<p>
 * Reference:
 * <ul>
 * <li><a href="http://myweb.lmu.edu/dmsmith/ZMLIB.pdf">
 * Multiple Precision DoubleComplexImpl Arithmetic and Functions</a></li>
 * </ul>
 * See individual method javadocs for the computational formulas used.
 * In general, NaN values in either real or imaginary parts of input arguments
 * result in {@link ComplexNum#NaN} returned.  Otherwise, infinite or NaN values
 * are returned as they arise in computing the real functions specified in the
 * computational formulas.  Null arguments result in NullPointerExceptions.
 *
 * @version $Revision: 1.7 $ $Date: 2008/09/07 10:01:54 $
 */
public class ComplexUtils {

    /**
     * Default constructor.
     */
    private ComplexUtils() {
        super();
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/InverseCosine.html" TARGET="_top">
     * inverse cosine</a> for the given complex argument.
     * <p>
     * Implements the formula: <pre>
     * <code> acos(z) = -i (log(z + i (sqrt(1 - z<sup>2</sup>))))</code></pre>
     * <p>
     * Returns {@link ComplexNum#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code> or infinite.
     *
     * @param z the value whose inverse cosine is to be returned
     * @return the inverse cosine of <code>z</code>
     * @throws NullPointerException if <code>z</code> is null
     */
    public static ComplexNum acos(final ComplexNum z) {
        if (z.isNaN()) {
            return ComplexNum.NaN;
        }

        return ComplexNum.I.negate().multiply(log(z.add(
            ComplexNum.I.multiply(sqrt1z(z)))));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/InverseSine.html" TARGET="_top">
     * inverse sine</a> for the given complex argument.
     * <p>
     * Implements the formula: <pre>
     * <code> asin(z) = -i (log(sqrt(1 - z<sup>2</sup>) + iz)) </code></pre>
     * <p>
     * Returns {@link ComplexNum#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code> or infinite.
     *
     * @param z the value whose inverse sine is to be returned.
     * @return the inverse sine of <code>z</code>.
     * @throws NullPointerException if <code>z</code> is null
     */
    public static ComplexNum asin(final ComplexNum z) {
        if (z.isNaN()) {
            return ComplexNum.NaN;
        }

        return ComplexNum.I.negate().multiply(log(sqrt1z(z).add(
            ComplexNum.I.multiply(z))));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/InverseTangent.html" TARGET="_top">
     * inverse tangent</a> for the given complex argument.
     * <p>
     * Implements the formula: <pre>
     * <code> atan(z) = (i/2) log((i + z)/(i - z)) </code></pre>
     * <p>
     * Returns {@link ComplexNum#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code> or infinite.
     *
     * @param z the value whose inverse tangent is to be returned
     * @return the inverse tangent of <code>z</code>
     * @throws NullPointerException if <code>z</code> is null
     */
    public static ComplexNum atan(final ComplexNum z) {
        if (z.isNaN()) {
            return ComplexNum.NaN;
        }

        return ComplexNum.I.multiply(
            log(ComplexNum.I.add(z).divide(ComplexNum.I.subtract(z))))
            .divide(ComplexNum.valueOf(2.0, 0.0));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/Cosine.html" TARGET="_top">
     * cosine</a>
     * for the given complex argument.
     * <p>
     * Implements the formula: <pre>
     * <code> cos(a + bi) = cos(a)cosh(b) - sin(a)sinh(b)i</code></pre>
     * where the (real) functions on the right-hand side are
     * {@link java.lang.Math#sin}, {@link java.lang.Math#cos},
     * {@link MathUtils#cosh} and {@link MathUtils#sinh}.
     * <p>
     * Returns {@link ComplexNum#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * cos(1 &plusmn; INFINITY i) = 1 &#x2213; INFINITY i
     * cos(&plusmn;INFINITY + i) = NaN + NaN i
     * cos(&plusmn;INFINITY &plusmn; INFINITY i) = NaN + NaN i</code></pre>
     *
     * @param z the value whose cosine is to be returned
     * @return the cosine of <code>z</code>
     * @throws NullPointerException if <code>z</code> is null
     */
    public static ComplexNum cos(final ComplexNum z) {
        if (z.isNaN()) {
            return ComplexNum.NaN;
        }

        final double a = z.reDoubleValue();
        final double b = z.imDoubleValue();

        return ComplexNum.valueOf(Math.cos(a) * MathUtils.cosh(b),
            -Math.sin(a) * MathUtils.sinh(b));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/HyperbolicCosine.html" TARGET="_top">
     * hyperbolic cosine</a> for the given complex argument.
     * <p>
     * Implements the formula: <pre>
     * <code> cosh(a + bi) = cosh(a)cos(b) + sinh(a)sin(b)i</code></pre>
     * where the (real) functions on the right-hand side are
     * {@link java.lang.Math#sin}, {@link java.lang.Math#cos},
     * {@link MathUtils#cosh} and {@link MathUtils#sinh}.
     * <p>
     * Returns {@link ComplexNum#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * cosh(1 &plusmn; INFINITY i) = NaN + NaN i
     * cosh(&plusmn;INFINITY + i) = INFINITY &plusmn; INFINITY i
     * cosh(&plusmn;INFINITY &plusmn; INFINITY i) = NaN + NaN i</code></pre>
     * <p>
     * Throws <code>NullPointerException</code> if z is null.
     *
     * @param z the value whose hyperbolic cosine is to be returned.
     * @return the hyperbolic cosine of <code>z</code>.
     */
    public static ComplexNum cosh(final ComplexNum z) {
        if (z.isNaN()) {
            return ComplexNum.NaN;
        }

        final double a = z.reDoubleValue();
        final double b = z.imDoubleValue();

        return ComplexNum.valueOf(MathUtils.cosh(a) * Math.cos(b),
            MathUtils.sinh(a) * Math.sin(b));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/ExponentialFunction.html" TARGET="_top">
     * exponential function</a> for the given complex argument.
     * <p>
     * Implements the formula: <pre>
     * <code> exp(a + bi) = exp(a)cos(b) + exp(a)sin(b)i</code></pre>
     * where the (real) functions on the right-hand side are
     * {@link java.lang.Math#exp}, {@link java.lang.Math#cos}, and
     * {@link java.lang.Math#sin}.
     * <p>
     * Returns {@link ComplexNum#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * exp(1 &plusmn; INFINITY i) = NaN + NaN i
     * exp(INFINITY + i) = INFINITY + INFINITY i
     * exp(-INFINITY + i) = 0 + 0i
     * exp(&plusmn;INFINITY &plusmn; INFINITY i) = NaN + NaN i</code></pre>
     * <p>
     * Throws <code>NullPointerException</code> if z is null.
     *
     * @param z the value
     * @return <i>e</i><sup><code>z</code></sup>
     */
    public static ComplexNum exp(final ComplexNum z) {
        if (z.isNaN()) {
            return ComplexNum.NaN;
        }

        final double b = z.imDoubleValue();
        final double expA = Math.exp(z.reDoubleValue());
        return ComplexNum.valueOf(expA *  Math.cos(b), expA * Math.sin(b));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/NaturalLogarithm.html" TARGET="_top">
     * natural logarithm</a> for the given complex argument.
     * <p>
     * Implements the formula: <pre>
     * <code> log(a + bi) = ln(|a + bi|) + arg(a + bi)i</code></pre>
     * where ln on the right hand side is {@link java.lang.Math#log},
     * <code>|a + bi|</code> is the modulus, {@link ComplexNum#abs},  and
     * <code>arg(a + bi) = {@link java.lang.Math#atan2}(b, a)</code>
     * <p>
     * Returns {@link ComplexNum#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.
     * <p>
     * Infinite (or critical) values in real or imaginary parts of the input may
     * result in infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * log(1 &plusmn; INFINITY i) = INFINITY &plusmn; (&pi;/2)i
     * log(INFINITY + i) = INFINITY + 0i
     * log(-INFINITY + i) = INFINITY + &pi;i
     * log(INFINITY &plusmn; INFINITY i) = INFINITY &plusmn; (&pi;/4)i
     * log(-INFINITY &plusmn; INFINITY i) = INFINITY &plusmn; (3&pi;/4)i
     * log(0 + 0i) = -INFINITY + 0i
     * </code></pre>
     * Throws <code>NullPointerException</code> if z is null.
     *
     * @param z the value.
     * @return ln <code>z</code>.
     */
    public static ComplexNum log(final ComplexNum z) {
        if (z.isNaN()) {
            return ComplexNum.NaN;
        }

        return ComplexNum.valueOf(Math.log(z.dabs()),
            Math.atan2(z.imDoubleValue(), z.reDoubleValue()));
    }

    /**
     * Creates a complex number from the given polar representation.
     * <p>
     * The value returned is <code>r&middot;e<sup>i&middot;theta</sup></code>,
     * computed as <code>r&middot;cos(theta) + r&middot;sin(theta)i</code>
     * <p>
     * If either <code>r</code> or <code>theta</code> is NaN, or
     * <code>theta</code> is infinite, {@link ComplexNum#NaN} is returned.
     * <p>
     * If <code>r</code> is infinite and <code>theta</code> is finite,
     * infinite or NaN values may be returned in parts of the result, following
     * the rules for double arithmetic.<pre>
     * Examples:
     * <code>
     * polar2Complex(INFINITY, &pi;/4) = INFINITY + INFINITY i
     * polar2Complex(INFINITY, 0) = INFINITY + NaN i
     * polar2Complex(INFINITY, -&pi;/4) = INFINITY - INFINITY i
     * polar2Complex(INFINITY, 5&pi;/4) = -INFINITY - INFINITY i </code></pre>
     *
     * @param r the modulus of the complex number to create
     * @param theta  the argument of the complex number to create
     * @return <code>r&middot;e<sup>i&middot;theta</sup></code>
     * @throws IllegalArgumentException  if r is negative
     * @since 1.1
     */
    public static ComplexNum polar2Complex(final double r, final double theta) {
        if (r < 0) {
            throw new IllegalArgumentException
                ("DoubleComplexImpl modulus must not be negative");
        }
        return ComplexNum.valueOf(r * Math.cos(theta), r * Math.sin(theta));
    }

    /**
     * Returns of value of <code>y</code> raised to the power of <code>x</code>.
     * <p>
     * Implements the formula: <pre>
     * <code> y<sup>x</sup> = exp(x&middot;log(y))</code></pre>
     * where <code>exp</code> and <code>log</code> are {@link #exp} and
     * {@link #log}, respectively.
     * <p>
     * Returns {@link ComplexNum#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code> or infinite, or if <code>y</code>
     * equals {@link ComplexNum#ZERO}.
     *
     * @param y the base.
     * @param x the exponent.
     * @return <code>y</code><sup><code>x</code></sup>
     * @throws NullPointerException if either x or y is null
     */
    public static ComplexNum pow(final ComplexNum y, final ComplexNum x) {
        return exp(x.multiply(log(y)));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/Sine.html" TARGET="_top">
     * sine</a>
     * for the given complex argument.
     * <p>
      * Implements the formula: <pre>
     * <code> sin(a + bi) = sin(a)cosh(b) - cos(a)sinh(b)i</code></pre>
     * where the (real) functions on the right-hand side are
     * {@link java.lang.Math#sin}, {@link java.lang.Math#cos},
     * {@link MathUtils#cosh} and {@link MathUtils#sinh}.
     * <p>
     * Returns {@link ComplexNum#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * sin(1 &plusmn; INFINITY i) = 1 &plusmn; INFINITY i
     * sin(&plusmn;INFINITY + i) = NaN + NaN i
     * sin(&plusmn;INFINITY &plusmn; INFINITY i) = NaN + NaN i</code></pre>
     *
     * Throws <code>NullPointerException</code> if z is null.
     *
     * @param z the value whose sine is to be returned.
     * @return the sine of <code>z</code>.
     */
    public static ComplexNum sin(final ComplexNum z) {
        if (z.isNaN()) {
            return ComplexNum.NaN;
        }

        final double a = z.reDoubleValue();
        final double b = z.imDoubleValue();

        return ComplexNum.valueOf(Math.sin(a) * MathUtils.cosh(b),
            Math.cos(a) * MathUtils.sinh(b));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/HyperbolicSine.html" TARGET="_top">
     * hyperbolic sine</a> for the given complex argument.
     * <p>
     * Implements the formula: <pre>
     * <code> sinh(a + bi) = sinh(a)cos(b)) + cosh(a)sin(b)i</code></pre>
     * where the (real) functions on the right-hand side are
     * {@link java.lang.Math#sin}, {@link java.lang.Math#cos},
     * {@link MathUtils#cosh} and {@link MathUtils#sinh}.
     * <p>
     * Returns {@link ComplexNum#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * sinh(1 &plusmn; INFINITY i) = NaN + NaN i
     * sinh(&plusmn;INFINITY + i) = &plusmn; INFINITY + INFINITY i
     * sinh(&plusmn;INFINITY &plusmn; INFINITY i) = NaN + NaN i</code></pre
     *
     * @param z the value whose hyperbolic sine is to be returned
     * @return the hyperbolic sine of <code>z</code>
     * @throws NullPointerException if <code>z</code> is null
     */
    public static ComplexNum sinh(final ComplexNum z) {
        if (z.isNaN()) {
            return ComplexNum.NaN;
        }

        final double a = z.reDoubleValue();
        final double b = z.imDoubleValue();

        return ComplexNum.valueOf(MathUtils.sinh(a) * Math.cos(b),
            MathUtils.cosh(a) * Math.sin(b));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/SquareRoot.html" TARGET="_top">
     * square root</a> for the given complex argument.
     * <p>
     * Implements the following algorithm to compute <code>sqrt(a + bi)</code>:
     * <ol><li>Let <code>t = sqrt((|a| + |a + bi|) / 2)</code></li>
     * <li><pre>if <code> a &#8805; 0</code> return <code>t + (b/2t)i</code>
     *  else return <code>|b|/2t + sign(b)t i </code></pre></li>
     * </ol>
     * where <ul>
     * <li><code>|a| = {@link Math#abs}(a)</code></li>
     * <li><code>|a + bi| = {@link ComplexNum#abs}(a + bi) </code></li>
     * <li><code>sign(b) =  {@link MathUtils#indicator}(b) </code>
     * </ul>
     * <p>
     * Returns {@link ComplexNum#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * sqrt(1 &plusmn; INFINITY i) = INFINITY + NaN i
     * sqrt(INFINITY + i) = INFINITY + 0i
     * sqrt(-INFINITY + i) = 0 + INFINITY i
     * sqrt(INFINITY &plusmn; INFINITY i) = INFINITY + NaN i
     * sqrt(-INFINITY &plusmn; INFINITY i) = NaN &plusmn; INFINITY i
     * </code></pre>
     *
     * @param z the value whose square root is to be returned
     * @return the square root of <code>z</code>
     * @throws NullPointerException if <code>z</code> is null
     */
    public static ComplexNum sqrt(final ComplexNum z) {
        if (z.isNaN()) {
            return ComplexNum.NaN;
        }

        final double a = z.reDoubleValue();
        final double b = z.imDoubleValue();

        final double t = Math.sqrt((Math.abs(a) + z.dabs()) / 2.0);
        if (a >= 0.0) {
            return ComplexNum.valueOf(t, b / (2.0 * t));
        } else {
            return ComplexNum.valueOf(Math.abs(b) / (2.0 * t),
                MathUtils.indicator(b) * t);
        }
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/SquareRoot.html" TARGET="_top">
     * square root</a> of 1 - <code>z</code><sup>2</sup> for the given complex
     * argument.
     * <p>
     * Computes the result directly as
     * <code>sqrt(DoubleComplexImpl.ONE.subtract(z.multiply(z)))</code>.
     * <p>
     * Returns {@link ComplexNum#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.
     *
     * @param z the value
     * @return the square root of 1 - <code>z</code><sup>2</sup>
     * @throws NullPointerException if <code>z</code> is null
     */
    public static ComplexNum sqrt1z(final ComplexNum z) {
        return sqrt(ComplexNum.ONE.subtract(z.multiply(z)));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/Tangent.html" TARGET="_top">
     * tangent</a> for the given complex argument.
     * <p>
     * Implements the formula: <pre>
     * <code>tan(a + bi) = sin(2a)/(cos(2a)+cosh(2b)) + [sinh(2b)/(cos(2a)+cosh(2b))]i</code></pre>
     * where the (real) functions on the right-hand side are
     * {@link java.lang.Math#sin}, {@link java.lang.Math#cos},
     * {@link MathUtils#cosh} and {@link MathUtils#sinh}.
     * <p>
     * Returns {@link ComplexNum#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.
     * <p>
     * Infinite (or critical) values in real or imaginary parts of the input may
     * result in infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * tan(1 &plusmn; INFINITY i) = 0 + NaN i
     * tan(&plusmn;INFINITY + i) = NaN + NaN i
     * tan(&plusmn;INFINITY &plusmn; INFINITY i) = NaN + NaN i
     * tan(&plusmn;&pi/2 + 0 i) = &plusmn;INFINITY + NaN i</code></pre>
     *
     * @param z the value whose tangent is to be returned
     * @return the tangent of <code>z</code>
     * @throws NullPointerException if <code>z</code> is null
     */
    public static ComplexNum tan(final ComplexNum z) {
        if (z.isNaN()) {
            return ComplexNum.NaN;
        }

        final double a2 = 2.0 * z.reDoubleValue();
        final double b2 = 2.0 * z.imDoubleValue();
        final double d = Math.cos(a2) + MathUtils.cosh(b2);

        return ComplexNum.valueOf(Math.sin(a2) / d, MathUtils.sinh(b2) / d);
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/HyperbolicTangent.html" TARGET="_top">
     * hyperbolic tangent</a> for the given complex argument.
    * <p>
     * Implements the formula: <pre>
     * <code>tan(a + bi) = sinh(2a)/(cosh(2a)+cos(2b)) + [sin(2b)/(cosh(2a)+cos(2b))]i</code></pre>
     * where the (real) functions on the right-hand side are
     * {@link java.lang.Math#sin}, {@link java.lang.Math#cos},
     * {@link MathUtils#cosh} and {@link MathUtils#sinh}.
     * <p>
     * Returns {@link ComplexNum#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * tanh(1 &plusmn; INFINITY i) = NaN + NaN i
     * tanh(&plusmn;INFINITY + i) = NaN + 0 i
     * tanh(&plusmn;INFINITY &plusmn; INFINITY i) = NaN + NaN i
     * tanh(0 + (&pi/2)i) = NaN + INFINITY i</code></pre>
     *
     * @param z the value whose hyperbolic tangent is to be returned
     * @return the hyperbolic tangent of <code>z</code>
     * @throws NullPointerException if <code>z</code> is null
     */
    public static ComplexNum tanh(final ComplexNum z) {
        if (z.isNaN()) {
            return ComplexNum.NaN;
        }

        final double a2 = 2.0 * z.reDoubleValue();
        final double b2 = 2.0 * z.imDoubleValue();
        final double d = MathUtils.cosh(a2) + Math.cos(b2);

        return ComplexNum.valueOf(MathUtils.sinh(a2) / d, Math.sin(b2) / d);
    }
}
