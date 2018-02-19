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
 * ODE integration for the 5(4) Dormand-Prince integrator.
 *
 * @see DormandPrince54Integrator
 */

class DormandPrince54StateInterpolator
        extends RungeKuttaStateInterpolator {

    /**
     * Last row of the Butcher-array internal weights, element 0.
     */
    private static final double A70 = 35.0 / 384.0;

    // element 1 is zero, so it is neither stored nor used

    /**
     * Last row of the Butcher-array internal weights, element 2.
     */
    private static final double A72 = 500.0 / 1113.0;

    /**
     * Last row of the Butcher-array internal weights, element 3.
     */
    private static final double A73 = 125.0 / 192.0;

    /**
     * Last row of the Butcher-array internal weights, element 4.
     */
    private static final double A74 = -2187.0 / 6784.0;

    /**
     * Last row of the Butcher-array internal weights, element 5.
     */
    private static final double A75 = 11.0 / 84.0;

    /**
     * Shampine (1986) Dense output, element 0.
     */
    private static final double D0 = -12715105075.0 / 11282082432.0;

    // element 1 is zero, so it is neither stored nor used

    /**
     * Shampine (1986) Dense output, element 2.
     */
    private static final double D2 = 87487479700.0 / 32700410799.0;

    /**
     * Shampine (1986) Dense output, element 3.
     */
    private static final double D3 = -10690763975.0 / 1880347072.0;

    /**
     * Shampine (1986) Dense output, element 4.
     */
    private static final double D4 = 701980252875.0 / 199316789632.0;

    /**
     * Shampine (1986) Dense output, element 5.
     */
    private static final double D5 = -1453857185.0 / 822651844.0;

    /**
     * Shampine (1986) Dense output, element 6.
     */
    private static final double D6 = 69997945.0 / 29380423.0;

    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 20160328L;

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
    DormandPrince54StateInterpolator(final boolean forward,
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
    protected DormandPrince54StateInterpolator create(final boolean newForward, final double[][] newYDotK,
                                                      final ODEStateAndDerivative newGlobalPreviousState,
                                                      final ODEStateAndDerivative newGlobalCurrentState,
                                                      final ODEStateAndDerivative newSoftPreviousState,
                                                      final ODEStateAndDerivative newSoftCurrentState,
                                                      final EquationsMapper newMapper) {
        return new DormandPrince54StateInterpolator(newForward, newYDotK,
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

        // interpolate
        final double eta = 1 - theta;
        final double twoTheta = 2 * theta;
        final double dot2 = 1 - twoTheta;
        final double dot3 = theta * (2 - 3 * theta);
        final double dot4 = twoTheta * (1 + theta * (twoTheta - 3));

        final double[] interpolatedState;
        final double[] interpolatedDerivatives;
        if (getGlobalPreviousState() != null && theta <= 0.5) {
            final double f1 = thetaH;
            final double f2 = f1 * eta;
            final double f3 = f2 * theta;
            final double f4 = f3 * eta;
            final double coeff0 = f1 * A70 - f2 * (A70 - 1) + f3 * (2 * A70 - 1) + f4 * D0;
            final double coeff1 = 0;
            final double coeff2 = f1 * A72 - f2 * A72 + f3 * (2 * A72) + f4 * D2;
            final double coeff3 = f1 * A73 - f2 * A73 + f3 * (2 * A73) + f4 * D3;
            final double coeff4 = f1 * A74 - f2 * A74 + f3 * (2 * A74) + f4 * D4;
            final double coeff5 = f1 * A75 - f2 * A75 + f3 * (2 * A75) + f4 * D5;
            final double coeff6 = f4 * D6 - f3;
            final double coeffDot0 = A70 - dot2 * (A70 - 1) + dot3 * (2 * A70 - 1) + dot4 * D0;
            final double coeffDot1 = 0;
            final double coeffDot2 = A72 - dot2 * A72 + dot3 * (2 * A72) + dot4 * D2;
            final double coeffDot3 = A73 - dot2 * A73 + dot3 * (2 * A73) + dot4 * D3;
            final double coeffDot4 = A74 - dot2 * A74 + dot3 * (2 * A74) + dot4 * D4;
            final double coeffDot5 = A75 - dot2 * A75 + dot3 * (2 * A75) + dot4 * D5;
            final double coeffDot6 = dot4 * D6 - dot3;
            interpolatedState = previousStateLinearCombination(coeff0, coeff1, coeff2, coeff3,
                    coeff4, coeff5, coeff6);
            interpolatedDerivatives = derivativeLinearCombination(coeffDot0, coeffDot1, coeffDot2, coeffDot3,
                    coeffDot4, coeffDot5, coeffDot6);
        } else {
            final double f1 = -oneMinusThetaH;
            final double f2 = oneMinusThetaH * theta;
            final double f3 = f2 * theta;
            final double f4 = f3 * eta;
            final double coeff0 = f1 * A70 - f2 * (A70 - 1) + f3 * (2 * A70 - 1) + f4 * D0;
            final double coeff1 = 0;
            final double coeff2 = f1 * A72 - f2 * A72 + f3 * (2 * A72) + f4 * D2;
            final double coeff3 = f1 * A73 - f2 * A73 + f3 * (2 * A73) + f4 * D3;
            final double coeff4 = f1 * A74 - f2 * A74 + f3 * (2 * A74) + f4 * D4;
            final double coeff5 = f1 * A75 - f2 * A75 + f3 * (2 * A75) + f4 * D5;
            final double coeff6 = f4 * D6 - f3;
            final double coeffDot0 = A70 - dot2 * (A70 - 1) + dot3 * (2 * A70 - 1) + dot4 * D0;
            final double coeffDot1 = 0;
            final double coeffDot2 = A72 - dot2 * A72 + dot3 * (2 * A72) + dot4 * D2;
            final double coeffDot3 = A73 - dot2 * A73 + dot3 * (2 * A73) + dot4 * D3;
            final double coeffDot4 = A74 - dot2 * A74 + dot3 * (2 * A74) + dot4 * D4;
            final double coeffDot5 = A75 - dot2 * A75 + dot3 * (2 * A75) + dot4 * D5;
            final double coeffDot6 = dot4 * D6 - dot3;
            interpolatedState = currentStateLinearCombination(coeff0, coeff1, coeff2, coeff3,
                    coeff4, coeff5, coeff6);
            interpolatedDerivatives = derivativeLinearCombination(coeffDot0, coeffDot1, coeffDot2, coeffDot3,
                    coeffDot4, coeffDot5, coeffDot6);
        }

        return mapper.mapStateAndDerivative(time, interpolatedState, interpolatedDerivatives);

    }

}
