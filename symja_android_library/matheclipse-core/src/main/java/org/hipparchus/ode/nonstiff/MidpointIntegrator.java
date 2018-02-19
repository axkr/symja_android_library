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

/**
 * This class implements a second order Runge-Kutta integrator for
 * Ordinary Differential Equations.
 * <p>
 * <p>This method is an explicit Runge-Kutta method, its Butcher-array
 * is the following one :
 * <pre>
 *    0  |  0    0
 *   1/2 | 1/2   0
 *       |----------
 *       |  0    1
 * </pre>
 * </p>
 *
 * @see EulerIntegrator
 * @see ClassicalRungeKuttaIntegrator
 * @see GillIntegrator
 * @see ThreeEighthesIntegrator
 * @see LutherIntegrator
 */

public class MidpointIntegrator extends RungeKuttaIntegrator {

    /**
     * Simple constructor.
     * Build a midpoint integrator with the given step.
     *
     * @param step integration step
     */
    public MidpointIntegrator(final double step) {
        super("midpoint", step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getC() {
        return new double[]{
                1.0 / 2.0
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[][] getA() {
        return new double[][]{
                {1.0 / 2.0}
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getB() {
        return new double[]{
                0.0, 1.0
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MidpointStateInterpolator
    createInterpolator(final boolean forward, double[][] yDotK,
                       final ODEStateAndDerivative globalPreviousState,
                       final ODEStateAndDerivative globalCurrentState,
                       final EquationsMapper mapper) {
        return new MidpointStateInterpolator(forward, yDotK,
                globalPreviousState, globalCurrentState,
                globalPreviousState, globalCurrentState,
                mapper);
    }

}
