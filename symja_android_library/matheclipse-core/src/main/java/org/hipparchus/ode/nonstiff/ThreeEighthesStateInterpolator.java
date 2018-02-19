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
 * This class implements a step interpolator for the 3/8 fourth
 * order Runge-Kutta integrator.
 * <p>
 * <p>This interpolator allows to compute dense output inside the last
 * step computed. The interpolation equation is consistent with the
 * integration scheme :
 * <ul>
 * <li>Using reference point at step start:<br>
 * y(t<sub>n</sub> + &theta; h) = y (t<sub>n</sub>)
 * + &theta; (h/8) [ (8 - 15 &theta; +  8 &theta;<sup>2</sup>) y'<sub>1</sub>
 * +  3 * (15 &theta; - 12 &theta;<sup>2</sup>) y'<sub>2</sub>
 * +        3 &theta;                           y'<sub>3</sub>
 * +      (-3 &theta; +  4 &theta;<sup>2</sup>) y'<sub>4</sub>
 * ]
 * </li>
 * <li>Using reference point at step end:<br>
 * y(t<sub>n</sub> + &theta; h) = y (t<sub>n</sub> + h)
 * - (1 - &theta;) (h/8) [(1 - 7 &theta; + 8 &theta;<sup>2</sup>) y'<sub>1</sub>
 * + 3 (1 +   &theta; - 4 &theta;<sup>2</sup>) y'<sub>2</sub>
 * + 3 (1 +   &theta;)                         y'<sub>3</sub>
 * +   (1 +   &theta; + 4 &theta;<sup>2</sup>) y'<sub>4</sub>
 * ]
 * </li>
 * </ul>
 * </p>
 * <p>
 * where &theta; belongs to [0 ; 1] and where y'<sub>1</sub> to y'<sub>4</sub> are the four
 * evaluations of the derivatives already computed during the
 * step.</p>
 *
 * @see ThreeEighthesIntegrator
 */

class ThreeEighthesStateInterpolator
        extends RungeKuttaStateInterpolator {

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
    ThreeEighthesStateInterpolator(final boolean forward,
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
    protected ThreeEighthesStateInterpolator create(final boolean newForward, final double[][] newYDotK,
                                                    final ODEStateAndDerivative newGlobalPreviousState,
                                                    final ODEStateAndDerivative newGlobalCurrentState,
                                                    final ODEStateAndDerivative newSoftPreviousState,
                                                    final ODEStateAndDerivative newSoftCurrentState,
                                                    final EquationsMapper newMapper) {
        return new ThreeEighthesStateInterpolator(newForward, newYDotK,
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

        final double coeffDot3 = 0.75 * theta;
        final double coeffDot1 = coeffDot3 * (4 * theta - 5) + 1;
        final double coeffDot2 = coeffDot3 * (5 - 6 * theta);
        final double coeffDot4 = coeffDot3 * (2 * theta - 1);
        final double[] interpolatedState;
        final double[] interpolatedDerivatives;

        if (getGlobalPreviousState() != null && theta <= 0.5) {
            final double s = thetaH / 8.0;
            final double fourTheta2 = 4 * theta * theta;
            final double coeff1 = s * (8 - 15 * theta + 2 * fourTheta2);
            final double coeff2 = 3 * s * (5 * theta - fourTheta2);
            final double coeff3 = 3 * s * theta;
            final double coeff4 = s * (-3 * theta + fourTheta2);
            interpolatedState = previousStateLinearCombination(coeff1, coeff2, coeff3, coeff4);
            interpolatedDerivatives = derivativeLinearCombination(coeffDot1, coeffDot2, coeffDot3, coeffDot4);
        } else {
            final double s = oneMinusThetaH / -8.0;
            final double fourTheta2 = 4 * theta * theta;
            final double coeff1 = s * (1 - 7 * theta + 2 * fourTheta2);
            final double coeff2 = 3 * s * (1 + theta - fourTheta2);
            final double coeff3 = 3 * s * (1 + theta);
            final double coeff4 = s * (1 + theta + fourTheta2);
            interpolatedState = currentStateLinearCombination(coeff1, coeff2, coeff3, coeff4);
            interpolatedDerivatives = derivativeLinearCombination(coeffDot1, coeffDot2, coeffDot3, coeffDot4);
        }

        return mapper.mapStateAndDerivative(time, interpolatedState, interpolatedDerivatives);

    }

}
