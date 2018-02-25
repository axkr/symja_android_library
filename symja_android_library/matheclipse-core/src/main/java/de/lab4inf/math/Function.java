/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer sciences (Lab4Inf).
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

import de.lab4inf.math.gof.Decoratable;
import de.lab4inf.math.gof.Pattern;
import de.lab4inf.math.gof.Visitable;

/**
 * General interface for a real valued function mapping a N-dimensional
 * input argument to a real value.
 * E.g. functions can be coded as
 * <pre>
 *   f(x), f(x,y), f(x,y,z), f(x1,...,xn) or f(double[] x)
 * </pre>
 * allowing multidimensional input vectors with the same generic interface.
 * It depends on the implementation if and how to handle multidimensional
 * input vectors.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Function.java,v 1.7 2014/06/26 13:02:38 nwulff Exp $
 * @since 05.11.2004
 */
@Pattern(name = "Visitor,Decorator", role = "Visitor:subject, Decorator:decorated")
public interface Function extends Visitable<Function>, Decoratable<Function> {
    /**
     * Evaluate the function at argument x giving output y=f(x).
     *
     * @param x input value (can be a multidimensional parameter list)
     * @return output double value y=f(x)
     */
    double f(double... x);
}
 