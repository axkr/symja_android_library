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
 * Definition of a vector space with a norm.
 *
 * @param <T> the type of the set elements
 * @author nwulff
 * @version $Id: NormedVectorSpace.java,v 1.2 2011/06/19 12:31:34 nwulff Exp $
 * @since 11.03.2011
 */

public interface NormedVectorSpace<T extends Field<T>> extends VectorSpace<T> {
    /**
     * Norm of this vector.
     *
     * @return ||this||
     */
    T norm();

    /**
     * The by the norm introduced distance between this and that.
     * distance = ||this - that||
     *
     * @param that second argument
     * @return || this - that ||
     */
    T distance(final NormedVectorSpace<T> that);

}
 