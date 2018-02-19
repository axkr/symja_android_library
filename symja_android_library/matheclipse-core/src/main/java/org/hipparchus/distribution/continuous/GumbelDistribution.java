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

import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;

/**
 * This class implements the Gumbel distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Gumbel_distribution">Gumbel Distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/GumbelDistribution.html">Gumbel Distribution (Mathworld)</a>
 */
public class GumbelDistribution extends AbstractRealDistribution {

    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 20141003L;

    /**
     * Approximation of Euler's constant
     * see http://mathworld.wolfram.com/Euler-MascheroniConstantApproximations.html
     */
    private static final double EULER = FastMath.PI / (2 * FastMath.E);

    /**
     * The location parameter.
     */
    private final double mu;
    /**
     * The scale parameter.
     */
    private final double beta;

    /**
     * Build a new instance.
     *
     * @param mu   location parameter
     * @param beta scale parameter (must be positive)
     * @throws MathIllegalArgumentException if {@code beta <= 0}
     */
    public GumbelDistribution(double mu, double beta)
            throws MathIllegalArgumentException {
        if (beta <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.SCALE, beta);
        }

        this.beta = beta;
        this.mu = mu;
    }

    /**
     * Access the location parameter, {@code mu}.
     *
     * @return the location parameter.
     */
    public double getLocation() {
        return mu;
    }

    /**
     * Access the scale parameter, {@code beta}.
     *
     * @return the scale parameter.
     */
    public double getScale() {
        return beta;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double density(double x) {
        final double z = (x - mu) / beta;
        final double t = FastMath.exp(-z);
        return FastMath.exp(-z - t) / beta;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(double x) {
        final double z = (x - mu) / beta;
        return FastMath.exp(-FastMath.exp(-z));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double inverseCumulativeProbability(double p) throws MathIllegalArgumentException {
        MathUtils.checkRangeInclusive(p, 0, 1);

        if (p == 0) {
            return Double.NEGATIVE_INFINITY;
        } else if (p == 1) {
            return Double.POSITIVE_INFINITY;
        }
        return mu - FastMath.log(-FastMath.log(p)) * beta;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getNumericalMean() {
        return mu + EULER * beta;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getNumericalVariance() {
        return (MathUtils.PI_SQUARED) / 6.0 * (beta * beta);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSupportLowerBound() {
        return Double.NEGATIVE_INFINITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupportConnected() {
        return true;
    }

}
