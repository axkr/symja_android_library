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

import de.lab4inf.math.gof.Visitor;

/**
 * Service interface for an integrator to be found by the L4MLoader.
 * <p>
 * <pre>
 * Integrator service = L4MLoader.load(Integrator.class);
 * Function fct = new Sine();
 * double y = service.integrate(fct, 0.2, 0.4); // y = cos(0.2) - cos(0.4)
 * </pre>
 *
 * @author nwulff
 * @version $Id: Integrator.java,v 1.5 2014/06/26 11:25:35 nwulff Exp $
 * @see de.lab4inf.math.L4MLoader
 * @since 07.01.2012
 */
@Service
public interface Integrator extends Visitor<Function> {
    /**
     * Return the antiderivative/primitive of the given function.
     * This might be a numerical wrapper unique up to the integration
     * constant c.
     *
     * @param fct to find the antiderivative for
     * @return F(x)+c
     */
    Function antiderivative(Function fct);

    /**
     * Integrate the given function from a to b.
     *
     * @param fct to calculate the integral for
     * @param a   left integration border
     * @param b   right integration border
     * @return F(b) - F(a)
     */
    double integrate(Function fct, double a, double b);
}
 