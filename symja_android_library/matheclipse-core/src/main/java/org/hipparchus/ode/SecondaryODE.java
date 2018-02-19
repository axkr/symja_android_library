/*
 * Licensed to the Hipparchus project under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Hipparchus project licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hipparchus.ode;

import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;

/**
 * This interface allows users to add secondary differential equations to a primary
 * set of differential equations.
 * <p>
 * In some cases users may need to integrate some problem-specific equations along
 * with a primary set of differential equations. One example is optimal control where
 * adjoined parameters linked to the minimized hamiltonian must be integrated.
 * </p>
 * <p>
 * This interface allows users to add such equations to a primary set of {@link
 * OrdinaryDifferentialEquation first order differential equations}
 * thanks to the {@link
 * ExpandableODE#addSecondaryEquations(SecondaryODE)}
 * method.
 * </p>
 *
 * @see ExpandableODE
 */
public interface SecondaryODE {

    /**
     * Get the dimension of the secondary state parameters.
     *
     * @return dimension of the secondary state parameters
     */
    int getDimension();

    /**
     * Initialize equations at the start of an ODE integration.
     * <p>
     * This method is called once at the start of the integration. It
     * may be used by the equations to initialize some internal data
     * if needed.
     * </p>
     * <p>
     * The default implementation does nothing.
     * </p>
     *
     * @param t0         value of the independent <I>time</I> variable at integration start
     * @param primary0   array containing the value of the primary state vector at integration start
     * @param secondary0 array containing the value of the secondary state vector at integration start
     * @param finalTime  target time for the integration
     */
    default void init(double t0, double[] primary0, double[] secondary0, double finalTime) {
        // nothing by default
    }

    /**
     * Compute the derivatives related to the secondary state parameters.
     *
     * @param t          current value of the independent <I>time</I> variable
     * @param primary    array containing the current value of the primary state vector
     * @param primaryDot array containing the derivative of the primary state vector
     * @param secondary  array containing the current value of the secondary state vector
     * @return derivative of the secondary state vector
     * @throws MathIllegalStateException    if the number of functions evaluations is exceeded
     * @throws MathIllegalArgumentException if arrays dimensions do not match equations settings
     */
    double[] computeDerivatives(double t, double[] primary, double[] primaryDot, double[] secondary)
            throws MathIllegalArgumentException, MathIllegalStateException;

}
