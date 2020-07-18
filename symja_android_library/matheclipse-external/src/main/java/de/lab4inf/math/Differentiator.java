/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2012,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer Sciences (Lab4Inf).
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

import de.lab4inf.math.gof.Pattern;
import de.lab4inf.math.gof.Visitor;

/**
 * Service interface for a differentiator to be found by the L4MLoader.
 * <pre>
 * Diffrentiator service = L4MLoader.load(Differentiator.class);
 * Function fct = new Sine();
 * double y = service.differentiate(fct, 0.5); // y = cos(0.5)
 * </pre>
 *
 * @author nwulff
 * @version $Id: Differentiator.java,v 1.7 2014/06/26 13:02:38 nwulff Exp $
 * @see de.lab4inf.math.L4MLoader
 * @since 06.01.2012
 */
@Service
@Pattern(name = "Visitor, Decorator")
public interface Differentiator extends Visitor<Function> {
    /**
     * Return the derivative of the given function.
     *
     * @param fct to find the derivative for
     * @return dFct
     */
    Function differential(Function fct);

    /**
     * Differentiate the given function at point x.
     *
     * @param fct to find the derivative for
     * @param x   the argument value
     * @return dFct(x)
     */
    double differentiate(Function fct, double x);

    /**
     * Differentiate the given function at point x,
     * i.e. calculate the gradient.
     *
     * @param fct to find the derivative for
     * @param x   the variable argument vector
     * @return grad_Fct(x)
     */
    double[] differentiate(Function fct, double... x);
}
 