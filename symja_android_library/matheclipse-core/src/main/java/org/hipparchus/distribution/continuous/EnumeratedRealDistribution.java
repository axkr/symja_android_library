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

import org.hipparchus.distribution.EnumeratedDistribution;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.util.MathArrays;
import org.hipparchus.util.MathUtils;
import org.hipparchus.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Implementation of a real-valued {@link EnumeratedDistribution}.
 * <p>
 * Values with zero-probability are allowed but they do not extend the
 * support.
 * <p>
 * Duplicate values are allowed. Probabilities of duplicate values are
 * combined when computing cumulative probabilities and statistics.
 */
public class EnumeratedRealDistribution extends AbstractRealDistribution {

    /**
     * Serializable UID.
     */
    private static final long serialVersionUID = 20130308L;

    /**
     * {@link EnumeratedDistribution} (using the {@link Double} wrapper)
     * used to generate the pmf.
     */
    private final EnumeratedDistribution<Double> innerDistribution;

    /**
     * Create a discrete real-valued distribution from the input data.  Values are assigned
     * mass based on their frequency.  For example, [0,1,1,2] as input creates a distribution
     * with values 0, 1 and 2 having probability masses 0.25, 0.5 and 0.25 respectively,
     *
     * @param data input dataset
     */
    public EnumeratedRealDistribution(final double[] data) {
        super();
        final Map<Double, Integer> dataMap = new HashMap<>();
        for (double value : data) {
            Integer count = dataMap.get(value);
            if (count == null) {
                count = 0;
            }
            dataMap.put(value, ++count);
        }
        final int massPoints = dataMap.size();
        final double denom = data.length;
        final double[] values = new double[massPoints];
        final double[] probabilities = new double[massPoints];
        int index = 0;
        for (Entry<Double, Integer> entry : dataMap.entrySet()) {
            values[index] = entry.getKey();
            probabilities[index] = entry.getValue().intValue() / denom;
            index++;
        }
        innerDistribution =
                new EnumeratedDistribution<Double>(createDistribution(values, probabilities));
    }

    /**
     * Create a discrete real-valued distribution using the given probability mass function
     * enumeration.
     *
     * @param singletons    array of random variable values.
     * @param probabilities array of probabilities.
     * @throws MathIllegalArgumentException if
     *                                      {@code singletons.length != probabilities.length}
     * @throws MathIllegalArgumentException if any of the probabilities are negative.
     * @throws MathIllegalArgumentException if any of the probabilities are NaN.
     * @throws MathIllegalArgumentException if any of the probabilities are infinite.
     */
    public EnumeratedRealDistribution(final double[] singletons, final double[] probabilities)
            throws MathIllegalArgumentException {
        super();
        innerDistribution =
                new EnumeratedDistribution<Double>(createDistribution(singletons, probabilities));
    }


    /**
     * Create the list of Pairs representing the distribution from singletons and probabilities.
     *
     * @param singletons    values
     * @param probabilities probabilities
     * @return list of value/probability pairs
     * @throws MathIllegalArgumentException if probabilities contains negative, infinite or NaN values or only 0's
     */
    private static List<Pair<Double, Double>> createDistribution(double[] singletons,
                                                                 double[] probabilities) {
        MathArrays.checkEqualLength(singletons, probabilities);
        final List<Pair<Double, Double>> samples = new ArrayList<>(singletons.length);

        final double[] normalizedProbabilities = EnumeratedDistribution.checkAndNormalize(probabilities);
        for (int i = 0; i < singletons.length; i++) {
            samples.add(new Pair<>(singletons[i], normalizedProbabilities[i]));
        }
        return samples;
    }

    /**
     * For a random variable {@code X} whose values are distributed according to
     * this distribution, this method returns {@code P(X = x)}. In other words,
     * this method represents the probability mass function (PMF) for the
     * distribution.
     * <p>
     * Note that if {@code x1} and {@code x2} satisfy {@code x1.equals(x2)},
     * or both are null, then {@code probability(x1) = probability(x2)}.
     *
     * @param x the point at which the PMF is evaluated
     * @return the value of the probability mass function at {@code x}
     */
    public double probability(final double x) {
        return innerDistribution.probability(x);
    }

    /**
     * For a random variable {@code X} whose values are distributed according to
     * this distribution, this method returns {@code P(X = x)}. In other words,
     * this method represents the probability mass function (PMF) for the
     * distribution.
     *
     * @param x the point at which the PMF is evaluated
     * @return the value of the probability mass function at point {@code x}
     */
    @Override
    public double density(final double x) {
        return probability(x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(final double x) {
        double probability = 0;

        for (final Pair<Double, Double> sample : innerDistribution.getPmf()) {
            if (sample.getKey() <= x) {
                probability += sample.getValue();
            }
        }

        return probability;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double inverseCumulativeProbability(final double p) throws MathIllegalArgumentException {
        MathUtils.checkRangeInclusive(p, 0, 1);

        double probability = 0;
        double x = getSupportLowerBound();
        for (final Pair<Double, Double> sample : innerDistribution.getPmf()) {
            if (sample.getValue() == 0.0) {
                continue;
            }

            probability += sample.getValue();
            x = sample.getKey();

            if (probability >= p) {
                break;
            }
        }

        return x;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code sum(singletons[i] * probabilities[i])}
     */
    @Override
    public double getNumericalMean() {
        double mean = 0;

        for (final Pair<Double, Double> sample : innerDistribution.getPmf()) {
            mean += sample.getValue() * sample.getKey();
        }

        return mean;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code sum((singletons[i] - mean) ^ 2 * probabilities[i])}
     */
    @Override
    public double getNumericalVariance() {
        double mean = 0;
        double meanOfSquares = 0;

        for (final Pair<Double, Double> sample : innerDistribution.getPmf()) {
            mean += sample.getValue() * sample.getKey();
            meanOfSquares += sample.getValue() * sample.getKey() * sample.getKey();
        }

        return meanOfSquares - mean * mean;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the lowest value with non-zero probability.
     *
     * @return the lowest value with non-zero probability.
     */
    @Override
    public double getSupportLowerBound() {
        double min = Double.POSITIVE_INFINITY;
        for (final Pair<Double, Double> sample : innerDistribution.getPmf()) {
            if (sample.getKey() < min && sample.getValue() > 0) {
                min = sample.getKey();
            }
        }

        return min;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the highest value with non-zero probability.
     *
     * @return the highest value with non-zero probability.
     */
    @Override
    public double getSupportUpperBound() {
        double max = Double.NEGATIVE_INFINITY;
        for (final Pair<Double, Double> sample : innerDistribution.getPmf()) {
            if (sample.getKey() > max && sample.getValue() > 0) {
                max = sample.getKey();
            }
        }

        return max;
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
     * Return the probability mass function as a list of <value, probability> pairs.
     *
     * @return the probability mass function.
     */
    public List<Pair<Double, Double>> getPmf() {
        return innerDistribution.getPmf();
    }
}
