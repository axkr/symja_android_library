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

package org.hipparchus.ode;

/**
 * Container for time, main and secondary state vectors as well as their derivatives.
 *
 * @see OrdinaryDifferentialEquation
 * @see SecondaryODE
 * @see ODEIntegrator
 */

public class ODEStateAndDerivative extends ODEState {

    /**
     * Serializable UID.
     */
    private static final long serialVersionUID = 20160408L;

    /**
     * Derivative of the primary state at time.
     */
    private final double[] primaryDerivative;

    /**
     * Derivative of the secondary state at time.
     */
    private final double[][] secondaryDerivative;

    /**
     * Simple constructor.
     * <p>Calling this constructor is equivalent to call {@link
     * #ODEStateAndDerivative(double, double[], double[],
     * double[][], double[][]) ODEStateAndDerivative(time, state,
     * derivative, null, null)}.</p>
     *
     * @param time              time
     * @param primaryState      primary state at time
     * @param primaryDerivative derivative of the primary state at time
     */
    public ODEStateAndDerivative(double time, double[] primaryState, double[] primaryDerivative) {
        this(time, primaryState, primaryDerivative, null, null);
    }

    /**
     * Simple constructor.
     *
     * @param time                time
     * @param primaryState        primary state at time
     * @param primaryDerivative   derivative of the primary state at time
     * @param secondaryState      state at time (may be null)
     * @param secondaryDerivative derivative of the state at time (may be null)
     */
    public ODEStateAndDerivative(double time, double[] primaryState, double[] primaryDerivative,
                                 double[][] secondaryState, double[][] secondaryDerivative) {
        super(time, primaryState, secondaryState);
        this.primaryDerivative = primaryDerivative.clone();
        this.secondaryDerivative = copy(secondaryDerivative);
    }

    /**
     * Get derivative of the primary state at time.
     *
     * @return derivative of the primary state at time
     * @see #getSecondaryDerivative(int)
     * @see #getCompleteDerivative()
     */
    public double[] getPrimaryDerivative() {
        return primaryDerivative.clone();
    }

    /**
     * Get derivative of the secondary state at time.
     *
     * @param index index of the secondary set as returned
     *              by {@link ExpandableODE#addSecondaryEquations(SecondaryODE)}
     *              (beware index 0 corresponds to primary state, secondary states start at 1)
     * @return derivative of the secondary state at time
     * @see #getPrimaryDerivative()
     * @see #getCompleteDerivative()
     */
    public double[] getSecondaryDerivative(final int index) {
        return index == 0 ? primaryDerivative.clone() : secondaryDerivative[index - 1].clone();
    }

    /**
     * Get complete derivative at time.
     *
     * @return complete derivative at time, starting with
     * {@link #getPrimaryDerivative() primary derivative}, followed
     * by all {@link #getSecondaryDerivative(int) secondary derivatives} in
     * increasing index order
     * @see #getPrimaryDerivative()
     * @see #getSecondaryDerivative(int)
     */
    public double[] getCompleteDerivative() {
        final double[] completeDerivative = new double[getCompleteStateDimension()];
        System.arraycopy(primaryDerivative, 0, completeDerivative, 0, primaryDerivative.length);
        int offset = primaryDerivative.length;
        if (secondaryDerivative != null) {
            for (int index = 0; index < secondaryDerivative.length; ++index) {
                System.arraycopy(secondaryDerivative[index], 0,
                        completeDerivative, offset,
                        secondaryDerivative[index].length);
                offset += secondaryDerivative[index].length;
            }
        }
        return completeDerivative;
    }

}
