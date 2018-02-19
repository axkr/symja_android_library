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
 * equations in this computation. The {@link AbstractIntegrator integrator} will
 * be able to know where the primary set ends and so where the secondary sets begin.
 * </p>
 *
 * @see OrdinaryDifferentialEquation
 * @see VariationalEquation
 */

public class ExpandableODE {

    /**
     * Primary differential equation.
     */
    private final OrdinaryDifferentialEquation primary;

    /**
     * Components of the expandable ODE.
     */
    private List<SecondaryODE> components;

    /**
     * Mapper for all equations.
     */
    private EquationsMapper mapper;

    /**
     * Build an expandable set from its primary ODE set.
     *
     * @param primary the primary set of differential equations to be integrated.
     */
    public ExpandableODE(final OrdinaryDifferentialEquation primary) {
        this.primary = primary;
        this.components = new ArrayList<SecondaryODE>();
        this.mapper = new EquationsMapper(null, primary.getDimension());
    }

    /**
     * Get the primaryset of differential equations to be integrated.
     *
     * @return primary set of differential equations to be integrated
     */
    public OrdinaryDifferentialEquation getPrimary() {
        return primary;
    }

    /**
     * Get the mapper for the set of equations.
     *
     * @return mapper for the set of equations
     */
    public EquationsMapper getMapper() {
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
    public int addSecondaryEquations(final SecondaryODE secondary) {

        components.add(secondary);
        mapper = new EquationsMapper(mapper, secondary.getDimension());

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
    public void init(final ODEState s0, final double finalTime) {

        final double t0 = s0.getTime();

        // initialize primary equations
        int index = 0;
        final double[] primary0 = s0.getPrimaryState();
        primary.init(t0, primary0, finalTime);

        // initialize secondary equations
        while (++index < mapper.getNumberOfEquations()) {
            final double[] secondary0 = s0.getSecondaryState(index);
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
    public double[] computeDerivatives(final double t, final double[] y)
            throws MathIllegalArgumentException, MathIllegalStateException {

        final double[] yDot = new double[mapper.getTotalDimension()];

        // compute derivatives of the primary equations
        int index = 0;
        final double[] primaryState = mapper.extractEquationData(index, y);
        final double[] primaryStateDot = primary.computeDerivatives(t, primaryState);
        mapper.insertEquationData(index, primaryStateDot, yDot);

        // Add contribution for secondary equations
        while (++index < mapper.getNumberOfEquations()) {
            final double[] componentState = mapper.extractEquationData(index, y);
            final double[] componentStateDot = components.get(index - 1).computeDerivatives(t, primaryState, primaryStateDot,
                    componentState);
            mapper.insertEquationData(index, componentStateDot, yDot);
        }

        return yDot;

    }

}
