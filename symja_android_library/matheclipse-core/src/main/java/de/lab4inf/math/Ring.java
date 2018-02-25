/*
 * Project: Lab4Math
 *
 * Copyright (c) 2006-2011,  Prof. Dr. Nikolaus Wulff
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

/**
 * Interface of the algebraic structure of a mathematical (multiplicative) ring
 * over a set T.
 *
 * @param <T> set of this ring
 * @author nwulff
 * @version $Id: Ring.java,v 1.3 2012/01/09 15:11:24 nwulff Exp $
 * @since 09.03.2011
 */
public interface Ring<T> extends Group<T> {
    /**
     * Indicate if this is the neutral ONE element of the multiplication.
     *
     * @return ONE indicator flag
     */
    boolean isOne();

    /**
     * Subtract that from this.
     *
     * @param that the subtrahend
     * @return this minus that
     */
    @Operand(symbol = "-")
    T minus(final T that);

    /**
     * Multiply this by that.
     *
     * @param that the multiplicand
     * @return this * that
     */
    @Operand(symbol = "*")
    T multiply(final T that);
}
 