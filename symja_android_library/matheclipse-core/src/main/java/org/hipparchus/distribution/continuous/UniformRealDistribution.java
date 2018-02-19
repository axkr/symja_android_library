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
import org.hipparchus.util.MathUtils;

/**
 * Implementation of the uniform real distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Uniform_distribution_(continuous)">
 * Uniform distribution (continuous), at Wikipedia</a>
 */
public class UniformRealDistribution extends AbstractRealDistribution {
    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 20120109L;
    /**
     * Lower bound of this distribution (inclusive).
     */
    private final double lower;
    /**
     * Upper bound of this distribution (exclusive).
     */
    private final double upper;

    /**
     * Create a standard uniform real distribution with lower bound (inclusive)
     * equal to zero and upper bound (exclusive) equal to one.
     */
    public UniformRealDistribution() {
        this(0, 1);
    }

    /**
     * Create a uniform real distribution using the given lower and upper
     * bounds.
     *
     * @param lower Lower bound of this distribution (inclusive).
     * @param upper Upper bound of this distribution (exclusive).
     * @throws MathIllegalArgumentException if {@code lower >= upper}.
     */
    public UniformRealDistribution(double lower, double upper)
            throws MathIllegalArgumentException {
        super();
        if (lower >= upper) {
            throw new MathIllegalArgumentException(
                    LocalizedCoreFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND,
                    lower, upper, false);
        }

        this.lower = lower;
        this.upper = upper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double density(double x) {
        if (x < lower || x > upper) {
            return 0.0;
        }
        return 1 / (upper - lower);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(double x) {
        if (x <= lower) {
            return 0;
        }
        if (x >= upper) {
            return 1;
        }
        return (x - lower) / (upper - lower);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double inverseCumulativeProbability(final double p)
            throws MathIllegalArgumentException {
        MathUtils.checkRangeInclusive(p, 0, 1);
        return p * (upper - lower) + lower;
    }

    /**
     * {@inheritDoc}
     * <p>
     * For lower bound {@code lower} and upper bound {@code upper}, the mean is
     * {@code 0.5 * (lower + upper)}.
     */
    @Override
    public double getNumericalMean() {
        return 0.5 * (lower + upper);
    }

    /**
     * {@inheritDoc}
     * <p>
     * For lower bound {@code lower} and upper bound {@code upper}, the
     * variance is {@code (upper - lower)^2 / 12}.
     */
    @Override
    public double getNumericalVariance() {
        double ul = upper - lower;
        return ul * ul / 12;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The lower bound of the support is equal to the lower bound parameter
     * of the distribution.
     *
     * @return lower bound of the support
     */
    @Override
    public double getSupportLowerBound() {
        return lower;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The upper bound of the support is equal to the upper bound parameter
     * of the distribution.
     *
     * @return upper bound of the support
     */
    @Override
    public double getSupportUpperBound() {
        return upper;
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
