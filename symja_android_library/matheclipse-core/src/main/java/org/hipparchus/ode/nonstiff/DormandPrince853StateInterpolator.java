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
 * This class represents an interpolator over the last step during an
 * ODE integration for the 8(5,3) Dormand-Prince integrator.
 *
 * @see DormandPrince853Integrator
 */

class DormandPrince853StateInterpolator
        extends RungeKuttaStateInterpolator {

    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 20160328L;

    /**
     * Interpolation weights.
     */
    private static final double[][] D = {
            {
                    // this row is the same as the b array
                    104257.0 / 1920240.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    3399327.0 / 763840.0,
                    66578432.0 / 35198415.0,
                    -1674902723.0 / 288716400.0,
                    54980371265625.0 / 176692375811392.0,
                    -734375.0 / 4826304.0,
                    171414593.0 / 851261400.0,
                    137909.0 / 3084480.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
            }, {
            1815983.0 / 1920240.0,
            0.0,
            0.0,
            0.0,
            0.0,
            -3399327.0 / 763840.0,
            -66578432.0 / 35198415.0,
            1674902723.0 / 288716400.0,
            -54980371265625.0 / 176692375811392.0,
            734375.0 / 4826304.0,
            -171414593.0 / 851261400.0,
            -137909.0 / 3084480.0,
            0.0,
            0.0,
            0.0,
            0.0,
    }, {
            -855863.0 / 960120.0,
            0.0,
            0.0,
            0.0,
            0.0,
            3399327.0 / 381920.0,
            133156864.0 / 35198415.0,
            -1674902723.0 / 144358200.0,
            54980371265625.0 / 88346187905696.0,
            -734375.0 / 2413152.0,
            171414593.0 / 425630700.0,
            137909.0 / 1542240.0,
            -1.0,
            0.0,
            0.0,
            0.0
    }, {
            -17751989329.0 / 2106076560.0,
            0.0,
            0.0,
            0.0,
            0.0,
            4272954039.0 / 7539864640.0,
            -118476319744.0 / 38604839385.0,
            755123450731.0 / 316657731600.0,
            3692384461234828125.0 / 1744130441634250432.0,
            -4612609375.0 / 5293382976.0,
            2091772278379.0 / 933644586600.0,
            2136624137.0 / 3382989120.0,
            -126493.0 / 1421424.0,
            98350000.0 / 5419179.0,
            -18878125.0 / 2053168.0,
            -1944542619.0 / 438351368.0
    }, {
            32941697297.0 / 3159114840.0,
            0.0,
            0.0,
            0.0,
            0.0,
            456696183123.0 / 1884966160.0,
            19132610714624.0 / 115814518155.0,
            -177904688592943.0 / 474986597400.0,
            -4821139941836765625.0 / 218016305204281304.0,
            30702015625.0 / 3970037232.0,
            -85916079474274.0 / 2800933759800.0,
            -5919468007.0 / 634310460.0,
            2479159.0 / 157936.0,
            -18750000.0 / 602131.0,
            -19203125.0 / 2053168.0,
            15700361463.0 / 438351368.0
    }, {
            12627015655.0 / 631822968.0,
            0.0,
            0.0,
            0.0,
            0.0,
            -72955222965.0 / 188496616.0,
            -13145744952320.0 / 69488710893.0,
            30084216194513.0 / 56998391688.0,
            -296858761006640625.0 / 25648977082856624.0,
            569140625.0 / 82709109.0,
            -18684190637.0 / 18672891732.0,
            69644045.0 / 89549712.0,
            -11847025.0 / 4264272.0,
            -978650000.0 / 16257537.0,
            519371875.0 / 6159504.0,
            5256837225.0 / 438351368.0
    }, {
            -450944925.0 / 17550638.0,
            0.0,
            0.0,
            0.0,
            0.0,
            -14532122925.0 / 94248308.0,
            -595876966400.0 / 2573655959.0,
            188748653015.0 / 527762886.0,
            2545485458115234375.0 / 27252038150535163.0,
            -1376953125.0 / 36759604.0,
            53995596795.0 / 518691437.0,
            210311225.0 / 7047894.0,
            -1718875.0 / 39484.0,
            58000000.0 / 602131.0,
            -1546875.0 / 39484.0,
            -1262172375.0 / 8429834.0
    }
    };

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
    DormandPrince853StateInterpolator(final boolean forward,
                                      final double[][] yDotK,
                                      final ODEStateAndDerivative globalPreviousState,
                                      final ODEStateAndDerivative globalCurrentState,
                                      final ODEStateAndDerivative softPreviousState,
                                      final ODEStateAndDerivative softCurrentState,
                                      final EquationsMapper mapper) {
        super(forward, yDotK,
                globalPreviousState, globalCurrentState, softPreviousState, softCurrentState,
                mapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DormandPrince853StateInterpolator create(final boolean newForward, final double[][] newYDotK,
                                                       final ODEStateAndDerivative newGlobalPreviousState,
                                                       final ODEStateAndDerivative newGlobalCurrentState,
                                                       final ODEStateAndDerivative newSoftPreviousState,
                                                       final ODEStateAndDerivative newSoftCurrentState,
                                                       final EquationsMapper newMapper) {
        return new DormandPrince853StateInterpolator(newForward, newYDotK,
                newGlobalPreviousState, newGlobalCurrentState,
                newSoftPreviousState, newSoftCurrentState,
                newMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ODEStateAndDerivative computeInterpolatedStateAndDerivatives(final EquationsMapper mapper,
                                                                           final double time, final double theta,
                                                                           final double thetaH, final double oneMinusThetaH) {

        final double eta = 1.0 - theta;
        final double twoTheta = 2 * theta;
        final double theta2 = theta * theta;
        final double dot1 = 1.0 - twoTheta;
        final double dot2 = theta * (2 - 3 * theta);
        final double dot3 = twoTheta * (theta * (twoTheta - 3) + 1);
        final double dot4 = theta2 * (theta * (5 * theta - 8) + 3);
        final double dot5 = theta2 * (theta * (theta * (15 - 6 * theta) - 12) + 3);
        final double dot6 = theta2 * (theta * (theta * (theta * (18 - 7 * theta) - 15) + 4));
        final double[] interpolatedState;
        final double[] interpolatedDerivatives;


        if (getGlobalPreviousState() != null && theta <= 0.5) {
            final double f0 = thetaH;
            final double f1 = f0 * eta;
            final double f2 = f1 * theta;
            final double f3 = f2 * eta;
            final double f4 = f3 * theta;
            final double f5 = f4 * eta;
            final double f6 = f5 * theta;
            final double[] p = new double[16];
            final double[] q = new double[16];
            for (int i = 0; i < p.length; ++i) {
                p[i] = f0 * D[0][i] + f1 * D[1][i] + f2 * D[2][i] + f3 * D[3][i] +
                        f4 * D[4][i] + f5 * D[5][i] + f6 * D[6][i];
                q[i] = D[0][i] + dot1 * D[1][i] + dot2 * D[2][i] + dot3 * D[3][i] +
                        dot4 * D[4][i] + dot5 * D[5][i] + dot6 * D[6][i];
            }
            interpolatedState = previousStateLinearCombination(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7],
                    p[8], p[9], p[10], p[11], p[12], p[13], p[14], p[15]);
            interpolatedDerivatives = derivativeLinearCombination(q[0], q[1], q[2], q[3], q[4], q[5], q[6], q[7],
                    q[8], q[9], q[10], q[11], q[12], q[13], q[14], q[15]);
        } else {
            final double f0 = -oneMinusThetaH;
            final double f1 = -f0 * theta;
            final double f2 = f1 * theta;
            final double f3 = f2 * eta;
            final double f4 = f3 * theta;
            final double f5 = f4 * eta;
            final double f6 = f5 * theta;
            final double[] p = new double[16];
            final double[] q = new double[16];
            for (int i = 0; i < p.length; ++i) {
                p[i] = f0 * D[0][i] + f1 * D[1][i] + f2 * D[2][i] + f3 * D[3][i] +
                        f4 * D[4][i] + f5 * D[5][i] + f6 * D[6][i];
                q[i] = D[0][i] + dot1 * D[1][i] + dot2 * D[2][i] + dot3 * D[3][i] +
                        dot4 * D[4][i] + dot5 * D[5][i] + dot6 * D[6][i];
            }
            interpolatedState = currentStateLinearCombination(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7],
                    p[8], p[9], p[10], p[11], p[12], p[13], p[14], p[15]);
            interpolatedDerivatives = derivativeLinearCombination(q[0], q[1], q[2], q[3], q[4], q[5], q[6], q[7],
                    q[8], q[9], q[10], q[11], q[12], q[13], q[14], q[15]);
        }

        return mapper.mapStateAndDerivative(time, interpolatedState, interpolatedDerivatives);

    }

}
