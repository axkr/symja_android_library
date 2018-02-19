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

import java.util.Collections;
import java.util.List;

/**
 * Interface expanding {@link OrdinaryDifferentialEquation first order
 * differential equations} in order to compute exactly the Jacobian
 * matrices for {@link VariationalEquation partial derivatives equations}.
 */
public interface ODEJacobiansProvider
        extends OrdinaryDifferentialEquation, NamedParameterJacobianProvider {

    /**
     * Compute the Jacobian matrix of ODE with respect to state.
     *
     * @param t    current value of the independent <I>time</I> variable
     * @param y    array containing the current value of the main state vector
     * @param yDot array containing the current value of the time derivative of the main state vector
     * @return Jacobian matrix of the ODE w.r.t. the main state vector
     * @throws MathIllegalStateException    if the number of functions evaluations is exceeded
     * @throws MathIllegalArgumentException if arrays dimensions do not match equations settings
     */
    double[][] computeMainStateJacobian(double t, double[] y, double[] yDot)
            throws MathIllegalArgumentException, MathIllegalStateException;

    /**
     * {@inheritDoc}
     * <p>
     * The default implementation has no parameters at all.
     * </p>
     */
    @Override
    default List<String> getParametersNames() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default implementation supports no parameters at all.
     * </p>
     */
    @Override
    default boolean isSupported(String name) {
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default implementation supports no parameters at all.
     * </p>
     */
    @Override
    default double[] computeParameterJacobian(double t, double[] y, double[] yDot,
                                              String paramName)
            throws MathIllegalArgumentException {
        throw new MathIllegalArgumentException(LocalizedODEFormats.UNKNOWN_PARAMETER,
                paramName);
    }

}
