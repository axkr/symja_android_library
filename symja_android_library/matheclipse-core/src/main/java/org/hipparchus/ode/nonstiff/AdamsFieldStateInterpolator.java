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

package org.hipparchus.ode.nonstiff;

import org.hipparchus.RealFieldElement;
import org.hipparchus.linear.Array2DRowFieldMatrix;
import org.hipparchus.ode.FieldEquationsMapper;
import org.hipparchus.ode.FieldODEStateAndDerivative;
import org.hipparchus.ode.sampling.AbstractFieldODEStateInterpolator;
import org.hipparchus.util.MathArrays;

import java.util.Arrays;

/**
 * This class implements an interpolator for Adams integrators using Nordsieck representation.
 * <p>
 * <p>This interpolator computes dense output around the current point.
 * The interpolation equation is based on Taylor series formulas.
 *
 * @param <T> the type of the field elements
 * @see AdamsBashforthFieldIntegrator
 * @see AdamsMoultonFieldIntegrator
 */

class AdamsFieldStateInterpolator<T extends RealFieldElement<T>> extends AbstractFieldODEStateInterpolator<T> {

    /**
     * Reference state.
     * <p>Sometimes, the reference state is the same as globalPreviousState,
     * sometimes it is the same as globalCurrentState, so we use a separate
     * field to avoid any confusion.
     * </p>
     */
    private final FieldODEStateAndDerivative<T> reference;
    /**
     * First scaled derivative.
     */
    private final T[] scaled;
    /**
     * Nordsieck vector.
     */
    private final Array2DRowFieldMatrix<T> nordsieck;
    /**
     * Step size used in the first scaled derivative and Nordsieck vector.
     */
    private T scalingH;

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
    AdamsFieldStateInterpolator(final T stepSize, final FieldODEStateAndDerivative<T> reference,
                                final T[] scaled, final Array2DRowFieldMatrix<T> nordsieck,
                                final boolean isForward,
                                final FieldODEStateAndDerivative<T> globalPreviousState,
                                final FieldODEStateAndDerivative<T> globalCurrentState,
                                final FieldEquationsMapper<T> equationsMapper) {
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
    private AdamsFieldStateInterpolator(final T stepSize, final FieldODEStateAndDerivative<T> reference,
                                        final T[] scaled, final Array2DRowFieldMatrix<T> nordsieck,
                                        final boolean isForward,
                                        final FieldODEStateAndDerivative<T> globalPreviousState,
                                        final FieldODEStateAndDerivative<T> globalCurrentState,
                                        final FieldODEStateAndDerivative<T> softPreviousState,
                                        final FieldODEStateAndDerivative<T> softCurrentState,
                                        final FieldEquationsMapper<T> equationsMapper) {
        super(isForward, globalPreviousState, globalCurrentState,
                softPreviousState, softCurrentState, equationsMapper);
        this.scalingH = stepSize;
        this.reference = reference;
        this.scaled = scaled.clone();
        this.nordsieck = new Array2DRowFieldMatrix<T>(nordsieck.getData(), false);
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
     * @param <S>             the type of the field elements
     * @return estimated state
     */
    public static <S extends RealFieldElement<S>> FieldODEStateAndDerivative<S> taylor(final FieldEquationsMapper<S> equationsMapper,
                                                                                       final FieldODEStateAndDerivative<S> reference,
                                                                                       final S time, final S stepSize,
                                                                                       final S[] scaled,
                                                                                       final Array2DRowFieldMatrix<S> nordsieck) {

        final S x = time.subtract(reference.getTime());
        final S normalizedAbscissa = x.divide(stepSize);

        S[] stateVariation = MathArrays.buildArray(time.getField(), scaled.length);
        Arrays.fill(stateVariation, time.getField().getZero());
        S[] estimatedDerivatives = MathArrays.buildArray(time.getField(), scaled.length);
        Arrays.fill(estimatedDerivatives, time.getField().getZero());

        // apply Taylor formula from high order to low order,
        // for the sake of numerical accuracy
        final S[][] nData = nordsieck.getDataRef();
        for (int i = nData.length - 1; i >= 0; --i) {
            final int order = i + 2;
            final S[] nDataI = nData[i];
            final S power = normalizedAbscissa.pow(order);
            for (int j = 0; j < nDataI.length; ++j) {
                final S d = nDataI[j].multiply(power);
                stateVariation[j] = stateVariation[j].add(d);
                estimatedDerivatives[j] = estimatedDerivatives[j].add(d.multiply(order));
            }
        }

        S[] estimatedState = reference.getCompleteState();
        for (int j = 0; j < stateVariation.length; ++j) {
            stateVariation[j] = stateVariation[j].add(scaled[j].multiply(normalizedAbscissa));
            estimatedState[j] = estimatedState[j].add(stateVariation[j]);
            estimatedDerivatives[j] =
                    estimatedDerivatives[j].add(scaled[j].multiply(normalizedAbscissa)).divide(x);
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
    protected AdamsFieldStateInterpolator<T> create(boolean newForward,
                                                    FieldODEStateAndDerivative<T> newGlobalPreviousState,
                                                    FieldODEStateAndDerivative<T> newGlobalCurrentState,
                                                    FieldODEStateAndDerivative<T> newSoftPreviousState,
                                                    FieldODEStateAndDerivative<T> newSoftCurrentState,
                                                    FieldEquationsMapper<T> newMapper) {
        return new AdamsFieldStateInterpolator<T>(scalingH, reference, scaled, nordsieck,
                newForward,
                newGlobalPreviousState, newGlobalCurrentState,
                newSoftPreviousState, newSoftCurrentState,
                newMapper);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> equationsMapper,
                                                                                   final T time, final T theta,
                                                                                   final T thetaH, final T oneMinusThetaH) {
        return taylor(equationsMapper, reference, time, scalingH, scaled, nordsieck);
    }

}
