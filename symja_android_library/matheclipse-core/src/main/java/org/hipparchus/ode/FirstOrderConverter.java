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

package org.hipparchus.ode;

/**
 * This class converts second order differential equations to first
 * order ones.
 * <p>
 * <p>This class is a wrapper around a {@link SecondOrderODE} which
 * allow to use a {@link ODEIntegrator} to integrate it.</p>
 * <p>
 * <p>The transformation is done by changing the n dimension state
 * vector to a 2n dimension vector, where the first n components are
 * the initial state variables and the n last components are their
 * first time derivative. The first time derivative of this state
 * vector then really contains both the first and second time
 * derivative of the initial state vector, which can be handled by the
 * underlying second order equations set.</p>
 * <p>
 * <p>One should be aware that the data is duplicated during the
 * transformation process and that for each call to {@link
 * #computeDerivatives computeDerivatives}, this wrapper does copy 4n
 * scalars : 2n before the call to {@link
 * SecondOrderODE#computeSecondDerivatives
 * computeSecondDerivatives} in order to dispatch the y state vector
 * into z and zDot, and 2n after the call to gather zDot and zDDot
 * into yDot. Since the underlying problem by itself perhaps also
 * needs to copy data and dispatch the arrays into domain objects,
 * this has an impact on both memory and CPU usage. The only way to
 * avoid this duplication is to perform the transformation at the
 * problem level, i.e. to implement the problem as a first order one
 * and then avoid using this class.</p>
 *
 * @see ODEIntegrator
 * @see OrdinaryDifferentialEquation
 * @see SecondOrderODE
 */

public class FirstOrderConverter implements OrdinaryDifferentialEquation {

    /**
     * Underlying second order equations set.
     */
    private final SecondOrderODE equations;

    /**
     * second order problem dimension.
     */
    private final int dimension;

    /**
     * Simple constructor.
     * Build a converter around a second order equations set.
     *
     * @param equations second order equations set to convert
     */
    public FirstOrderConverter(final SecondOrderODE equations) {
        this.equations = equations;
        dimension = equations.getDimension();
    }

    /**
     * {@inheritDoc}
     * <p>The dimension of the first order problem is twice the
     * dimension of the underlying second order problem.</p>
     *
     * @return dimension of the problem
     */
    @Override
    public int getDimension() {
        return 2 * dimension;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] computeDerivatives(final double t, final double[] y) {

        final double[] yDot = new double[y.length];

        // split the state vector in two
        final double[] z = new double[dimension];
        final double[] zDot = new double[dimension];
        System.arraycopy(y, 0, z, 0, dimension);
        System.arraycopy(y, dimension, zDot, 0, dimension);

        // apply the underlying equations set
        final double[] zDDot = equations.computeSecondDerivatives(t, z, zDot);

        // build the result state derivative
        System.arraycopy(zDot, 0, yDot, 0, dimension);
        System.arraycopy(zDDot, 0, yDot, dimension, dimension);

        return yDot;

    }

}
