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
package org.hipparchus.distribution.discrete;

import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;

/**
 * Implementation of the geometric distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Geometric_distribution">Geometric distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/GeometricDistribution.html">Geometric Distribution (MathWorld)</a>
 */
public class GeometricDistribution extends AbstractIntegerDistribution {

    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 20130507L;
    /**
     * The probability of success.
     */
    private final double probabilityOfSuccess;
    /**
     * {@code log(p)} where p is the probability of success.
     */
    private final double logProbabilityOfSuccess;
    /**
     * {@code log(1 - p)} where p is the probability of success.
     */
    private final double log1mProbabilityOfSuccess;

    /**
     * Create a geometric distribution with the given probability of success.
     *
     * @param p probability of success.
     * @throws MathIllegalArgumentException if {@code p <= 0} or {@code p > 1}.
     */
    public GeometricDistribution(double p)
            throws MathIllegalArgumentException {
        if (p <= 0 || p > 1) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.OUT_OF_RANGE_LEFT, p, 0, 1);
        }

        probabilityOfSuccess = p;
        logProbabilityOfSuccess = FastMath.log(p);
        log1mProbabilityOfSuccess = FastMath.log1p(-p);
    }

    /**
     * Access the probability of success for this distribution.
     *
     * @return the probability of success.
     */
    public double getProbabilityOfSuccess() {
        return probabilityOfSuccess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double probability(int x) {
        if (x < 0) {
            return 0.0;
        } else {
            return FastMath.exp(log1mProbabilityOfSuccess * x) * probabilityOfSuccess;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double logProbability(int x) {
        if (x < 0) {
            return Double.NEGATIVE_INFINITY;
        } else {
            return x * log1mProbabilityOfSuccess + logProbabilityOfSuccess;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(int x) {
        if (x < 0) {
            return 0.0;
        } else {
            return -FastMath.expm1(log1mProbabilityOfSuccess * (x + 1));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * For probability parameter {@code p}, the mean is {@code (1 - p) / p}.
     */
    @Override
    public double getNumericalMean() {
        return (1 - probabilityOfSuccess) / probabilityOfSuccess;
    }

    /**
     * {@inheritDoc}
     * <p>
     * For probability parameter {@code p}, the variance is
     * {@code (1 - p) / (p * p)}.
     */
    @Override
    public double getNumericalVariance() {
        return (1 - probabilityOfSuccess) / (probabilityOfSuccess * probabilityOfSuccess);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The lower bound of the support is always 0.
     *
     * @return lower bound of the support (always 0)
     */
    @Override
    public int getSupportLowerBound() {
        return 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The upper bound of the support is infinite (which we approximate as
     * {@code Integer.MAX_VALUE}).
     *
     * @return upper bound of the support (always Integer.MAX_VALUE)
     */
    @Override
    public int getSupportUpperBound() {
        return Integer.MAX_VALUE;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int inverseCumulativeProbability(double p) throws MathIllegalArgumentException {
        MathUtils.checkRangeInclusive(p, 0, 1);

        if (p == 1) {
            return Integer.MAX_VALUE;
        }
        if (p == 0) {
            return 0;
        }
        return Math.max(0, (int) Math.ceil(FastMath.log1p(-p) / log1mProbabilityOfSuccess - 1));
    }
}
