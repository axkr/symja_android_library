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

/**
 * A complex function with a known complex primitive.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: CIntegrable.java,v 1.2 2011/03/10 19:44:01 nwulff Exp $
 * @since 20.12.2008
 */
public interface CIntegrable extends Integrable, CFunction {
    /**
     * The complex primitive of this function.
     *
     * @return CFunction the primitive
     */
    CFunction getCAntiderivative();

}
 