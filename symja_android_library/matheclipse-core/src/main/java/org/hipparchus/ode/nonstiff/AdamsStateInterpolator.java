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

package org.hipparchus.ode.nonstiff;

import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.ode.EquationsMapper;
import org.hipparchus.ode.ODEStateAndDerivative;
import org.hipparchus.ode.sampling.AbstractODEStateInterpolator;
import org.hipparchus.util.FastMath;

/**
 * This class implements an interpolator for integrators using Nordsieck representation.
 * <p>
 * <p>This interpolator computes dense output around the current point.
 * The interpolation equation is based on Taylor series formulas.
 *
 * @see org.hipparchus.ode.nonstiff.AdamsBashforthIntegrator
 * @see AdamsMoultonIntegrator
 */

class AdamsStateInterpolator extends AbstractODEStateInterpolator {

    /**
     * Serializable version identifier
     */
    private static final long serialVersionUID = 20160402L;
    /**
     * Reference state.
     * <p>Sometimes, the reference state is the same as globalPreviousState,
     * sometimes it is the same as globalCurrentState, so we use a separate
     * field to avoid any confusion.
     * </p>
     */
    private final ODEStateAndDerivative reference;
    /**
     * Step size used in the first scaled derivative and Nordsieck vector.
     */
    private double scalingH;
    /**
     * First scaled derivative.
     */
    private double[] scaled;

    /**
     * Nordsieck vector.
     */
    private Array2DRowRealMatrix nordsieck;

    /**
     * Simple constructor.
     *
     * @param stepSize            step size used in the scaled and Nordsieck arrays
     * @param reference           reference state from which Taylor expansion are estimated
     * @param scaled              first scaled derivative
     * @param nordsieck           Nordsieck vector
     * @param isForward           integration direction indicator
     * @param globalPreviousState start of the global step
     * @param globalCurrentState  end of the global step
     * @param equationsMapper     mapper for ODE equations primary and secondary components
     */
    AdamsStateInterpolator(final double stepSize, final ODEStateAndDerivative reference,
                           final double[] scaled, final Array2DRowRealMatrix nordsieck,
                           final boolean isForward,
                           final ODEStateAndDerivative globalPreviousState,
                           final ODEStateAndDerivative globalCurrentState,
                           final EquationsMapper equationsMapper) {
        this(stepSize, reference, scaled, nordsieck,
                isForward, globalPreviousState, globalCurrentState,
                globalPreviousState, globalCurrentState, equationsMapper);
    }

    /**
     * Simple constructor.
     *
     * @param stepSize            step size used in the scaled and Nordsieck arrays
     * @param reference           reference state from which Taylor expansion are estimated
     * @param scaled              first scaled derivative
     * @param nordsieck           Nordsieck vector
     * @param isForward           integration direction indicator
     * @param globalPreviousState start of the global step
     * @param globalCurrentState  end of the global step
     * @param softPreviousState   start of the restricted step
     * @param softCurrentState    end of the restricted step
     * @param equationsMapper     mapper for ODE equations primary and secondary components
     */
    private AdamsStateInterpolator(final double stepSize, final ODEStateAndDerivative reference,
                                   final double[] scaled, final Array2DRowRealMatrix nordsieck,
                                   final boolean isForward,
                                   final ODEStateAndDerivative globalPreviousState,
                                   final ODEStateAndDerivative globalCurrentState,
                                   final ODEStateAndDerivative softPreviousState,
                                   final ODEStateAndDerivative softCurrentState,
                                   final EquationsMapper equationsMapper) {
        super(isForward, globalPreviousState, globalCurrentState,
                softPreviousState, softCurrentState, equationsMapper);
        this.scalingH = stepSize;
        this.reference = reference;
        this.scaled = scaled.clone();
        this.nordsieck = new Array2DRowRealMatrix(nordsieck.getData(), false);
    }

    /**
     * Estimate state by applying Taylor formula.
     *
     * @param equationsMapper mapper for ODE equations primary and secondary components
     * @param reference       reference state
     * @param time            time at which state must be estimated
     * @param stepSize        step size used in the scaled and Nordsieck arrays
     * @param scaled          first scaled derivative
     * @param nordsieck       Nordsieck vector
     * @return estimated state
     */
    public static ODEStateAndDerivative taylor(final EquationsMapper equationsMapper,
                                               final ODEStateAndDerivative reference,
                                               final double time, final double stepSize,
                                               final double[] scaled,
                                               final Array2DRowRealMatrix nordsieck) {

        final double x = time - reference.getTime();
        final double normalizedAbscissa = x / stepSize;

        double[] stateVariation = new double[scaled.length];
        double[] estimatedDerivatives = new double[scaled.length];

        // apply Taylor formula from high order to low order,
        // for the sake of numerical accuracy
        final double[][] nData = nordsieck.getDataRef();
        for (int i = nData.length - 1; i >= 0; --i) {
            final int order = i + 2;
            final double[] nDataI = nData[i];
            final double power = FastMath.pow(normalizedAbscissa, order);
            for (int j = 0; j < nDataI.length; ++j) {
                final double d = nDataI[j] * power;
                stateVariation[j] = stateVariation[j] + d;
                estimatedDerivatives[j] = estimatedDerivatives[j] + d * order;
            }
        }

        double[] estimatedState = reference.getCompleteState();
        for (int j = 0; j < stateVariation.length; ++j) {
            stateVariation[j] = stateVariation[j] + scaled[j] * normalizedAbscissa;
            estimatedState[j] = estimatedState[j] + stateVariation[j];
            estimatedDerivatives[j] = (estimatedDerivatives[j] + scaled[j] * normalizedAbscissa) / x;
        }

        return equationsMapper.mapStateAndDerivative(time, estimatedState, estimatedDerivatives);

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
    protected AdamsStateInterpolator create(boolean newForward,
                                            ODEStateAndDerivative newGlobalPreviousState,
                                            ODEStateAndDerivative newGlobalCurrentState,
                                            ODEStateAndDerivative newSoftPreviousState,
                                            ODEStateAndDerivative newSoftCurrentState,
                                            EquationsMapper newMapper) {
        return new AdamsStateInterpolator(scalingH, reference, scaled, nordsieck,
                newForward,
                newGlobalPreviousState, newGlobalCurrentState,
                newSoftPreviousState, newSoftCurrentState,
                newMapper);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ODEStateAndDerivative computeInterpolatedStateAndDerivatives(final EquationsMapper equationsMapper,
                                                                           final double time, final double theta,
                                                                           final double thetaH, final double oneMinusThetaH) {
        return taylor(equationsMapper, reference, time, scalingH, scaled, nordsieck);
    }

}
