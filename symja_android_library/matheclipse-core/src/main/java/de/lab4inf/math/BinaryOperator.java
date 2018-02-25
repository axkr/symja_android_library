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

/**
 * BinaryOperator for two instances of type T.
 *
 * @param <T> type of the arguments
 * @author nwulff
 * @version $Id: BinaryOperator.java,v 1.1 2013/06/25 23:28:30 nwulff Exp $
 * @since 26.06.2013
 */
public interface BinaryOperator<T> {
    /**
     * Calculate z = x op y
     *
     * @param x first argument
     * @param y second argument
     * @return x op y
     */
    T op(final T x, final T y);
}
 