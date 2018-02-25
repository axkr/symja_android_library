/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2011,  Prof. Dr. Nikolaus Wulff
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
 * General interface for function defined over the natural numbers.
 * Mapping a N-dimensional input argument to a natural number.
 * E.g. functions can be coded as
 * <pre>
 *   f(x), f(x,y), f(x,y,z), f(x1,...,xn) or f(long[] x)
 * </pre>
 * allowing multidimensional input vectors with the same generic interface.
 * It depends on the implementation if and how to handle multidimensional
 * input vectors.
 *
 * @author nwulff
 * @version $Id: NFunction.java,v 1.2 2011/10/16 11:22:48 nwulff Exp $
 * @since 15.10.2011
 */
public interface NFunction {
    /**
     * Evaluate the function at argument x giving output y=f(x).
     *
     * @param x input value (can be a multidimensional parameter list)
     * @return output long value y=f(x)
     */
    long f(long... x);
}
 