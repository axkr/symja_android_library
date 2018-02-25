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

package de.lab4inf.math.ode;

import de.lab4inf.math.L4MObject;

/**
 * Abstract base class for OdeSolvers.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: AbstractOdeSolver.java,v 1.7 2014/05/12 14:38:12 nwulff Exp $
 * @since 21.12.2008
 */
public class AbstractOdeSolver extends L4MObject implements OdeSolver {
    /**
     * the minimal allowed accuracy.
     */
    protected static final double EPS_MIN = 1.E-10;
    /**
     * the maximal allowed interval splits.
     */
    protected static final int MAX_SPLITS = 22;
    /**
     * the maximal allowed step width.
     */
    protected static final double H_MAX = 0.25;
    /**
     * the minimal allowed step width.
     */
    protected static final double H_MIN = H_MAX / (1 << (MAX_SPLITS));

}
 