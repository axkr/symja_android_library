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
package org.hipparchus.distribution.continuous;

/**
 * Implementation of the chi-squared distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Chi-squared_distribution">Chi-squared distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/Chi-SquaredDistribution.html">Chi-squared Distribution (MathWorld)</a>
 */
public class ChiSquaredDistribution extends AbstractRealDistribution {
    /**
     * Serializable version identifier
     */
    private static final long serialVersionUID = 20160320L;
    /**
     * Internal Gamma distribution.
     */
    private final GammaDistribution gamma;

    /**
     * Create a Chi-Squared distribution with the given degrees of freedom.
     *
     * @param degreesOfFreedom Degrees of freedom.
     */
    public ChiSquaredDistribution(double degreesOfFreedom) {
        this(degreesOfFreedom, DEFAULT_SOLVER_ABSOLUTE_ACCURACY);
    }

    /**
     * Create a Chi-Squared distribution with the given degrees of freedom and
     * inverse cumulative probability accuracy.
     *
     * @param degreesOfFreedom   Degrees of freedom.
     * @param inverseCumAccuracy the maximum absolute error in inverse
     *                           cumulative probability estimates (defaults to
     *                           {@link #DEFAULT_SOLVER_ABSOLUTE_ACCURACY}).
     */
    public ChiSquaredDistribution(double degreesOfFreedom,
                                  double inverseCumAccuracy) {
        super(inverseCumAccuracy);

        gamma = new GammaDistribution(degreesOfFreedom / 2, 2);
    }

    /**
     * Access the number of degrees of freedom.
     *
     * @return the degrees of freedom.
     */
    public double getDegreesOfFreedom() {
        return gamma.getShape() * 2.0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double density(double x) {
        return gamma.density(x);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public double logDensity(double x) {
        return gamma.logDensity(x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(double x) {
        return gamma.cumulativeProbability(x);
    }

    /**
     * {@inheritDoc}
     * <p>
     * For {@code k} degrees of freedom, the mean is {@code k}.
     */
    @Override
    public double getNumericalMean() {
        return getDegreesOfFreedom();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code 2 * k}, where {@code k} is the number of degrees of freedom.
     */
    @Override
    public double getNumericalVariance() {
        return 2 * getDegreesOfFreedom();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The lower bound of the support is always 0 no matter the
     * degrees of freedom.
     *
     * @return zero.
     */
    @Override
    public double getSupportLowerBound() {
        return 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The upper bound of the support is always positive infinity no matter the
     * degrees of freedom.
     *
     * @return {@code Double.POSITIVE_INFINITY}.
     */
    @Override
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The support of this distribution is connected.
     *
     * @return {@code true}
     */
    @Override
    public boolean isSupportConnected() {
        return true;
    }
}
