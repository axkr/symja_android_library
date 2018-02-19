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
 * ODE integration for the 5(4) Higham and Hall integrator.
 *
 * @see HighamHall54Integrator
 */

class HighamHall54StateInterpolator
        extends RungeKuttaStateInterpolator {

    /**
     * Serializable version identifier
     */
    private static final long serialVersionUID = 20111120L;

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
    HighamHall54StateInterpolator(final boolean forward,
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
    protected HighamHall54StateInterpolator create(final boolean newForward, final double[][] newYDotK,
                                                   final ODEStateAndDerivative newGlobalPreviousState,
                                                   final ODEStateAndDerivative newGlobalCurrentState,
                                                   final ODEStateAndDerivative newSoftPreviousState,
                                                   final ODEStateAndDerivative newSoftCurrentState,
                                                   final EquationsMapper newMapper) {
        return new HighamHall54StateInterpolator(newForward, newYDotK,
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

        final double bDot0 = 1 + theta * (-15.0 / 2.0 + theta * (16.0 - 10.0 * theta));
        final double bDot1 = 0;
        final double bDot2 = theta * (459.0 / 16.0 + theta * (-729.0 / 8.0 + 135.0 / 2.0 * theta));
        final double bDot3 = theta * (-44.0 + theta * (152.0 - 120.0 * theta));
        final double bDot4 = theta * (375.0 / 16.0 + theta * (-625.0 / 8.0 + 125.0 / 2.0 * theta));
        final double bDot5 = theta * 5.0 / 8.0 * (2 * theta - 1);

        final double[] interpolatedState;
        final double[] interpolatedDerivatives;
        if (getGlobalPreviousState() != null && theta <= 0.5) {
            final double b0 = thetaH * (1.0 + theta * (-15.0 / 4.0 + theta * (16.0 / 3.0 - 5.0 / 2.0 * theta)));
            final double b1 = 0;
            final double b2 = thetaH * (theta * (459.0 / 32.0 + theta * (-243.0 / 8.0 + theta * 135.0 / 8.0)));
            final double b3 = thetaH * (theta * (-22.0 + theta * (152.0 / 3.0 + theta * -30.0)));
            final double b4 = thetaH * (theta * (375.0 / 32.0 + theta * (-625.0 / 24.0 + theta * 125.0 / 8.0)));
            final double b5 = thetaH * (theta * (-5.0 / 16.0 + theta * 5.0 / 12.0));
            interpolatedState = previousStateLinearCombination(b0, b1, b2, b3, b4, b5);
            interpolatedDerivatives = derivativeLinearCombination(bDot0, bDot1, bDot2, bDot3, bDot4, bDot5);
        } else {
            final double theta2 = theta * theta;
            final double h = thetaH / theta;
            final double b0 = h * (-1.0 / 12.0 + theta * (1.0 + theta * (-15.0 / 4.0 + theta * (16.0 / 3.0 + theta * -5.0 / 2.0))));
            final double b1 = 0;
            final double b2 = h * (-27.0 / 32.0 + theta2 * (459.0 / 32.0 + theta * (-243.0 / 8.0 + theta * 135.0 / 8.0)));
            final double b3 = h * (4.0 / 3.0 + theta2 * (-22.0 + theta * (152.0 / 3.0 + theta * -30.0)));
            final double b4 = h * (-125.0 / 96.0 + theta2 * (375.0 / 32.0 + theta * (-625.0 / 24.0 + theta * 125.0 / 8.0)));
            final double b5 = h * (-5.0 / 48.0 + theta2 * (-5.0 / 16.0 + theta * 5.0 / 12.0));
            interpolatedState = currentStateLinearCombination(b0, b1, b2, b3, b4, b5);
            interpolatedDerivatives = derivativeLinearCombination(bDot0, bDot1, bDot2, bDot3, bDot4, bDot5);
        }

        return mapper.mapStateAndDerivative(time, interpolatedState, interpolatedDerivatives);

    }

}
