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

/**
 * Implementation of the uniform integer distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Uniform_distribution_(discrete)">
 * Uniform distribution (discrete), at Wikipedia</a>
 */
public class UniformIntegerDistribution extends AbstractIntegerDistribution {
    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 20120109L;
    /**
     * Lower bound (inclusive) of this distribution.
     */
    private final int lower;
    /**
     * Upper bound (inclusive) of this distribution.
     */
    private final int upper;

    /**
     * Creates a new uniform integer distribution using the given lower and
     * upper bounds (both inclusive).
     *
     * @param lower Lower bound (inclusive) of this distribution.
     * @param upper Upper bound (inclusive) of this distribution.
     * @throws MathIllegalArgumentException if {@code lower >= upper}.
     */
    public UniformIntegerDistribution(int lower, int upper)
            throws MathIllegalArgumentException {
        if (lower > upper) {
            throw new MathIllegalArgumentException(
                    LocalizedCoreFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND,
                    lower, upper, true);
        }
        this.lower = lower;
        this.upper = upper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double probability(int x) {
        if (x < lower || x > upper) {
            return 0;
        }
        return 1.0 / (upper - lower + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(int x) {
        if (x < lower) {
            return 0;
        }
        if (x > upper) {
            return 1;
        }
        return (x - lower + 1.0) / (upper - lower + 1.0);
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
     * For lower bound {@code lower} and upper bound {@code upper}, and
     * {@code n = upper - lower + 1}, the variance is {@code (n^2 - 1) / 12}.
     */
    @Override
    public double getNumericalVariance() {
        double n = upper - lower + 1;
        return (n * n - 1) / 12.0;
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
    public int getSupportLowerBound() {
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
    public int getSupportUpperBound() {
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
