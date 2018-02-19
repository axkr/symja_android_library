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
package org.hipparchus.distribution.multivariate;

import org.hipparchus.distribution.MultivariateRealDistribution;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.random.RandomGenerator;
import org.hipparchus.random.Well19937c;
import org.hipparchus.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for representing <a href="http://en.wikipedia.org/wiki/Mixture_model">
 * mixture model</a> distributions.
 *
 * @param <T> Type of the mixture components.
 */
public class MixtureMultivariateRealDistribution<T extends MultivariateRealDistribution>
        extends AbstractMultivariateRealDistribution {
    /**
     * Normalized weight of each mixture component.
     */
    private final double[] weight;
    /**
     * Mixture components.
     */
    private final List<T> distribution;

    /**
     * Creates a mixture model from a list of distributions and their
     * associated weights.
     * <p>
     * <b>Note:</b> this constructor will implicitly create an instance of
     * {@link Well19937c} as random generator to be used for sampling only (see
     * {@link #sample()} and {@link #sample(int)}). In case no sampling is
     * needed for the created distribution, it is advised to pass {@code null}
     * as random generator via the appropriate constructors to avoid the
     * additional initialisation overhead.
     *
     * @param components List of (weight, distribution) pairs from which to sample.
     */
    public MixtureMultivariateRealDistribution(List<Pair<Double, T>> components) {
        this(new Well19937c(), components);
    }

    /**
     * Creates a mixture model from a list of distributions and their
     * associated weights.
     *
     * @param rng        Random number generator.
     * @param components Distributions from which to sample.
     * @throws MathIllegalArgumentException if any of the weights is negative.
     * @throws MathIllegalArgumentException if not all components have the same
     *                                      number of variables.
     */
    public MixtureMultivariateRealDistribution(RandomGenerator rng,
                                               List<Pair<Double, T>> components) {
        super(rng, components.get(0).getSecond().getDimension());

        final int numComp = components.size();
        final int dim = getDimension();
        double weightSum = 0;
        for (int i = 0; i < numComp; i++) {
            final Pair<Double, T> comp = components.get(i);
            if (comp.getSecond().getDimension() != dim) {
                throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                        comp.getSecond().getDimension(), dim);
            }
            if (comp.getFirst() < 0) {
                throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_SMALL, comp.getFirst(), 0);
            }
            weightSum += comp.getFirst();
        }

        // Check for overflow.
        if (Double.isInfinite(weightSum)) {
            throw new MathRuntimeException(LocalizedCoreFormats.OVERFLOW);
        }

        // Store each distribution and its normalized weight.
        distribution = new ArrayList<T>();
        weight = new double[numComp];
        for (int i = 0; i < numComp; i++) {
            final Pair<Double, T> comp = components.get(i);
            weight[i] = comp.getFirst() / weightSum;
            distribution.add(comp.getSecond());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double density(final double[] values) {
        double p = 0;
        for (int i = 0; i < weight.length; i++) {
            p += weight[i] * distribution.get(i).density(values);
        }
        return p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] sample() {
        // Sampled values.
        double[] vals = null;

        // Determine which component to sample from.
        final double randomValue = random.nextDouble();
        double sum = 0;

        for (int i = 0; i < weight.length; i++) {
            sum += weight[i];
            if (randomValue <= sum) {
                // pick model i
                vals = distribution.get(i).sample();
                break;
            }
        }

        if (vals == null) {
            // This should never happen, but it ensures we won't return a null in
            // case the loop above has some floating point inequality problem on
            // the final iteration.
            vals = distribution.get(weight.length - 1).sample();
        }

        return vals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reseedRandomGenerator(long seed) {
        // Seed needs to be propagated to underlying components
        // in order to maintain consistency between runs.
        super.reseedRandomGenerator(seed);

        for (int i = 0; i < distribution.size(); i++) {
            // Make each component's seed different in order to avoid
            // using the same sequence of random numbers.
            distribution.get(i).reseedRandomGenerator(i + 1 + seed);
        }
    }

    /**
     * Gets the distributions that make up the mixture model.
     *
     * @return the component distributions and associated weights.
     */
    public List<Pair<Double, T>> getComponents() {
        final List<Pair<Double, T>> list = new ArrayList<Pair<Double, T>>(weight.length);

        for (int i = 0; i < weight.length; i++) {
            list.add(new Pair<Double, T>(weight[i], distribution.get(i)));
        }

        return list;
    }
}
