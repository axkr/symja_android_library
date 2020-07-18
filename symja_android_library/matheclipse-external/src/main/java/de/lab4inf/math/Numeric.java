/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2013,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer Sciences (Lab4Inf).
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

/**
 * A numeric is a common representation of numbers like java.lang.Number but
 * in addition offers the basic operations (plus, multiply, divide, etc.) of a
 * mathematical field and can be used within generic algorithms.
 *
 * @param <T> the type
 * @author nwulff
 * @version $Id: Numeric.java,v 1.6 2015/02/11 13:52:17 nwulff Exp $
 * @since 20.06.2013
 */
public interface Numeric<T extends Numeric<T>> extends Field<T>, Orderable<T>, Factory<T> {
    /**
     * Create a random numeric value.
     *
     * @return a random
     */
    T rnd();

    /***
     * Get the neutral additive element ZERO for this Numeric type.
     * @return additive ZERO
     */
    T getZero();

    /***
     * Get the neutral multiplicative element ONE for this Numeric type.
     * @return multiplicative ONE
     */
    T getOne();

    /***
     * Get the additive inverse of ONE for this this Numeric type.
     * @return minus ONE
     */
    T getMinusOne();

    /**
     * Multiply this by x.
     *
     * @param x the multiplicand
     * @return this * x
     */
    @Operand(symbol = "*")
    T multiply(double x);

    /**
     * Factory method to create a scaled T object.
     *
     * @param x scale for ONE
     * @return ONE*x
     */
    T create(double x);

    /**
     * Get the absolute value of this Numeric.
     *
     * @return absolute value
     */
    T abs();

    /**
     * Get the square root of this Numeric.
     *
     * @return the square root
     */
    T sqrt();

    /**
     * Get the corresponding double value of this Numeric as
     * accurate as possible.
     *
     * @return double value
     */
    double doubleValue();

    /**
     * difference between this and that
     *
     * @param that the value to check against
     * @return ||this-that||
     */
    double difference(T that);
}
 