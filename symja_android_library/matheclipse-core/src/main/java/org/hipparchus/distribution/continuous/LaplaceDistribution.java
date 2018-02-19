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
 * This class implements the Laplace distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Laplace_distribution">Laplace distribution (Wikipedia)</a>
 */
public class LaplaceDistribution extends AbstractRealDistribution {

    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 20141003L;

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
    public LaplaceDistribution(double mu, double beta)
            throws MathIllegalArgumentException {
        if (beta <= 0.0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NOT_POSITIVE_SCALE, beta);
        }

        this.mu = mu;
        this.beta = beta;
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
        return FastMath.exp(-FastMath.abs(x - mu) / beta) / (2.0 * beta);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(double x) {
        if (x <= mu) {
            return FastMath.exp((x - mu) / beta) / 2.0;
        } else {
            return 1.0 - FastMath.exp((mu - x) / beta) / 2.0;
        }
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
        double x = (p > 0.5) ? -Math.log(2.0 - 2.0 * p) : Math.log(2.0 * p);
        return mu + beta * x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getNumericalMean() {
        return mu;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getNumericalVariance() {
        return 2.0 * beta * beta;
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
