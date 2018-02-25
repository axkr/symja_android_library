/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2014,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math.ode;

import static de.lab4inf.math.ode.FirstOrderSystemSolver.Solver.RungeKuttaFehlberg;

/**
 * SecondOrder differential solver wrapping the Runge-Kutta Fehlberg method.
 *
 * @author nwulff
 * @version $Id: SecondOrderRKFSolver.java,v 1.1 2014/05/12 14:38:12 nwulff Exp $
 * @since 12.05.2014
 */
public class SecondOrderRKFSolver extends SecondOrderSolver {
    /**
     * Sole constructor.
     */
    public SecondOrderRKFSolver() {
        super(RungeKuttaFehlberg);
    }
}
 