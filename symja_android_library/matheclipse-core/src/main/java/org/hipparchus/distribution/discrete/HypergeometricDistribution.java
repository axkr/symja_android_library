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

/**
 * Implementation of the hypergeometric distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Hypergeometric_distribution">Hypergeometric distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/HypergeometricDistribution.html">Hypergeometric distribution (MathWorld)</a>
 */
public class HypergeometricDistribution extends AbstractIntegerDistribution {
    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 20160320L;
    /**
     * The number of successes in the population.
     */
    private final int numberOfSuccesses;
    /**
     * The population size.
     */
    private final int populationSize;
    /**
     * The sample size.
     */
    private final int sampleSize;
    /**
     * Cached numerical variance
     */
    private final double numericalVariance;

    /**
     * Construct a new hypergeometric distribution with the specified population
     * size, number of successes in the population, and sample size.
     *
     * @param populationSize    Population size.
     * @param numberOfSuccesses Number of successes in the population.
     * @param sampleSize        Sample size.
     * @throws MathIllegalArgumentException if {@code numberOfSuccesses < 0}.
     * @throws MathIllegalArgumentException if {@code populationSize <= 0}.
     * @throws MathIllegalArgumentException if {@code numberOfSuccesses > populationSize},
     *                                      or {@code sampleSize > populationSize}.
     */
    public HypergeometricDistribution(int populationSize, int numberOfSuccesses, int sampleSize)
            throws MathIllegalArgumentException {
        if (populationSize <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.POPULATION_SIZE,
                    populationSize);
        }
        if (numberOfSuccesses < 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_OF_SUCCESSES,
                    numberOfSuccesses);
        }
        if (sampleSize < 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_OF_SAMPLES,
                    sampleSize);
        }

        if (numberOfSuccesses > populationSize) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_OF_SUCCESS_LARGER_THAN_POPULATION_SIZE,
                    numberOfSuccesses, populationSize, true);
        }
        if (sampleSize > populationSize) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.SAMPLE_SIZE_LARGER_THAN_POPULATION_SIZE,
                    sampleSize, populationSize, true);
        }

        this.numberOfSuccesses = numberOfSuccesses;
        this.populationSize = populationSize;
        this.sampleSize = sampleSize;
        this.numericalVariance = calculateNumericalVariance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(int x) {
        double ret;

        int[] domain = getDomain(populationSize, numberOfSuccesses, sampleSize);
        if (x < domain[0]) {
            ret = 0.0;
        } else if (x >= domain[1]) {
            ret = 1.0;
        } else {
            ret = innerCumulativeProbability(domain[0], x, 1);
        }

        return ret;
    }

    /**
     * Return the domain for the given hypergeometric distribution parameters.
     *
     * @param n Population size.
     * @param m Number of successes in the population.
     * @param k Sample size.
     * @return a two element array containing the lower and upper bounds of the
     * hypergeometric distribution.
     */
    private int[] getDomain(int n, int m, int k) {
        return new int[]{getLowerDomain(n, m, k), getUpperDomain(m, k)};
    }

    /**
     * Return the lowest domain value for the given hypergeometric distribution
     * parameters.
     *
     * @param n Population size.
     * @param m Number of successes in the population.
     * @param k Sample size.
     * @return the lowest domain value of the hypergeometric distribution.
     */
    private int getLowerDomain(int n, int m, int k) {
        return FastMath.max(0, m - (n - k));
    }

    /**
     * Access the number of successes.
     *
     * @return the number of successes.
     */
    public int getNumberOfSuccesses() {
        return numberOfSuccesses;
    }

    /**
     * Access the population size.
     *
     * @return the population size.
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * Access the sample size.
     *
     * @return the sample size.
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Return the highest domain value for the given hypergeometric distribution
     * parameters.
     *
     * @param m Number of successes in the population.
     * @param k Sample size.
     * @return the highest domain value of the hypergeometric distribution.
     */
    private int getUpperDomain(int m, int k) {
        return FastMath.min(k, m);
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
     */
    @Override
    public double logProbability(int x) {
        double ret;

        int[] domain = getDomain(populationSize, numberOfSuccesses, sampleSize);
        if (x < domain[0] || x > domain[1]) {
            ret = Double.NEGATIVE_INFINITY;
        } else {
            double p = (double) sampleSize / (double) populationSize;
            double q = (double) (populationSize - sampleSize) / (double) populationSize;
            double p1 = SaddlePointExpansion.logBinomialProbability(x,
                    numberOfSuccesses, p, q);
            double p2 =
                    SaddlePointExpansion.logBinomialProbability(sampleSize - x,
                            populationSize - numberOfSuccesses, p, q);
            double p3 =
                    SaddlePointExpansion.logBinomialProbability(sampleSize, populationSize, p, q);
            ret = p1 + p2 - p3;
        }

        return ret;
    }

    /**
     * For this distribution, {@code X}, this method returns {@code P(X >= x)}.
     *
     * @param x Value at which the CDF is evaluated.
     * @return the upper tail CDF for this distribution.
     */
    public double upperCumulativeProbability(int x) {
        double ret;

        final int[] domain = getDomain(populationSize, numberOfSuccesses, sampleSize);
        if (x <= domain[0]) {
            ret = 1.0;
        } else if (x > domain[1]) {
            ret = 0.0;
        } else {
            ret = innerCumulativeProbability(domain[1], x, -1);
        }

        return ret;
    }

    /**
     * For this distribution, {@code X}, this method returns
     * {@code P(x0 <= X <= x1)}.
     * This probability is computed by summing the point probabilities for the
     * values {@code x0, x0 + 1, x0 + 2, ..., x1}, in the order directed by
     * {@code dx}.
     *
     * @param x0 Inclusive lower bound.
     * @param x1 Inclusive upper bound.
     * @param dx Direction of summation (1 indicates summing from x0 to x1, and
     *           0 indicates summing from x1 to x0).
     * @return {@code P(x0 <= X <= x1)}.
     */
    private double innerCumulativeProbability(int x0, int x1, int dx) {
        double ret = probability(x0);
        while (x0 != x1) {
            x0 += dx;
            ret += probability(x0);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * <p>
     * For population size {@code N}, number of successes {@code m}, and sample
     * size {@code n}, the mean is {@code n * m / N}.
     */
    @Override
    public double getNumericalMean() {
        return getSampleSize() * (getNumberOfSuccesses() / (double) getPopulationSize());
    }

    /**
     * {@inheritDoc}
     * <p>
     * For population size {@code N}, number of successes {@code m}, and sample
     * size {@code n}, the variance is
     * {@code [n * m * (N - n) * (N - m)] / [N^2 * (N - 1)]}.
     */
    @Override
    public double getNumericalVariance() {
        return numericalVariance;
    }

    /**
     * Calculate the numerical variance.
     *
     * @return the variance of this distribution
     */
    private double calculateNumericalVariance() {
        final double N = getPopulationSize();
        final double m = getNumberOfSuccesses();
        final double n = getSampleSize();
        return (n * m * (N - n) * (N - m)) / (N * N * (N - 1));
    }

    /**
     * {@inheritDoc}
     * <p>
     * For population size {@code N}, number of successes {@code m}, and sample
     * size {@code n}, the lower bound of the support is
     * {@code max(0, n + m - N)}.
     *
     * @return lower bound of the support
     */
    @Override
    public int getSupportLowerBound() {
        return FastMath.max(0,
                getSampleSize() + getNumberOfSuccesses() - getPopulationSize());
    }

    /**
     * {@inheritDoc}
     * <p>
     * For number of successes {@code m} and sample size {@code n}, the upper
     * bound of the support is {@code min(m, n)}.
     *
     * @return upper bound of the support
     */
    @Override
    public int getSupportUpperBound() {
        return FastMath.min(getNumberOfSuccesses(), getSampleSize());
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
