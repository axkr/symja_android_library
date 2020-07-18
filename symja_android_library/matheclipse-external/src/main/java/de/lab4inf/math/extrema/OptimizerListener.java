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
package de.lab4inf.math.extrema;

/**
 * Listener interface for a MVC alike observer pattern.
 * The optimizer informs all registered listeners when
 * an iteration or the optimization has finished.
 *
 * @author nwulff
 * @version $Id: OptimizerListener.java,v 1.3 2010/02/25 15:31:54 nwulff Exp $
 * @since 19.06.2007
 */
public interface OptimizerListener {
    /**
     * To be called by the optimizer when
     * an iteration has finished.
     *
     * @param iteration   the actual iteration number
     * @param actualguess the actual iteration guess
     */
    void iterationFinished(int iteration, double... actualguess);

    /**
     * To be called by the optimizer when
     * the optimization has finished.
     *
     * @param iteration int the final iteration number
     * @param bestguess the final best guess
     */
    void optimizationFinished(int iteration, double... bestguess);
}
 