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

import org.hipparchus.ode.EquationsMapper;
import org.hipparchus.ode.ODEStateAndDerivative;
import org.hipparchus.ode.sampling.AbstractODEStateInterpolator;

/**
 * This class represents an interpolator over the last step during an
 * ODE integration for Runge-Kutta and embedded Runge-Kutta integrators.
 *
 * @see RungeKuttaIntegrator
 * @see EmbeddedRungeKuttaIntegrator
 */

abstract class RungeKuttaStateInterpolator extends AbstractODEStateInterpolator {

    /**
     * Serializable UID.
     */
    private static final long serialVersionUID = 20160328L;

    /**
     * Slopes at the intermediate points
     */
    protected double[][] yDotK;

    /**
     * Simple constructor.
     *
     * @param forward             integration direction indicator
     * @param yDotK               slopes at the intermediate points
     * @param globalPreviousState start of the global step
     * @param globalCurrentState  end of the global step
     * @param softPreviousState   start of the restricted step
     * @param softCurrentState    end of the restricted step
     * @param mapper              equations mapper for the all equations
     */
    protected RungeKuttaStateInterpolator(final boolean forward,
                                          final double[][] yDotK,
                                          final ODEStateAndDerivative globalPreviousState,
                                          final ODEStateAndDerivative globalCurrentState,
                                          final ODEStateAndDerivative softPreviousState,
                                          final ODEStateAndDerivative softCurrentState,
                                          final EquationsMapper mapper) {
        super(forward, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
        this.yDotK = new double[yDotK.length][];
        for (int i = 0; i < yDotK.length; ++i) {
            this.yDotK[i] = yDotK[i].clone();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RungeKuttaStateInterpolator create(boolean newForward,
                                                 ODEStateAndDerivative newGlobalPreviousState,
                                                 ODEStateAndDerivative newGlobalCurrentState,
                                                 ODEStateAndDerivative newSoftPreviousState,
                                                 ODEStateAndDerivative newSoftCurrentState,
                                                 EquationsMapper newMapper) {
        return create(newForward, yDotK,
                newGlobalPreviousState, newGlobalCurrentState,
                newSoftPreviousState, newSoftCurrentState,
                newMapper);
    }

    /**
     * Create a new instance.
     *
     * @param newForward             integration direction indicator
     * @param newYDotK               slopes at the intermediate points
     * @param newGlobalPreviousState start of the global step
     * @param newGlobalCurrentState  end of the global step
     * @param newSoftPreviousState   start of the restricted step
     * @param newSoftCurrentState    end of the restricted step
     * @param newMapper              equations mapper for the all equations
     * @return a new instance
     */
    protected abstract RungeKuttaStateInterpolator create(boolean newForward, double[][] newYDotK,
                                                          ODEStateAndDerivative newGlobalPreviousState,
                                                          ODEStateAndDerivative newGlobalCurrentState,
                                                          ODEStateAndDerivative newSoftPreviousState,
                                                          ODEStateAndDerivative newSoftCurrentState,
                                                          EquationsMapper newMapper);

    /**
     * Compute a state by linear combination added to previous state.
     *
     * @param coefficients coefficients to apply to the method staged derivatives
     * @return combined state
     */
    protected final double[] previousStateLinearCombination(final double... coefficients) {
        return combine(getGlobalPreviousState().getCompleteState(),
                coefficients);
    }

    /**
     * Compute a state by linear combination added to current state.
     *
     * @param coefficients coefficients to apply to the method staged derivatives
     * @return combined state
     */
    protected double[] currentStateLinearCombination(final double... coefficients) {
        return combine(getGlobalCurrentState().getCompleteState(),
                coefficients);
    }

    /**
     * Compute a state derivative by linear combination.
     *
     * @param coefficients coefficients to apply to the method staged derivatives
     * @return combined state
     */
    protected double[] derivativeLinearCombination(final double... coefficients) {
        return combine(new double[yDotK[0].length], coefficients);
    }

    /**
     * Linearly combine arrays.
     *
     * @param a            array to add to
     * @param coefficients coefficients to apply to the method staged derivatives
     * @return a itself, as a conveniency for fluent API
     */
    private double[] combine(final double[] a, final double... coefficients) {
        for (int i = 0; i < a.length; ++i) {
            for (int k = 0; k < coefficients.length; ++k) {
                a[i] += coefficients[k] * yDotK[k][i];
            }
        }
        return a;
    }

}
