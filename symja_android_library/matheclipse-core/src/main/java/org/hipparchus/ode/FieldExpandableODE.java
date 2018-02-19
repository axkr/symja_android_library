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

import org.hipparchus.RealFieldElement;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.util.MathArrays;

import java.util.ArrayList;
import java.util.List;


/**
 * This class represents a combined set of first order differential equations,
 * with at least a primary set of equations expandable by some sets of secondary
 * equations.
 * <p>
 * One typical use case is the computation of the Jacobian matrix for some ODE.
 * In this case, the primary set of equations corresponds to the raw ODE, and we
 * add to this set another bunch of secondary equations which represent the Jacobian
 * matrix of the primary set.
 * </p>
 * <p>
 * We want the integrator to use <em>only</em> the primary set to estimate the
 * errors and hence the step sizes. It should <em>not</em> use the secondary
 * equations in this computation. The {@link FieldODEIntegrator integrator} will
 * be able to know where the primary set ends and so where the secondary sets begin.
 * </p>
 *
 * @param <T> the type of the field elements
 * @see FieldOrdinaryDifferentialEquation
 * @see FieldSecondaryODE
 */

public class FieldExpandableODE<T extends RealFieldElement<T>> {

    /**
     * Primary differential equation.
     */
    private final FieldOrdinaryDifferentialEquation<T> primary;

    /**
     * Components of the expandable ODE.
     */
    private List<FieldSecondaryODE<T>> components;

    /**
     * Mapper for all equations.
     */
    private FieldEquationsMapper<T> mapper;

    /**
     * Build an expandable set from its primary ODE set.
     *
     * @param primary the primary set of differential equations to be integrated.
     */
    public FieldExpandableODE(final FieldOrdinaryDifferentialEquation<T> primary) {
        this.primary = primary;
        this.components = new ArrayList<FieldSecondaryODE<T>>();
        this.mapper = new FieldEquationsMapper<T>(null, primary.getDimension());
    }

    /**
     * Get the mapper for the set of equations.
     *
     * @return mapper for the set of equations
     */
    public FieldEquationsMapper<T> getMapper() {
        return mapper;
    }

    /**
     * Add a set of secondary equations to be integrated along with the primary set.
     *
     * @param secondary secondary equations set
     * @return index of the secondary equation in the expanded state, to be used
     * as the parameter to {@link FieldODEState#getSecondaryState(int)} and
     * {@link FieldODEStateAndDerivative#getSecondaryDerivative(int)} (beware index
     * 0 corresponds to primary state, secondary states start at 1)
     */
    public int addSecondaryEquations(final FieldSecondaryODE<T> secondary) {

        components.add(secondary);
        mapper = new FieldEquationsMapper<T>(mapper, secondary.getDimension());

        return components.size();

    }

    /**
     * Initialize equations at the start of an ODE integration.
     *
     * @param s0        state at integration start
     * @param finalTime target time for the integration
     * @throws MathIllegalStateException    if the number of functions evaluations is exceeded
     * @throws MathIllegalArgumentException if arrays dimensions do not match equations settings
     */
    public void init(final FieldODEState<T> s0, final T finalTime) {

        final T t0 = s0.getTime();

        // initialize primary equations
        int index = 0;
        final T[] primary0 = s0.getPrimaryState();
        primary.init(t0, primary0, finalTime);

        // initialize secondary equations
        while (++index < mapper.getNumberOfEquations()) {
            final T[] secondary0 = s0.getSecondaryState(index);
            components.get(index - 1).init(t0, primary0, secondary0, finalTime);
        }

    }

    /**
     * Get the current time derivative of the complete state vector.
     *
     * @param t current value of the independent <I>time</I> variable
     * @param y array containing the current value of the complete state vector
     * @return time derivative of the complete state vector
     * @throws MathIllegalStateException    if the number of functions evaluations is exceeded
     * @throws MathIllegalArgumentException if arrays dimensions do not match equations settings
     */
    public T[] computeDerivatives(final T t, final T[] y)
            throws MathIllegalArgumentException, MathIllegalStateException {

        final T[] yDot = MathArrays.buildArray(t.getField(), mapper.getTotalDimension());

        // compute derivatives of the primary equations
        int index = 0;
        final T[] primaryState = mapper.extractEquationData(index, y);
        final T[] primaryStateDot = primary.computeDerivatives(t, primaryState);
        mapper.insertEquationData(index, primaryStateDot, yDot);

        // Add contribution for secondary equations
        while (++index < mapper.getNumberOfEquations()) {
            final T[] componentState = mapper.extractEquationData(index, y);
            final T[] componentStateDot = components.get(index - 1).computeDerivatives(t, primaryState, primaryStateDot,
                    componentState);
            mapper.insertEquationData(index, componentStateDot, yDot);
        }

        return yDot;

    }

}
