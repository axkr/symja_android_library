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

import org.hipparchus.RealFieldElement;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.ode.FieldEquationsMapper;
import org.hipparchus.ode.FieldODEStateAndDerivative;

/**
 * This abstract class represents an interpolator over the last step
 * during an ODE integration.
 * <p>
 * <p>The various ODE integrators provide objects extending this class
 * to the step handlers. The handlers can use these objects to
 * retrieve the state vector at intermediate times between the
 * previous and the current grid points (dense output).</p>
 *
 * @param <T> the type of the field elements
 * @see org.hipparchus.ode.FieldODEIntegrator
 * @see FieldODEStepHandler
 */

public abstract class AbstractFieldODEStateInterpolator<T extends RealFieldElement<T>>
        implements FieldODEStateInterpolator<T> {

    /**
     * Global previous state.
     */
    private final FieldODEStateAndDerivative<T> globalPreviousState;

    /**
     * Global current state.
     */
    private final FieldODEStateAndDerivative<T> globalCurrentState;

    /**
     * Soft previous state.
     */
    private final FieldODEStateAndDerivative<T> softPreviousState;

    /**
     * Soft current state.
     */
    private final FieldODEStateAndDerivative<T> softCurrentState;

    /**
     * integration direction.
     */
    private final boolean forward;

    /**
     * Mapper for ODE equations primary and secondary components.
     */
    private FieldEquationsMapper<T> mapper;

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
    protected AbstractFieldODEStateInterpolator(final boolean isForward,
                                                final FieldODEStateAndDerivative<T> globalPreviousState,
                                                final FieldODEStateAndDerivative<T> globalCurrentState,
                                                final FieldODEStateAndDerivative<T> softPreviousState,
                                                final FieldODEStateAndDerivative<T> softCurrentState,
                                                final FieldEquationsMapper<T> equationsMapper) {
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
    public AbstractFieldODEStateInterpolator<T> restrictStep(final FieldODEStateAndDerivative<T> previousState,
                                                             final FieldODEStateAndDerivative<T> currentState) {
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
    protected abstract AbstractFieldODEStateInterpolator<T> create(boolean newForward,
                                                                   FieldODEStateAndDerivative<T> newGlobalPreviousState,
                                                                   FieldODEStateAndDerivative<T> newGlobalCurrentState,
                                                                   FieldODEStateAndDerivative<T> newSoftPreviousState,
                                                                   FieldODEStateAndDerivative<T> newSoftCurrentState,
                                                                   FieldEquationsMapper<T> newMapper);

    /**
     * Get the previous global grid point state.
     *
     * @return previous global grid point state
     */
    public FieldODEStateAndDerivative<T> getGlobalPreviousState() {
        return globalPreviousState;
    }

    /**
     * Get the current global grid point state.
     *
     * @return current global grid point state
     */
    public FieldODEStateAndDerivative<T> getGlobalCurrentState() {
        return globalCurrentState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldODEStateAndDerivative<T> getPreviousState() {
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
    public FieldODEStateAndDerivative<T> getCurrentState() {
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
    public FieldODEStateAndDerivative<T> getInterpolatedState(final T time) {
        final T thetaH = time.subtract(globalPreviousState.getTime());
        final T oneMinusThetaH = globalCurrentState.getTime().subtract(time);
        final T theta = thetaH.divide(globalCurrentState.getTime().subtract(globalPreviousState.getTime()));
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
    protected FieldEquationsMapper<T> getMapper() {
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
    protected abstract FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> equationsMapper,
                                                                                            T time, T theta,
                                                                                            T thetaH, T oneMinusThetaH)
            throws MathIllegalStateException;

}
