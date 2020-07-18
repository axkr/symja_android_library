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
 * Vector space over a field of a set T defined as an abelian group under
 * addition with a scalar product.
 *
 * @param <T> the type of the set elements
 * @author nwulff
 * @version $Id: VectorSpace.java,v 1.3 2012/01/09 14:49:51 nwulff Exp $
 * @since 11.03.2011
 */
public interface VectorSpace<T extends Field<T>> extends Group<VectorSpace<T>> {
    /**
     * Get the elements of this vector.
     *
     * @return T array
     */
    T[] getElements();

    /**
     * Multipliy the vector elements by a scalar.
     *
     * @param scalar of type T
     * @return this*scalar
     */
    @Operand(symbol = "*")
    VectorSpace<T> multiply(final T scalar);
}
 