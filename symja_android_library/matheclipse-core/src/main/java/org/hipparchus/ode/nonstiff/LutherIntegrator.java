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
import org.hipparchus.util.FastMath;


/**
 * This class implements the Luther sixth order Runge-Kutta
 * integrator for Ordinary Differential Equations.
 * <p>
 * <p>
 * This method is described in H. A. Luther 1968 paper <a
 * href="http://www.ams.org/journals/mcom/1968-22-102/S0025-5718-68-99876-1/S0025-5718-68-99876-1.pdf">
 * An explicit Sixth-Order Runge-Kutta Formula</a>.
 * </p>
 * <p>
 * <p>This method is an explicit Runge-Kutta method, its Butcher-array
 * is the following one :
 * <pre>
 *        0   |               0                     0                     0                     0                     0                     0
 *        1   |               1                     0                     0                     0                     0                     0
 *       1/2  |              3/8                   1/8                    0                     0                     0                     0
 *       2/3  |              8/27                  2/27                  8/27                   0                     0                     0
 *   (7-q)/14 | (  -21 +   9q)/392    (  -56 +   8q)/392    (  336 -  48q)/392    (  -63 +   3q)/392                  0                     0
 *   (7+q)/14 | (-1155 - 255q)/1960   ( -280 -  40q)/1960   (    0 - 320q)/1960   (   63 + 363q)/1960   ( 2352 + 392q)/1960                 0
 *        1   | (  330 + 105q)/180    (  120 +   0q)/180    ( -200 + 280q)/180    (  126 - 189q)/180    ( -686 - 126q)/180     ( 490 -  70q)/180
 *            |--------------------------------------------------------------------------------------------------------------------------------------------------
 *            |              1/20                   0                   16/45                  0                   49/180                 49/180         1/20
 * </pre>
 * where q = &radic;21</p>
 *
 * @see EulerIntegrator
 * @see ClassicalRungeKuttaIntegrator
 * @see GillIntegrator
 * @see MidpointIntegrator
 * @see ThreeEighthesIntegrator
 */

public class LutherIntegrator extends RungeKuttaIntegrator {

    /**
     * Square root.
     */
    private static final double Q = FastMath.sqrt(21);

    /**
     * Simple constructor.
     * Build a fourth-order Luther integrator with the given step.
     *
     * @param step integration step
     */
    public LutherIntegrator(final double step) {
        super("Luther", step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getC() {
        return new double[]{
                1.0, 1.0 / 2.0, 2.0 / 3.0, (7.0 - Q) / 14.0, (7.0 + Q) / 14.0, 1.0
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[][] getA() {
        return new double[][]{
                {1.0},
                {3.0 / 8.0, 1.0 / 8.0},
                {8.0 / 27.0, 2.0 / 27.0, 8.0 / 27.0},
                {(-21.0 + 9.0 * Q) / 392.0, (-56.0 + 8.0 * Q) / 392.0, (336.0 - 48.0 * Q) / 392.0, (-63.0 + 3.0 * Q) / 392.0},
                {(-1155.0 - 255.0 * Q) / 1960.0, (-280.0 - 40.0 * Q) / 1960.0, (0.0 - 320.0 * Q) / 1960.0, (63.0 + 363.0 * Q) / 1960.0, (2352.0 + 392.0 * Q) / 1960.0},
                {(330.0 + 105.0 * Q) / 180.0, (120.0 + 0.0 * Q) / 180.0, (-200.0 + 280.0 * Q) / 180.0, (126.0 - 189.0 * Q) / 180.0, (-686.0 - 126.0 * Q) / 180.0, (490.0 - 70.0 * Q) / 180.0}
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getB() {
        return new double[]{
                1.0 / 20.0, 0, 16.0 / 45.0, 0, 49.0 / 180.0, 49.0 / 180.0, 1.0 / 20.0
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LutherStateInterpolator
    createInterpolator(final boolean forward, double[][] yDotK,
                       final ODEStateAndDerivative globalPreviousState,
                       final ODEStateAndDerivative globalCurrentState,
                       final EquationsMapper mapper) {
        return new LutherStateInterpolator(forward, yDotK,
                globalPreviousState, globalCurrentState,
                globalPreviousState, globalCurrentState,
                mapper);
    }

}
