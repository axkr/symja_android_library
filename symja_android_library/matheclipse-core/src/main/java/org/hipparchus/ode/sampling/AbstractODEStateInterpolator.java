/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
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

package org.hipparchus.ode.sampling;

import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.ode.EquationsMapper;
import org.hipparchus.ode.ODEStateAndDerivative;

/**
 * This abstract class represents an interpolator over the last step
 * during an ODE integration.
 * <p>
 * <p>The various ODE integrators provide objects extending this class
 * to the step handlers. The handlers can use these objects to
 * retrieve the state vector at intermediate times between the
 * previous and the current grid points (dense output).</p>
 *
 * @see org.hipparchus.ode.ODEIntegrator
 * @see ODEStepHandler
 */

public abstract class AbstractODEStateInterpolator
        implements ODEStateInterpolator {

    /**
     * Serializable UID.
     */
    private static final long serialVersionUID = 20160328L;

    /**
     * Global previous state.
     */
    private final ODEStateAndDerivative globalPreviousState;

    /**
     * Global current state.
     */
    private final ODEStateAndDerivative globalCurrentState;

    /**
     * Soft previous state.
     */
    private final ODEStateAndDerivative softPreviousState;

    /**
     * Soft current state.
     */
    private final ODEStateAndDerivative softCurrentState;

    /**
     * integration direction.
     */
    private final boolean forward;

    /**
     * Mapper for ODE equations primary and secondary components.
     */
    private EquationsMapper mapper;

    /**
     * Simple constructor.
     *
     * @param isForward           integration direction indicator
     * @param globalPreviousState start of the global step
     * @param globalCurrentState  end of the global step
     * @param softPreviousState   start of the restricted step
     * @param softCurrentState    end of the restricted step
     * @param equationsMapper     mapper for ODE equations primary and secondary components
     */
    protected AbstractODEStateInterpolator(final boolean isForward,
                                           final ODEStateAndDerivative globalPreviousState,
                                           final ODEStateAndDerivative globalCurrentState,
                                           final ODEStateAndDerivative softPreviousState,
                                           final ODEStateAndDerivative softCurrentState,
                                           final EquationsMapper equationsMapper) {
        this.forward = isForward;
        this.globalPreviousState = globalPreviousState;
        this.globalCurrentState = globalCurrentState;
        this.softPreviousState = softPreviousState;
        this.softCurrentState = softCurrentState;
        this.mapper = equationsMapper;
    }

    /**
     * Create a new restricted version of the instance.
     * <p>
     * The instance is not changed at all.
     * </p>
     *
     * @param previousState start of the restricted step
     * @param currentState  end of the restricted step
     * @return restricted version of the instance
     * @see #getPreviousState()
     * @see #getCurrentState()
     */
    public AbstractODEStateInterpolator restrictStep(final ODEStateAndDerivative previousState,
                                                     final ODEStateAndDerivative currentState) {
        return create(forward, globalPreviousState, globalCurrentState, previousState, currentState, mapper);
    }

    /**
     * Create a new instance.
     *
     * @param newForward             integration direction indicator
     * @param newGlobalPreviousState start of the global step
     * @param newGlobalCurrentState  end of the global step
     * @param newSoftPreviousState   start of the restricted step
     * @param newSoftCurrentState    end of the restricted step
     * @param newMapper              equations mapper for the all equations
     * @return a new instance
     */
    protected abstract AbstractODEStateInterpolator create(boolean newForward,
                                                           ODEStateAndDerivative newGlobalPreviousState,
                                                           ODEStateAndDerivative newGlobalCurrentState,
                                                           ODEStateAndDerivative newSoftPreviousState,
                                                           ODEStateAndDerivative newSoftCurrentState,
                                                           EquationsMapper newMapper);

    /**
     * Get the previous global grid point state.
     *
     * @return previous global grid point state
     */
    public ODEStateAndDerivative getGlobalPreviousState() {
        return globalPreviousState;
    }

    /**
     * Get the current global grid point state.
     *
     * @return current global grid point state
     */
    public ODEStateAndDerivative getGlobalCurrentState() {
        return globalCurrentState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODEStateAndDerivative getPreviousState() {
        return softPreviousState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPreviousStateInterpolated() {
        return softPreviousState != globalPreviousState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODEStateAndDerivative getCurrentState() {
        return softCurrentState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCurrentStateInterpolated() {
        return softCurrentState != globalCurrentState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODEStateAndDerivative getInterpolatedState(final double time) {
        final double thetaH = time - globalPreviousState.getTime();
        final double oneMinusThetaH = globalCurrentState.getTime() - time;
        final double theta = thetaH / (globalCurrentState.getTime() - globalPreviousState.getTime());
        return computeInterpolatedStateAndDerivatives(mapper, time, theta, thetaH, oneMinusThetaH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isForward() {
        return forward;
    }

    /**
     * Get the mapper for ODE equations primary and secondary components.
     *
     * @return mapper for ODE equations primary and secondary components
     */
    protected EquationsMapper getMapper() {
        return mapper;
    }

    /**
     * Compute the state and derivatives at the interpolated time.
     * This is the main processing method that should be implemented by
     * the derived classes to perform the interpolation.
     *
     * @param equationsMapper mapper for ODE equations primary and secondary components
     * @param time            interpolation time
     * @param theta           normalized interpolation abscissa within the step
     *                        (theta is zero at the previous time step and one at the current time step)
     * @param thetaH          time gap between the previous time and the interpolated time
     * @param oneMinusThetaH  time gap between the interpolated time and
     *                        the current time
     * @return interpolated state and derivatives
     * @throws MathIllegalStateException if the number of functions evaluations is exceeded
     */
    protected abstract ODEStateAndDerivative computeInterpolatedStateAndDerivatives(EquationsMapper equationsMapper,
                                                                                    double time, double theta,
                                                                                    double thetaH, double oneMinusThetaH)
            throws MathIllegalStateException;

}
