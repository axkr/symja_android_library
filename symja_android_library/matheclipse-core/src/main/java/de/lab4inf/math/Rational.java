/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2013,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
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
package de.lab4inf.math;

import java.math.BigInteger;

/**
 * Basic interface for a Rational as fraction of two integers.
 * <p>
 * <br/>
 * You can resolve a Real implementation via the L4MLoader:
 * <pre>
 *
 *   Real w = L4MLoader.load(Real.class);
 *
 * </pre>
 *
 * @author nwulff
 * @version $Id: Rational.java,v 1.6 2014/11/16 21:47:23 nwulff Exp $
 * @see de.lab4inf.math.L4MLoader
 * @since 16.02.2013
 */
@Service
public interface Rational extends Numeric<Rational> {
    /**
     * Factory method, mimic a constructor for a new rational number
     * without exposing the implementing class to a client.
     *
     * @param numerator of the rational
     * @param divider   of the rational
     * @return new Rational numerator/divider
     */
    Rational newRational(long numerator, long divider);

    /**
     * Factory method, mimic a constructor for a new rational number
     * with an divider of one.
     *
     * @param numerator of the rational
     * @return new Rational numerator/1
     */
    Rational newRational(long numerator);

    /**
     * Addition of this and x.
     *
     * @param x scalar to shift with
     * @return this + x
     */
    @Operand(symbol = "+")
    Rational plus(long x);

    /**
     * Subtraction of this and x.
     *
     * @param x scalar to shift with
     * @return this + x
     */
    @Operand(symbol = "-")
    Rational minus(long x);

    /**
     * Multiply this by x.
     *
     * @param x scalar to scale with
     * @return this * x
     */
    @Operand(symbol = "*")
    Rational multiply(long x);

    /**
     * Divide this by x.
     *
     * @param x scalar to divide with
     * @return this / x
     */
    @Operand(symbol = "/")
    Rational div(long x);

    @Override
    /**
     * Representation as a double
     * @return double value
     */
    double doubleValue();

    /**
     * Get the numerator of this rational.
     *
     * @return the numerator
     */
    BigInteger numerator();

    /**
     * Get the divider of this rational.
     *
     * @return the divider
     */
    BigInteger divider();

    @Override
    /**
     * Get the absolute value of of this rational.
     * @return the abs(this)
     */
    Rational abs();

    /**
     * Get the absolute value of of this rational squared.
     *
     * @return the abs(this)**2
     */
    Rational abs2();

    /**
     * Get the cosine of this rational.
     *
     * @return the cos(this)
     */
    Rational cos();

    /**
     * Get the exponentional of this rational.
     *
     * @return the exp(this)
     */
    Rational exp();

    /**
     * Get the sine of this rational.
     *
     * @return the sin(this)
     */
    Rational sin();

    @Override
    /**
     * Get the square root of this rational.
     * @return the sqrt(this)
     */
    Rational sqrt();

    /**
     * Get the tangent of this rational.
     *
     * @return the tan(this)
     */
    Rational tan();
}
 