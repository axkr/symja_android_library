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
 * This class implements the 3/8 fourth order Runge-Kutta
 * integrator for Ordinary Differential Equations.
 * <p>
 * <p>This method is an explicit Runge-Kutta method, its Butcher-array
 * is the following one :
 * <pre>
 *    0  |  0    0    0    0
 *   1/3 | 1/3   0    0    0
 *   2/3 |-1/3   1    0    0
 *    1  |  1   -1    1    0
 *       |--------------------
 *       | 1/8  3/8  3/8  1/8
 * </pre>
 * </p>
 *
 * @see EulerIntegrator
 * @see ClassicalRungeKuttaIntegrator
 * @see GillIntegrator
 * @see MidpointIntegrator
 * @see LutherIntegrator
 */

public class ThreeEighthesIntegrator extends RungeKuttaIntegrator {

    /**
     * Simple constructor.
     * Build a 3/8 integrator with the given step.
     *
     * @param step integration step
     */
    public ThreeEighthesIntegrator(final double step) {
        super("3/8", step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getC() {
        return new double[]{
                1.0 / 3.0, 2.0 / 3.0, 1.0
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[][] getA() {
        return new double[][]{
                {1.0 / 3.0},
                {-1.0 / 3.0, 1.0},
                {1.0, -1.0, 1.0}
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getB() {
        return new double[]{
                1.0 / 8.0, 3.0 / 8.0, 3.0 / 8.0, 1.0 / 8.0
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ThreeEighthesStateInterpolator
    createInterpolator(final boolean forward, double[][] yDotK,
                       final ODEStateAndDerivative globalPreviousState,
                       final ODEStateAndDerivative globalCurrentState,
                       final EquationsMapper mapper) {
        return new ThreeEighthesStateInterpolator(forward, yDotK,
                globalPreviousState, globalCurrentState,
                globalPreviousState, globalCurrentState,
                mapper);
    }

}
