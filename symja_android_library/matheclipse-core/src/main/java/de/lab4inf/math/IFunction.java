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
 * Interface for an interval based function.
 *
 * @author nwulff
 * @version $Id: IFunction.java,v 1.1 2011/02/09 22:28:28 nwulff Exp $
 * @since 07.02.2011
 */

public interface IFunction extends Function {
    /**
     * Evaluate the function at the interval v giving the output interval y=f(v).
     *
     * @param v interval based input value (can be a multidimensional parameter list)
     * @return output interval based value y=f(v)
     */
    Interval f(Interval... v);
}
 