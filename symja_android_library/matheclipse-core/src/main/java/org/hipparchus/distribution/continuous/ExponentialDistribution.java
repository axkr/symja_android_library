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
 * Implementation of the exponential distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Exponential_distribution">Exponential distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/ExponentialDistribution.html">Exponential distribution (MathWorld)</a>
 */
public class ExponentialDistribution extends AbstractRealDistribution {
    /**
     * Serializable version identifier
     */
    private static final long serialVersionUID = 20160320L;
    /**
     * The mean of this distribution.
     */
    private final double mean;
    /**
     * The logarithm of the mean, stored to reduce computing time.
     **/
    private final double logMean;

    /**
     * Create an exponential distribution with the given mean.
     *
     * @param mean Mean of this distribution.
     * @throws MathIllegalArgumentException if {@code mean <= 0}.
     */
    public ExponentialDistribution(double mean)
            throws MathIllegalArgumentException {
        if (mean <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.MEAN, mean);
        }

        this.mean = mean;
        this.logMean = FastMath.log(mean);
    }

    /**
     * Access the mean.
     *
     * @return the mean.
     */
    public double getMean() {
        return mean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double density(double x) {
        final double logDensity = logDensity(x);
        return logDensity == Double.NEGATIVE_INFINITY ? 0 : FastMath.exp(logDensity);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public double logDensity(double x) {
        if (x < 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return -x / mean - logMean;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation of this method is based on:
     * <ul>
     * <li>
     * <a href="http://mathworld.wolfram.com/ExponentialDistribution.html">
     * Exponential Distribution</a>, equation (1).</li>
     * </ul>
     */
    @Override
    public double cumulativeProbability(double x) {
        double ret;
        if (x <= 0.0) {
            ret = 0.0;
        } else {
            ret = 1.0 - FastMath.exp(-x / mean);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns {@code 0} when {@code p= = 0} and
     * {@code Double.POSITIVE_INFINITY} when {@code p == 1}.
     */
    @Override
    public double inverseCumulativeProbability(double p) throws MathIllegalArgumentException {
        MathUtils.checkRangeInclusive(p, 0, 1);

        double ret;
        if (p == 1.0) {
            ret = Double.POSITIVE_INFINITY;
        } else {
            ret = -mean * FastMath.log(1.0 - p);
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     * <p>
     * For mean parameter {@code k}, the mean is {@code k}.
     */
    @Override
    public double getNumericalMean() {
        return getMean();
    }

    /**
     * {@inheritDoc}
     * <p>
     * For mean parameter {@code k}, the variance is {@code k^2}.
     */
    @Override
    public double getNumericalVariance() {
        final double m = getMean();
        return m * m;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The lower bound of the support is always 0 no matter the mean parameter.
     *
     * @return lower bound of the support (always 0)
     */
    @Override
    public double getSupportLowerBound() {
        return 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The upper bound of the support is always positive infinity
     * no matter the mean parameter.
     *
     * @return upper bound of the support (always Double.POSITIVE_INFINITY)
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
