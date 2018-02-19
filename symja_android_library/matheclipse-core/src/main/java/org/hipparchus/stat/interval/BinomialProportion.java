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
package org.hipparchus.stat.interval;

import org.hipparchus.distribution.continuous.FDistribution;
import org.hipparchus.distribution.continuous.NormalDistribution;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;

/**
 * Utility methods to generate confidence intervals for a binomial proportion.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval">
 * Binomial proportion confidence interval (Wikipedia)</a>
 */
public class BinomialProportion {

    /**
     * The standard normal distribution to calculate the inverse cumulative probability.
     * Accessed and used in a thread-safe way.
     */
    private static final NormalDistribution NORMAL_DISTRIBUTION = new NormalDistribution(0, 1);

    /**
     * Utility class, prevent instantiation.
     */
    private BinomialProportion() {
    }

    /**
     * Create an Agresti-Coull binomial confidence interval for the true
     * probability of success of an unknown binomial distribution with
     * the given observed number of trials, probability of success and
     * confidence level.
     * <p>
     * Preconditions:
     * <ul>
     * <li>{@code numberOfTrials} must be positive</li>
     * <li>{@code probabilityOfSuccess} must be between 0 and 1 (inclusive)</li>
     * <li>{@code confidenceLevel} must be strictly between 0 and 1 (exclusive)</li>
     * </ul>
     *
     * @param numberOfTrials       number of trials
     * @param probabilityOfSuccess observed probability of success
     * @param confidenceLevel      desired probability that the true probability of
     *                             success falls within the returned interval
     * @return Confidence interval containing the probability of success with
     * probability {@code confidenceLevel}
     * @throws MathIllegalArgumentException if {@code numberOfTrials <= 0}.
     * @throws MathIllegalArgumentException if {@code probabilityOfSuccess} is not in the interval [0, 1].
     * @throws MathIllegalArgumentException if {@code confidenceLevel} is not in the interval (0, 1).
     * @see <a href="http://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval#Agresti-Coull_Interval">
     * Agresti-Coull interval (Wikipedia)</a>
     */
    public static ConfidenceInterval getAgrestiCoullInterval(int numberOfTrials,
                                                             double probabilityOfSuccess,
                                                             double confidenceLevel)
            throws MathIllegalArgumentException {

        checkParameters(numberOfTrials, probabilityOfSuccess, confidenceLevel);

        final int numberOfSuccesses = (int) (numberOfTrials * probabilityOfSuccess);

        final double alpha = (1.0 - confidenceLevel) / 2;
        final double z = NORMAL_DISTRIBUTION.inverseCumulativeProbability(1 - alpha);
        final double zSquared = FastMath.pow(z, 2);
        final double modifiedNumberOfTrials = numberOfTrials + zSquared;
        final double modifiedSuccessesRatio = (1.0 / modifiedNumberOfTrials) *
                (numberOfSuccesses + 0.5 * zSquared);
        final double difference = z * FastMath.sqrt(1.0 / modifiedNumberOfTrials *
                modifiedSuccessesRatio *
                (1 - modifiedSuccessesRatio));

        return new ConfidenceInterval(modifiedSuccessesRatio - difference,
                modifiedSuccessesRatio + difference,
                confidenceLevel);
    }

    /**
     * Create a Clopper-Pearson binomial confidence interval for the true
     * probability of success of an unknown binomial distribution with
     * the given observed number of trials, probability of success and
     * confidence level.
     * <p>
     * Preconditions:
     * <ul>
     * <li>{@code numberOfTrials} must be positive</li>
     * <li>{@code probabilityOfSuccess} must be between 0 and 1 (inclusive)</li>
     * <li>{@code confidenceLevel} must be strictly between 0 and 1 (exclusive)</li>
     * </ul>
     *
     * @param numberOfTrials       number of trials
     * @param probabilityOfSuccess observed probability of success
     * @param confidenceLevel      desired probability that the true probability of
     *                             success falls within the returned interval
     * @return Confidence interval containing the probability of success with
     * probability {@code confidenceLevel}
     * @throws MathIllegalArgumentException if {@code numberOfTrials <= 0}.
     * @throws MathIllegalArgumentException if {@code probabilityOfSuccess} is not in the interval [0, 1].
     * @throws MathIllegalArgumentException if {@code confidenceLevel} is not in the interval (0, 1).
     * @see <a href="http://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval#Clopper-Pearson_interval">
     * Clopper-Pearson interval (Wikipedia)</a>
     */
    public static ConfidenceInterval getClopperPearsonInterval(int numberOfTrials,
                                                               double probabilityOfSuccess,
                                                               double confidenceLevel)
            throws MathIllegalArgumentException {

        checkParameters(numberOfTrials, probabilityOfSuccess, confidenceLevel);

        double lowerBound = 0;
        double upperBound = 0;
        final int numberOfSuccesses = (int) (numberOfTrials * probabilityOfSuccess);

        if (numberOfSuccesses > 0) {
            final double alpha = (1.0 - confidenceLevel) / 2.0;

            final FDistribution distributionLowerBound =
                    new FDistribution(2 * (numberOfTrials - numberOfSuccesses + 1),
                            2 * numberOfSuccesses);

            final double fValueLowerBound =
                    distributionLowerBound.inverseCumulativeProbability(1 - alpha);
            lowerBound = numberOfSuccesses /
                    (numberOfSuccesses + (numberOfTrials - numberOfSuccesses + 1) * fValueLowerBound);

            final FDistribution distributionUpperBound =
                    new FDistribution(2 * (numberOfSuccesses + 1),
                            2 * (numberOfTrials - numberOfSuccesses));

            final double fValueUpperBound =
                    distributionUpperBound.inverseCumulativeProbability(1 - alpha);
            upperBound = (numberOfSuccesses + 1) * fValueUpperBound /
                    (numberOfTrials - numberOfSuccesses + (numberOfSuccesses + 1) * fValueUpperBound);
        }

        return new ConfidenceInterval(lowerBound, upperBound, confidenceLevel);
    }

    /**
     * Create a binomial confidence interval using normal approximation
     * for the true probability of success of an unknown binomial distribution
     * with the given observed number of trials, probability of success and
     * confidence level.
     * <p>
     * Preconditions:
     * <ul>
     * <li>{@code numberOfTrials} must be positive</li>
     * <li>{@code probabilityOfSuccess} must be between 0 and 1 (inclusive)</li>
     * <li>{@code confidenceLevel} must be strictly between 0 and 1 (exclusive)</li>
     * </ul>
     *
     * @param numberOfTrials       number of trials
     * @param probabilityOfSuccess observed probability of success
     * @param confidenceLevel      desired probability that the true probability of
     *                             success falls within the returned interval
     * @return Confidence interval containing the probability of success with
     * probability {@code confidenceLevel}
     * @throws MathIllegalArgumentException if {@code numberOfTrials <= 0}.
     * @throws MathIllegalArgumentException if {@code probabilityOfSuccess} is not in the interval [0, 1].
     * @throws MathIllegalArgumentException if {@code confidenceLevel} is not in the interval (0, 1).
     * @see <a href="http://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval#Normal_approximation_interval">
     * Normal approximation interval (Wikipedia)</a>
     */
    public static ConfidenceInterval getNormalApproximationInterval(int numberOfTrials,
                                                                    double probabilityOfSuccess,
                                                                    double confidenceLevel)
            throws MathIllegalArgumentException {

        checkParameters(numberOfTrials, probabilityOfSuccess, confidenceLevel);

        final double mean = probabilityOfSuccess;
        final double alpha = (1.0 - confidenceLevel) / 2;

        final double difference = NORMAL_DISTRIBUTION.inverseCumulativeProbability(1 - alpha) *
                FastMath.sqrt(1.0 / numberOfTrials * mean * (1 - mean));
        return new ConfidenceInterval(mean - difference, mean + difference, confidenceLevel);
    }

    /**
     * Create an Wilson score binomial confidence interval for the true
     * probability of success of an unknown binomial distribution with
     * the given observed number of trials, probability of success and
     * confidence level.
     * <p>
     * Preconditions:
     * <ul>
     * <li>{@code numberOfTrials} must be positive</li>
     * <li>{@code probabilityOfSuccess} must be between 0 and 1 (inclusive)</li>
     * <li>{@code confidenceLevel} must be strictly between 0 and 1 (exclusive)</li>
     * </ul>
     *
     * @param numberOfTrials       number of trials
     * @param probabilityOfSuccess observed probability of success
     * @param confidenceLevel      desired probability that the true probability of
     *                             success falls within the returned interval
     * @return Confidence interval containing the probability of success with
     * probability {@code confidenceLevel}
     * @throws MathIllegalArgumentException if {@code numberOfTrials <= 0}.
     * @throws MathIllegalArgumentException if {@code probabilityOfSuccess} is not in the interval [0, 1].
     * @throws MathIllegalArgumentException if {@code confidenceLevel} is not in the interval (0, 1).
     * @see <a href="http://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval#Wilson_score_interval">
     * Wilson score interval (Wikipedia)</a>
     */
    public static ConfidenceInterval getWilsonScoreInterval(int numberOfTrials,
                                                            double probabilityOfSuccess,
                                                            double confidenceLevel)
            throws MathIllegalArgumentException {

        checkParameters(numberOfTrials, probabilityOfSuccess, confidenceLevel);

        final double alpha = (1.0 - confidenceLevel) / 2;
        final double z = NORMAL_DISTRIBUTION.inverseCumulativeProbability(1 - alpha);
        final double zSquared = FastMath.pow(z, 2);
        final double mean = probabilityOfSuccess;

        final double factor = 1.0 / (1 + (1.0 / numberOfTrials) * zSquared);
        final double modifiedSuccessRatio = mean + (1.0 / (2 * numberOfTrials)) * zSquared;
        final double difference =
                z * FastMath.sqrt(1.0 / numberOfTrials * mean * (1 - mean) +
                        (1.0 / (4 * FastMath.pow(numberOfTrials, 2)) * zSquared));

        final double lowerBound = factor * (modifiedSuccessRatio - difference);
        final double upperBound = factor * (modifiedSuccessRatio + difference);
        return new ConfidenceInterval(lowerBound, upperBound, confidenceLevel);
    }

    /**
     * Verifies that parameters satisfy preconditions.
     *
     * @param numberOfTrials       number of trials (must be positive)
     * @param probabilityOfSuccess probability of successes (must be between 0 and 1)
     * @param confidenceLevel      confidence level (must be strictly between 0 and 1)
     * @throws MathIllegalArgumentException if {@code numberOfTrials <= 0}.
     * @throws MathIllegalArgumentException if {@code probabilityOfSuccess is not in the interval [0, 1]}.
     * @throws MathIllegalArgumentException if {@code confidenceLevel} is not in the interval (0, 1)}.
     */
    private static void checkParameters(int numberOfTrials,
                                        double probabilityOfSuccess,
                                        double confidenceLevel) {
        if (numberOfTrials <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_OF_TRIALS,
                    numberOfTrials);
        }
        MathUtils.checkRangeInclusive(probabilityOfSuccess, 0, 1);
        if (confidenceLevel <= 0 || confidenceLevel >= 1) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.OUT_OF_BOUNDS_CONFIDENCE_LEVEL,
                    confidenceLevel, 0, 1);
        }
    }

}
