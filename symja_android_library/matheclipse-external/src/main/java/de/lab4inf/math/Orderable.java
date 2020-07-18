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
 * Interface of things which can be compared.
 *
 * @param <T> the type of the orderables.
 * @author nwulff
 * @version $Id: Orderable.java,v 1.3 2014/11/16 21:47:23 nwulff Exp $
 * @since 20.06.2013
 */
public interface Orderable<T> extends Comparable<T> {
    /**
     * Compare this to that
     *
     * @param that to compare to
     * @return this &lt;  that
     */
    @Operand(symbol = "<")
    boolean lt(T that);

    /**
     * Compare this to that
     *
     * @param that to compare to
     * @return this &le;  that
     */
    @Operand(symbol = "<=")
    boolean leq(T that);

    /**
     * Compare this to that
     *
     * @param that to compare to
     * @return this &equiv; that
     */
    @Operand(symbol = "==")
    boolean eq(T that);

    /**
     * Compare this to that
     *
     * @param that to compare to
     * @return this &gt; that
     */
    @Operand(symbol = ">")
    boolean gt(T that);

    /**
     * Compare this to that
     *
     * @param that to compare to
     * @return this &ge; that
     */
    @Operand(symbol = ">=")
    boolean geq(T that);
}
 