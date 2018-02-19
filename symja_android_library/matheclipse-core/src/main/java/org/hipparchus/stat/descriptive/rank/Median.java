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
package org.hipparchus.stat.descriptive.rank;

import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.NullArgumentException;
import org.hipparchus.stat.descriptive.AbstractUnivariateStatistic;
import org.hipparchus.stat.descriptive.rank.Percentile.EstimationType;
import org.hipparchus.stat.ranking.NaNStrategy;
import org.hipparchus.util.KthSelector;

import java.io.Serializable;


/**
 * Returns the median of the available values.  This is the same as the 50th percentile.
 * See {@link Percentile} for a description of the algorithm used.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.
 */
public class Median extends AbstractUnivariateStatistic implements Serializable {

    /**
     * Serializable version identifier
     */
    private static final long serialVersionUID = 20150412L;

    /**
     * Fixed quantile.
     */
    private static final double FIXED_QUANTILE_50 = 50.0;

    /**
     * The percentile impl to calculate the median.
     */
    private final Percentile percentile;

    /**
     * Default constructor.
     */
    public Median() {
        percentile = new Percentile(FIXED_QUANTILE_50);
    }

    /**
     * Constructs a Median with the specific {@link EstimationType},
     * {@link NaNStrategy} and {@link KthSelector}.
     *
     * @param estimationType one of the percentile {@link EstimationType estimation types}
     * @param nanStrategy    one of {@link NaNStrategy} to handle with NaNs
     * @param kthSelector    {@link KthSelector} to use for pivoting during search
     * @throws MathIllegalArgumentException if p is not within (0,100]
     * @throws NullArgumentException        if type or NaNStrategy passed is null
     */
    private Median(final EstimationType estimationType, final NaNStrategy nanStrategy,
                   final KthSelector kthSelector)
            throws MathIllegalArgumentException {

        percentile = new Percentile(FIXED_QUANTILE_50, estimationType,
                nanStrategy, kthSelector);
    }

    /**
     * Copy constructor, creates a new {@code Median} identical
     * to the {@code original}
     *
     * @param original the {@code Median} instance to copy
     * @throws NullArgumentException if original is null
     */
    Median(Median original) throws NullArgumentException {
        super(original);
        this.percentile = original.percentile.copy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double evaluate(double[] values, int begin, int length)
            throws MathIllegalArgumentException {
        return percentile.evaluate(values, begin, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Median copy() {
        return new Median(this);
    }

    /**
     * Get the estimation {@link EstimationType type} used for computation.
     *
     * @return the {@code estimationType} set
     */
    public EstimationType getEstimationType() {
        return percentile.getEstimationType();
    }

    /**
     * Build a new instance similar to the current one except for the
     * {@link EstimationType estimation type}.
     *
     * @param newEstimationType estimation type for the new instance
     * @return a new instance, with changed estimation type
     * @throws NullArgumentException when newEstimationType is null
     */
    public Median withEstimationType(final EstimationType newEstimationType) {
        return new Median(newEstimationType,
                percentile.getNaNStrategy(),
                percentile.getKthSelector());
    }

    /**
     * Get the {@link NaNStrategy NaN Handling} strategy used for computation.
     *
     * @return {@code NaN Handling} strategy set during construction
     */
    public NaNStrategy getNaNStrategy() {
        return percentile.getNaNStrategy();
    }

    /**
     * Build a new instance similar to the current one except for the
     * {@link NaNStrategy NaN handling} strategy.
     *
     * @param newNaNStrategy NaN strategy for the new instance
     * @return a new instance, with changed NaN handling strategy
     * @throws NullArgumentException when newNaNStrategy is null
     */
    public Median withNaNStrategy(final NaNStrategy newNaNStrategy) {
        return new Median(percentile.getEstimationType(),
                newNaNStrategy,
                percentile.getKthSelector());
    }

    /**
     * Get the {@link KthSelector kthSelector} used for computation.
     *
     * @return the {@code kthSelector} set
     */
    public KthSelector getKthSelector() {
        return percentile.getKthSelector();
    }

    /**
     * Build a new instance similar to the current one except for the
     * {@link KthSelector kthSelector} instance specifically set.
     *
     * @param newKthSelector KthSelector for the new instance
     * @return a new instance, with changed KthSelector
     * @throws NullArgumentException when newKthSelector is null
     */
    public Median withKthSelector(final KthSelector newKthSelector) {
        return new Median(percentile.getEstimationType(),
                percentile.getNaNStrategy(),
                newKthSelector);
    }

}
