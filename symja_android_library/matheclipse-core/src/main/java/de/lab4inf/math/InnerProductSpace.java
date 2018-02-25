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
 * Definition of pre-Hilbert space as normed vector space V with an inner
 * product VxV -&gt; T, which introduces the naturally defined norm
 * ||x|| = sqrt(x*x) on V. It also induces an angle between two vectors
 * within this space and an orthogonal relation.
 * If the space is complete it is a Hilbert space.
 *
 * @param <T> the type of the set elements
 * @author nwulff
 * @version $Id: InnerProductSpace.java,v 1.5 2015/02/11 13:52:17 nwulff Exp $
 * @since 11.03.2011
 */

public interface InnerProductSpace<T extends Field<T>> extends NormedVectorSpace<T> {
    /**
     * Indicate if this vector is orthogonal to that vector.
     *
     * @param that second argument
     * @return true if this is orthogonal to that
     */
    boolean isOrthogonal(final InnerProductSpace<T> that);

    /**
     * Vector product of this times that.
     *
     * @param that second argument
     * @return this*that
     */
    @Operand(symbol = "*")
    T product(final InnerProductSpace<T> that);

    /**
     * The angle phi between this and that.
     * <p>
     * phi = arccos (|&lt;this,that&gt;|/(||this||*||that||))
     *
     * @param that the vector to compare to
     * @return angle(this, that)
     */
    double angle(final InnerProductSpace<T> that);
}
 