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
 * Interface of the algebraic structure of a mathematical (additive) group
 * over a set T.
 *
 * @param <T> set of this group
 * @author nwulff
 * @version $Id: Group.java,v 1.2 2012/01/09 14:49:51 nwulff Exp $
 * @since 09.03.2011
 */
public interface Group<T> {
    /**
     * Indicate if this is the neutral ZERO element of the addition.
     *
     * @return ZERO indicator flag
     */
    boolean isZero();

    /**
     * Addition of this plus that
     *
     * @param that the summand
     * @return this + that
     */
    @Operand(symbol = "+")
    T plus(final T that);

}
 