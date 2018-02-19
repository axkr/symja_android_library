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
import org.hipparchus.special.Beta;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;

/**
 * Implementation of the binomial distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Binomial_distribution">Binomial distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/BinomialDistribution.html">Binomial Distribution (MathWorld)</a>
 */
public class BinomialDistribution extends AbstractIntegerDistribution {
    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 20160320L;
    /**
     * The number of trials.
     */
    private final int numberOfTrials;
    /**
     * The probability of success.
     */
    private final double probabilityOfSuccess;

    /**
     * Create a binomial distribution with the given number of trials and
     * probability of success.
     *
     * @param trials Number of trials.
     * @param p      Probability of success.
     * @throws MathIllegalArgumentException if {@code trials < 0}.
     * @throws MathIllegalArgumentException if {@code p < 0} or {@code p > 1}.
     */
    public BinomialDistribution(int trials, double p)
            throws MathIllegalArgumentException {
        if (trials < 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_OF_TRIALS,
                    trials);
        }

        MathUtils.checkRangeInclusive(p, 0, 1);

        probabilityOfSuccess = p;
        numberOfTrials = trials;
    }

    /**
     * Access the number of trials for this distribution.
     *
     * @return the number of trials.
     */
    public int getNumberOfTrials() {
        return numberOfTrials;
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
        final double logProbability = logProbability(x);
        return logProbability == Double.NEGATIVE_INFINITY ? 0 : FastMath.exp(logProbability);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public double logProbability(int x) {
        if (numberOfTrials == 0) {
            return (x == 0) ? 0. : Double.NEGATIVE_INFINITY;
        }
        double ret;
        if (x < 0 || x > numberOfTrials) {
            ret = Double.NEGATIVE_INFINITY;
        } else {
            ret = SaddlePointExpansion.logBinomialProbability(x,
                    numberOfTrials, probabilityOfSuccess,
                    1.0 - probabilityOfSuccess);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(int x) {
        double ret;
        if (x < 0) {
            ret = 0.0;
        } else if (x >= numberOfTrials) {
            ret = 1.0;
        } else {
            ret = 1.0 - Beta.regularizedBeta(probabilityOfSuccess,
                    x + 1.0, numberOfTrials - x);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * <p>
     * For {@code n} trials and probability parameter {@code p}, the mean is
     * {@code n * p}.
     */
    @Override
    public double getNumericalMean() {
        return numberOfTrials * probabilityOfSuccess;
    }

    /**
     * {@inheritDoc}
     * <p>
     * For {@code n} trials and probability parameter {@code p}, the variance is
     * {@code n * p * (1 - p)}.
     */
    @Override
    public double getNumericalVariance() {
        final double p = probabilityOfSuccess;
        return numberOfTrials * p * (1 - p);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The lower bound of the support is always 0 except for the probability
     * parameter {@code p = 1}.
     *
     * @return lower bound of the support (0 or the number of trials)
     */
    @Override
    public int getSupportLowerBound() {
        return probabilityOfSuccess < 1.0 ? 0 : numberOfTrials;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The upper bound of the support is the number of trials except for the
     * probability parameter {@code p = 0}.
     *
     * @return upper bound of the support (number of trials or 0)
     */
    @Override
    public int getSupportUpperBound() {
        return probabilityOfSuccess > 0.0 ? numberOfTrials : 0;
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
