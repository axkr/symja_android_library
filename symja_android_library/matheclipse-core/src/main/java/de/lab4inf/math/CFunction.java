/*
 * Project: Lab4Math
 *
 * Copyright (c) 2006-2010,  Prof. Dr. Nikolaus Wulff
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
 * Interface of a complex valued function extending a real valued function to
 * the complex plane.
 *
 * @author nwulff
 * @version $Id: CFunction.java,v 1.1 2010/11/14 15:32:56 nwulff Exp $
 * @since 14.11.2010
 */

public interface CFunction extends Function {
    /**
     * Evaluate the function at argument z giving output y=f(z).
     *
     * @param z complex input value (can be a multidimensional parameter list)
     * @return output complex value y=f(z)
     */
    Complex f(Complex... z);
}
 