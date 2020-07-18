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
 * Algorithm to find the root (or roots) f(x)=0 for
 * a given real valued function f.
 * <p>
 * <br/>
 * You can resolve a RootFinder implementation via the L4MLoader:
 * <pre>
 *
 *   RootFinder w = L4MLoader.load(RootFinder.class);
 *
 * </pre>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: RootFinder.java,v 1.2 2014/11/16 21:47:23 nwulff Exp $
 * @since 15.01.2009
 */
@Service
public interface RootFinder {
    /**
     * Get the actual precision.
     *
     * @return precision
     */
    double getEpsilon();

    /**
     * Set the precision to reach.
     *
     * @param eps the precision
     */
    void setEpsilon(final double eps);

    /**
     * Find a root f(x)=0 for the function f.
     *
     * @param f     the function
     * @param guess initial start value or interval
     * @return x the root of f
     */
    double root(Function f, double... guess);
}
 